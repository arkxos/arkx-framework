/*
 *  Copyright 2019-2021 RapidArk
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.arkx.framework.common.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.arkx.framework.commons.exception.BadRequestException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * File工具类，扩展 hutool 工具包
 * @author darkness
 * @version 1.0
 * @date 2021/9/12 20:15
 */
public class FileUtil extends cn.hutool.core.io.FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 系统临时目录
     * <br>
     * windows 包含路径分割符，但Linux 不包含,
     * 在windows \\==\ 前提下，
     * 为安全起见 同意拼装 路径分割符，
     * <pre>
     *       java.io.tmpdir
     *       windows : C:\Users/xxx\AppData\Local\Temp\
     *       linux: /temp
     * </pre>
     */
    public static final String SYS_TEM_DIR = System.getProperty("java.io.tmpdir") + File.separator;
    /**
     * 定义GB的计算常量
     */
    private static final int GB = 1024 * 1024 * 1024;
    /**
     * 定义MB的计算常量
     */
    private static final int MB = 1024 * 1024;
    /**
     * 定义KB的计算常量
     */
    private static final int KB = 1024;

    /**
     * 格式化小数
     */
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    public static final String IMAGE = "图片";
    public static final String TXT = "文档";
    public static final String MUSIC = "音乐";
    public static final String VIDEO = "视频";
    public static final String OTHER = "其他";


    /**
     * MultipartFile转File
     */
    public static File toFile(MultipartFile multipartFile) {
        // 获取文件名
        String fileName = multipartFile.getOriginalFilename();
        // 获取文件后缀
        String prefix = "." + getExtensionName(fileName);
        File file = null;
        try {
            // 用uuid作为文件名，防止生成的临时文件重复
            file = new File(SYS_TEM_DIR + IdUtil.simpleUUID() + prefix);
            // MultipartFile to File
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return file;
    }

    /**
     * 获取文件扩展名，不带 .
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * Java文件操作 获取不带扩展名的文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 文件大小转换
     */
    public static String getSize(long size) {
        String resultSize;
        if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            resultSize = DF.format(size / (float) GB) + "GB   ";
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            resultSize = DF.format(size / (float) MB) + "MB   ";
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            resultSize = DF.format(size / (float) KB) + "KB   ";
        } else {
            resultSize = size + "B   ";
        }
        return resultSize;
    }

    /**
     * inputStream 转 File
     */
    public static File inputStreamToFile(InputStream ins, String name){
        File file = new File(SYS_TEM_DIR + name);
        if (file.exists()) {
            return file;
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int bytesRead;
            int len = 8192;
            byte[] buffer = new byte[len];
            while ((bytesRead = ins.read(buffer, 0, len)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(os);
            CloseUtil.close(ins);
        }
        return file;
    }

    /**
     * 将文件名解析成文件的上传路径
     */
    public static File upload(MultipartFile file, String filePath) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmssS");
        String name = getFileNameNoEx(file.getOriginalFilename());
        String suffix = getExtensionName(file.getOriginalFilename());
        String nowStr = "-" + format.format(date);
        try {
            String fileName = name + nowStr + "." + suffix;
            String path = filePath + fileName;
            // getCanonicalFile 可解析正确各种路径
            File dest = new File(path).getCanonicalFile();
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                if (!dest.getParentFile().mkdirs()) {
                    System.out.println("was not successful.");
                }
            }
            // 文件写入
            file.transferTo(dest);
            return dest;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 导出excel
     */
    public static void downloadExcel(List<Map<String, Object>> list, HttpServletResponse response) throws IOException {
        String tempPath = SYS_TEM_DIR + IdUtil.fastSimpleUUID() + ".xlsx";
        File file = new File(tempPath);
        BigExcelWriter writer = ExcelUtil.getBigWriter(file);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list, true);
        SXSSFSheet sheet = (SXSSFSheet)writer.getSheet();
        //上面需要强转SXSSFSheet  不然没有trackAllColumnsForAutoSizing方法
        sheet.trackAllColumnsForAutoSizing();
        //列宽自适应
        writer.autoSizeColumnAll();
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=file.xlsx");
        ServletOutputStream out = response.getOutputStream();
        // 终止后删除临时文件
        file.deleteOnExit();
        writer.flush(out, true);
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    public static String getFileType(String type) {
        String documents = "txt doc pdf ppt pps xlsx xls docx";
        String music = "mp3 wav wma mpa ram ra aac aif m4a";
        String video = "avi mpg mpe mpeg asf wmv mov qt rm mp4 flv m4v webm ogv ogg";
        String image = "bmp dib pcp dif wmf gif jpg tif eps psd cdr iff tga pcd mpt png jpeg";
        if (image.contains(type)) {
            return IMAGE;
        } else if (documents.contains(type)) {
            return TXT;
        } else if (music.contains(type)) {
            return MUSIC;
        } else if (video.contains(type)) {
            return VIDEO;
        } else {
            return OTHER;
        }
    }

    public static void checkSize(long maxSize, long size) {
        // 1M
        int len = 1024 * 1024;
        if (size > (maxSize * len)) {
            throw new BadRequestException("文件超出规定大小");
        }
    }

    /**
     * 判断两个文件是否相同
     */
    public static boolean check(File file1, File file2) {
        String img1Md5 = getMd5(file1);
        String img2Md5 = getMd5(file2);
        if(img1Md5 != null){
            return img1Md5.equals(img2Md5);
        }
        return false;
    }

    /**
     * 判断两个文件是否相同
     */
    public static boolean check(String file1Md5, String file2Md5) {
        return file1Md5.equals(file2Md5);
    }

    private static byte[] getByte(File file) {
        // 得到文件长度
        byte[] b = new byte[(int) file.length()];
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            try {
                System.out.println(in.read(b));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            CloseUtil.close(in);
        }
        return b;
    }

    private static String getMd5(byte[] bytes) {
        // 16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(bytes);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            // 移位 输出字符串
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 下载文件
     *
     * @param request  /
     * @param response /
     * @param file     /
     */
    public static void downloadFile(HttpServletRequest request, HttpServletResponse response, File file, boolean deleteOnExit) {
        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setContentType("application/octet-stream");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            IOUtils.copy(fis, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    if (deleteOnExit) {
                        file.deleteOnExit();
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public static String getMd5(File file) {
        return getMd5(getByte(file));
    }

    /**
     * 删除文件，不管路径是文件还是文件夹，都删掉。<br>
     * 删除文件夹时会自动删除子文件夹。
     */
    public static boolean delete(String path) {
        File file = new File(path);
        return delete(file);
    }

    /**
     * 删除文件，不管路径是文件还是文件夹，都删掉。<br>
     * 删除文件夹时会自动删除子文件夹。
     */
    public static boolean delete(File f) {
        if (!f.exists()) {
            //LogUtil.warn("File or directory not found " + f);
            return true;
        }
        if (f.isFile()) {
            return f.delete();
        } else {
            return deleteDir(f);
        }
    }

    /**
     * 删除文件夹及其子文件夹
     */
    private static boolean deleteDir(File dir) {
        try {
            return deleteFromDir(dir) && dir.delete(); // 先删除完里面所有内容再删除空文件夹
        } catch (Exception e) {
//			LogUtil.warn("Delete directory failed");
            // e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件夹里面的所有文件和子文件夹,但不删除自己本身
     *
     * @param dir
     * @return
     */
    public static boolean deleteFromDir(File dir) {
        if (!dir.exists()) {
//			LogUtil.warn("Directory not found：" + dir);
            return false;
        }
        if (!dir.isDirectory()) {
//			LogUtil.warn(dir + " is not directory");
            return false;
        }
        File[] tempList = dir.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (!delete(tempList[i])) {
                return false;
            }
        }
        return true;
    }


    /**
     * 以全局编码读取指定文件中的文本
     */
    public static String readText(File f) {
        return readText(f, "utf-8");
    }

    /**
     * 以指定编码读取指定文件中的文本
     */
    public static String readText(File f, String encoding) {
        InputStream is = null;
        try {
            is = new FileInputStream(f);
            String str = readText(is, encoding);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 以指定编码读取流中的文本
     */
    public static String readText(InputStream is, String encoding) {
        try {
            byte[] bs = readByte(is);
            if (encoding.equalsIgnoreCase("utf-8")) {// 如果是UTF8则要判断有没有BOM
                if (HexUtil.hexEncode(ArrayUtils.subarray(bs, 0, 3)).equals("efbbbf")) {// BOM标志
                    bs = ArrayUtils.subarray(bs, 3, bs.length);
                }
            }
            return new String(bs, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 以全局编码读取指定文件中的文本
     */
    public static String readText(String fileName) {
        //CX控制license文件的读取位置
//		if (fileName.endsWith("classes/rapidark.license")) {
//			String licensePath = LicensePath.getReadValue();
//			if (licensePath != null) {
//				fileName = licensePath;
//			}
//		}
        return readText(fileName, "utf-8");
    }

    /**
     * 以指定编码读取指定文件中的文本
     */
    public static String readText(String fileName, String encoding) {
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
            String str = readText(is, encoding);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    /**
     * 以二进制方式读取文件
     */
    public static byte[] readByte(String fileName) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
            byte[] r = new byte[fis.available()];
            fis.read(r);
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 以二进制方式读取文件
     */
    public static byte[] readByte(File f) {
        FileInputStream fis = null;
        try {

            fis = new FileInputStream(f);
            byte[] r = readByte(fis);
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 读取指定流，并转换为二进制数组
     */
    public static byte[] readByte(InputStream is) {
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        while (true) {
            int bytesRead = -1;
            try {
                bytesRead = is.read(buffer);
            } catch (IOException e) {
                throw new RuntimeException("File.readByte() failed");
            }
            if (bytesRead == -1) {
                break;
            }
            try {
                os.write(buffer, 0, bytesRead);
            } catch (Exception e) {
                throw new RuntimeException("File.readByte() failed");
            }
        }
        return os.toByteArray();
    }

    /**
     * 以全局编码将指定内容写入指定文件
     */
    public static boolean writeText(String fileName, String content) {
//		if (CheckContentBeforeWriteFlag.checkFlag()) {
        File file = new File(fileName);
        if (file.exists()) {
            String txt = readText(file);
            if (txt != null && txt.equals(content)) {
                //LogUtil.info("=======内容相同,不更新文件：" + fileName);
                return false;
            }
        }
//		}
        return writeText(fileName, content, getGlobalCharset());
    }

    private static String getGlobalCharset() {
        return "utf-8";
    }

    public static boolean writeText(String fileName, String content, boolean append) {
        //fileName = normalizePath(fileName);
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, append);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 以指定编码将指定内容写入指定文件
     */
    public static boolean writeText(String fileName, String content, String encoding) {
        return writeTextWithBom(fileName, content, encoding, false);
    }

    /**
     * 以指定编码将指定内容写入指定文件，如果编码为UTF-8且bomFlag为true,则在文件头部加入3字节的BOM
     */
    public static boolean writeTextWithBom(String fileName, String content, String encoding, boolean bomFlag) {

        try {
            byte[] bs = content.getBytes(encoding);
            if (encoding.equalsIgnoreCase("UTF-8") && bomFlag) {
                bs = ArrayUtils.addAll(HexUtil.BOM, bs);
            }
            writeByte(fileName, bs);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 将二进制数组写入指定文件
     */
    public static boolean writeByte(String fileName, byte[] b) {
        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(fileName));
            os.write(b);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
