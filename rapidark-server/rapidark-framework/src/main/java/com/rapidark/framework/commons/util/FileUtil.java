package com.rapidark.framework.commons.util;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/9/12 20:15
 */
public class FileUtil {

    /**
     * 创建文件夹
     */
    public static boolean mkdir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return true;
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
            return FileUtil.deleteDir(f);
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
                if (StringUtil.hexEncode(ArrayUtils.subarray(bs, 0, 3)).equals("efbbbf")) {// BOM标志
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
                bs = ArrayUtils.addAll(StringUtil.BOM, bs);
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
