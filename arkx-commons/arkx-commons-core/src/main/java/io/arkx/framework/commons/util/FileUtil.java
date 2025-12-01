package io.arkx.framework.commons.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import com.google.common.base.Joiner;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.exception.BadRequestException;
import io.arkx.framework.thirdparty.commons.ArrayUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * 文件操作工具类
 * @author darkness
 * @version 1.0
 * @date 2021/9/12 20:15
 */
public class FileUtil {

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
	 * > buildPath(SystemInfo.userHome(), "icequeen", "quote", "test.txt")
	 * >　C:\Users\Administrator\icequeen\quote\test.txt
	 * @param paths
	 * @return
	 */
	public static String buildPath(String... paths) {
		return Joiner.on(File.separator).join(paths);
	}
	
	public static String safePath(String... paths) {
		String path = buildPath(paths);
		File file = new File(path);
		
		String folder = path;
		if (file.isFile()) {
			folder = file.getParent();
		}
		
		mkdir(folder);
		
		return path;
	}
	
	/**
	 * 将文件路径规则化，去掉其中多余的/和\，去掉可能造成文件信息泄漏的../
	 */
	public static String normalizePath(String path) {
		path = path.replace('\\', '/');
		path = StringUtil.replaceEx(path, "../", "/");
		path = StringUtil.replaceEx(path, "./", "/");
		if (path.endsWith("..")) {
			path = path.substring(0, path.length() - 2);
		}
		path = path.replaceAll("/+", "/");
		return path;
	}

	public static File normalizeFile(File f) {
		String path = f.getAbsolutePath();
		path = normalizePath(path);
		return new File(path);
	}

	/**
	 * 得到文件名中的扩展名，不带圆点。
	 */
	public static String getExtension(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index < 0) {
			return null;
		}
		String ext = fileName.substring(index + 1);
		return ext.toLowerCase();
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
	 * 以全局编码将指定内容写入指定文件
	 */
	public static boolean writeTextWithCheck(String fileName, String content, boolean checkContent) {
		if (checkContent) {
			File file = new File(fileName);
			if (file.exists()) {
				String txt = readText(file);
				if (txt != null && txt.equals(content)) {
					//LogUtil.info("内容相同,不更新文件：" + fileName);
					return false;
				}
			}
		}
		return writeText(fileName, content, getGlobalCharset());
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
		fileName = normalizePath(fileName);
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
	 * 以二进制方式读取文件
	 */
	public static byte[] readByte(String fileName) {
		fileName = normalizePath(fileName);
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
		f = normalizeFile(f);
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
		if(is == null) {
			return null;
		}
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
	 * 将二进制数组写入指定文件
	 */
	public static boolean writeByte(String fileName, byte[] b) {
		fileName = normalizePath(fileName);
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

	/**
	 * 将二进制数组写入指定文件
	 */
	public static boolean writeByte(File f, byte[] b) {
		f = normalizeFile(f);
		BufferedOutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(f));
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

	/**
	 * 以全局编码读取指定文件中的文本
	 */
	public static String readText(File f) {
		return readText(f, getGlobalCharset());
	}

	/**
	 * 以指定编码读取指定文件中的文本
	 */
	public static String readText(File f, String encoding) {
		f = normalizeFile(f);
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
		return readText(fileName, getGlobalCharset());
	}

	/**
	 * 以指定编码读取指定文件中的文本
	 */
	public static String readText(String fileName, String encoding) {
		fileName = normalizePath(fileName);
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
	 * 以全局编码读取指定URL中的文本
	 */
	public static String readURLText(String urlPath) {
		return readURLText(urlPath, getGlobalCharset());
	}

	/**
	 * 以指定编码读取指定URL中的文本
	 */
	public static String readURLText(String urlPath, String encoding) {
		BufferedReader in = null;
		try {
			URL url = new URL(urlPath);
			in = new BufferedReader(new InputStreamReader(url.openStream(), encoding));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = in.readLine()) != null) {
				sb.append(line + "\n");
			}

			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
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
		f = normalizeFile(f);
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
		dir = normalizeFile(dir);
		try {
			return deleteFromDir(dir) && dir.delete(); // 先删除完里面所有内容再删除空文件夹
		} catch (Exception e) {
//			LogUtil.warn("Delete directory failed");
			// e.printStackTrace();
			return false;
		}
	}
	
	public static boolean deleteEx(String fileName) {
		fileName = normalizePath(fileName);
		int index1 = fileName.lastIndexOf("\\");
		int index2 = fileName.lastIndexOf("/");
		index1 = index1 > index2 ? index1 : index2;
		String path = fileName.substring(0, index1);
		String name = fileName.substring(index1 + 1);
		File f = new File(path);
		if ((f.exists()) && (f.isDirectory())) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (Pattern.matches(name, files[i].getName())) {
					files[i].delete();
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 创建文件夹
	 */
	public static boolean mkdir(String path) {
		path = normalizePath(path);
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return true;
	}

	/**
	 * 判断文件或文件夹是否存在
	 */
	public static boolean exists(String path) {
		path = normalizePath(path);
		File dir = new File(path);
		return dir.exists();
	}

	/**
	 * 删除文件夹里面的所有文件,但不删除自己本身
	 */
	public static boolean deleteFromDir(String dirPath) {
		dirPath = normalizePath(dirPath);
		File file = new File(dirPath);
		return deleteFromDir(file);
	}

	/**
	 * 删除文件夹里面的所有文件和子文件夹,但不删除自己本身
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteFromDir(File dir) {
		dir = normalizeFile(dir);
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
	 * 从指定位置复制文件到另一个文件夹，复制时不符合filter条件的不复制
	 */
	public static boolean copy(String oldPath, String newPath, FileFilter filter) {
		oldPath = normalizePath(oldPath);
		newPath = normalizePath(newPath);
		File oldFile = new File(oldPath);
		File[] oldFiles = oldFile.listFiles(filter);
		boolean flag = true;
		if (oldFiles != null) {
			for (int i = 0; i < oldFiles.length; i++) {
				if (!copy(oldFiles[i], newPath + "/" + oldFiles[i].getName())) {
					flag = false;
				}
			}
		}
		return flag;
	}

	/**
	 * 从指定位置复制文件到另一个文件夹
	 */
	public static boolean copy(String oldPath, String newPath) {
		File oldFile = new File(oldPath);
		return copy(oldFile, newPath);
	}

	public static boolean copy(File oldFile, String newPath) {
		oldFile = normalizeFile(oldFile);
		if (!oldFile.exists()) {
//			LogUtil.warn("File not found:" + oldFile);
			return false;
		}
		if (StringUtil.isEmpty(newPath)) {
//			LogUtil.info("Destintion path:" + newPath + " cannot be empty!");
			return false;
		}
		if (oldFile.isFile()) {
			return copyFile(oldFile, newPath);
		} else {
			// 判断newPath是否为oldFile的子路径，进行特殊处理
			if (normalizePath(newPath).startsWith(normalizePath(oldFile.getAbsolutePath()))) {
				return renameToSubDir(normalizePath(oldFile.getAbsolutePath()), newPath, false);
			} else {
				return copyDir(oldFile, newPath);
			}
		}
	}

	/**
	 * 将指定路径的文件复制到输出流中
	 * 
	 * @param filePath 要复制的文件路径
	 * @param outputStream 输出流
	 * @param close 是否关闭输出流
	 * @param buffer 缓冲区
	 * @return 复制字节长度
	 * @throws IOException
	 */
	public static long copy(String filePath, OutputStream outputStream, boolean close, byte[] buffer) throws IOException {
		FileInputStream inputStream = new FileInputStream(filePath);
		return copy(inputStream, outputStream, close, buffer);
	}

	/**
	 * 将指定的文件复制到输出流中
	 * 
	 * @param file 要复制的文件
	 * @param outputStream 输出流
	 * @param close 是否关闭输出流
	 * @param buffer 缓冲区
	 * @return 复制的字节长度
	 * @throws IOException
	 */
	public static long copy(File file, OutputStream outputStream, boolean close, byte[] buffer) throws IOException {
		FileInputStream inputStream = new FileInputStream(file);
		return copy(inputStream, outputStream, close, buffer);
	}

	/**
	 * 从输入流将内容写入输出流
	 * 
	 * @param inputStream 输入流
	 * @param outputStream 输出流
	 * @param close 写入完毕是否关闭输出流
	 * @param buffer 缓存字节数组
	 * @return 复制流的字节长度
	 * @throws IOException
	 */
	public static long copy(InputStream inputStream, OutputStream outputStream, boolean close, byte[] buffer) throws IOException {
		OutputStream out = outputStream;
		InputStream in = inputStream;
		try {
			long total = 0;
			for (;;) {
				int res = in.read(buffer);
				if (res == -1) {
					break;
				}
				if (res > 0) {
					total += res;
					if (out != null) {
						out.write(buffer, 0, res);
					}
				}
			}
			if (out != null) {
				if (close) {
					out.close();
				} else {
					out.flush();
				}
				out = null;
			}
			in.close();
			in = null;
			return total;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Throwable t) {
					/* Ignore me */
				}
			}
			if (close && out != null) {
				try {
					out.close();
				} catch (Throwable t) {
					/* Ignore me */
				}
			}
		}
	}

	/**
	 * 复制单个文件
	 */
	private static boolean copyFile(File oldFile, String newPath) {
		oldFile = normalizeFile(oldFile);
		newPath = normalizePath(newPath);

		if (!oldFile.exists()) { // 文件存在时
//			LogUtil.warn("File not found:" + oldFile);
			return false;
		}
		if (!oldFile.isFile()) { // 文件存在时
//			LogUtil.warn(oldFile + " is not file");
			return false;
		}
		if (oldFile.getName().equalsIgnoreCase("Thumbs.db")) {
//			LogUtil.warn(oldFile + " is ignored");
			return true;
		}
		FileUtil.mkdir(newPath.substring(0, newPath.lastIndexOf("/")));
		File newFile = new File(newPath);
		// 如果新文件是一个目录，则创建新的File对象
		if (newFile.isDirectory()) {
			newFile = new File(newPath, oldFile.getName());
		}
		if (newFile.getAbsoluteFile().equals(oldFile.getAbsoluteFile())) {
			return true;
		}
		try {
			InputStream inStream = new FileInputStream(oldFile); // 读入原文件
			FileOutputStream fs = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024];
			copy(inStream, fs, true, buffer);
		} catch (Exception e) {
//			LogUtil.warn("Copy file " + oldFile.getPath() + " failed,cause:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 复制整个文件夹内容
	 */
	private static boolean copyDir(File oldDir, String newPath) {
		oldDir = normalizeFile(oldDir);
		newPath = normalizePath(newPath);
		if (!oldDir.exists()) { // 文件存在时
//			LogUtil.info("File not found:" + oldDir);
			return false;
		}
		if (!oldDir.isDirectory()) { // 文件存在时
//			LogUtil.info(oldDir + " is not directory");
			return false;
		}
		try {
			new File(newPath).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File[] files = oldDir.listFiles();
			File temp = null;
			for (File file : files) {
				temp = file;
				if (temp.isFile()) {
					if (!FileUtil.copyFile(temp, newPath + "/" + temp.getName())) {
						return false;
					}
				} else if (temp.isDirectory()) {// 如果是子文件夹
					if (!FileUtil.copyDir(temp, newPath + "/" + temp.getName())) {
						return false;
					}
				}
			}
			return true;
		} catch (Exception e) {
//			LogUtil.info("Copy directory failed,cause:" + e.getMessage());
			 e.printStackTrace();
			return false;
		}
	}

	/**
	 * 移动文件到指定目录
	 */
	public static boolean move(String oldPath, String newPath) {
		if (StringUtil.isEmpty(newPath)) {
//			LogUtil.info("Destintion path:" + newPath + " cannot be empty!");
			return false;
		}
		if (StringUtil.isEmpty(oldPath)) {
//			LogUtil.info("Source path:" + oldPath + " cannot be empty!");
			return false;
		}
		oldPath = normalizePath(oldPath);
		newPath = normalizePath(newPath);

		// 判断newPath是否为oldFile的子路径，进行特殊处理
		if (newPath.startsWith(oldPath)) {
			return renameToSubDir(oldPath, newPath, true);
		} else {
			return copy(oldPath, newPath) && delete(oldPath);
		}
	}

	/**
	 * 该方法用于处理move和copy时目标路径为源路径的子路径问题
	 * 
	 * @param oldPath
	 * @param newPath
	 * @param deleteOldDir 如果是true则删除源文件夹，否则不删除
	 * @return
	 */
	private static boolean renameToSubDir(String oldPath, String newPath, boolean deleteOldDir) {
		File oldFile = new File(oldPath);
		File oldParent = oldFile.getParentFile();
		// 如果oldFile的父目录为分区根目录，如D:\的时候，不做处理，因为有些系统的文件或路径会出错
		if (oldParent == null) {
			String action = deleteOldDir ? "move" : "copy";
//			LogUtil.info("Cannot " + action + " directory:" + oldPath + " to a subdirectory of itself " + newPath);
			return false;
		}
		// 生成临时文件夹
		String oldParentPath = normalizePath(oldParent.getAbsolutePath()) + "/";
		String tempPath = oldParentPath + System.currentTimeMillis();

		File tempFile = new File(tempPath);
		File newFile = new File(newPath);
		newFile.mkdirs();
		// 将目标路径重命名为临时文件夹的名字
		newFile.renameTo(tempFile);
		if (!copy(oldPath, tempPath)) {
			return false;
		}
		// 删除源文件
		if (deleteOldDir && !delete(oldPath)) {
			return false;
		}
		newFile.getParentFile().mkdirs();
		// 如果重命名失败可能由于磁盘文件系统不一样，这时需要复制
		if (!tempFile.renameTo(newFile)) {
			// 需要重新创建文件夹
			oldFile.mkdirs();
			return copy(tempPath, oldPath) && delete(tempPath);
		}
		return true;
	}

	/**
	 * 将可序列化对象序列化并写入指定文件
	 */
	public static void serialize(Serializable obj, String fileName) {// NO_UCD
		fileName = normalizePath(fileName);
		ObjectOutputStream s = null;
		try {
			FileOutputStream f = new FileOutputStream(fileName);
			s = new ObjectOutputStream(f);
			s.writeObject(obj);
			s.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将可序列化对象序列化并返回二进制数组
	 */
	public static byte[] serialize(Serializable obj) {
		ObjectOutputStream s = null;
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			s = new ObjectOutputStream(b);
			s.writeObject(obj);
			s.flush();
			return b.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 从指定文件中反序列化对象
	 */
	public static Object unserialize(String fileName) {// NO_UCD
		fileName = normalizePath(fileName);
		ObjectInputStream s = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			s = new ContextClassLoaderObjectInputStream(in);
			Object o = s.readObject();
			return o;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 从二进制数组中反序列化对象
	 */
	public static Object unserialize(byte[] bs) {
		ObjectInputStream s = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bs);
			s = new ContextClassLoaderObjectInputStream(in);
			Object o = s.readObject();
			return o;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将一个Map高性能序列化,键值只能为字符串<br>
	 */
	public static byte[] mapToBytes(Map<?, ?> map) {
		if (map == null) {
			return null;
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			for (Entry<?, ?> entry : map.entrySet()) {
				Object k = entry.getKey();
				Object v = entry.getValue();
				if (k == null) {
					continue;
				}
				if (v == null) {
					bos.write(new byte[] { 0 });
				} else if (v instanceof String) {
					bos.write(new byte[] { 1 });
				} else if (v instanceof Long) {
					bos.write(new byte[] { 2 });
				} else if (v instanceof Integer) {
					bos.write(new byte[] { 3 });
				} else if (v instanceof Boolean) {
					bos.write(new byte[] { 4 });
				} else if (v instanceof Date) {
					bos.write(new byte[] { 5 });
				} else if (v instanceof Mapx) {
					bos.write(new byte[] { 6 });
				} else if (v instanceof Serializable) {
					bos.write(new byte[] { 7 });
				} else {
					throw new RuntimeException("Unknown datatype:" + v.getClass().getName());
				}
				byte[] bs = k.toString().getBytes();
				bos.write(NumberUtil.toBytes(bs.length));
				bos.write(bs);
				if (v == null) {
					continue;
				} else if (v instanceof String) {
					bs = v.toString().getBytes();
					bos.write(NumberUtil.toBytes(bs.length));
					bos.write(bs);
				} else if (v instanceof Long) {
					bos.write(NumberUtil.toBytes(((Long) v).longValue()));
				} else if (v instanceof Integer) {
					bos.write(NumberUtil.toBytes(((Integer) v).intValue()));
				} else if (v instanceof Boolean) {
					bos.write(((Boolean) v).booleanValue() ? 1 : 0);
				} else if (v instanceof Date) {
					bos.write(NumberUtil.toBytes(((Date) v).getTime()));
				} else if (v instanceof Mapx) {
					byte[] arr = mapToBytes((Mapx<?, ?>) v);
					bos.write(NumberUtil.toBytes(arr.length));
					bos.write(arr);
				} else if (v instanceof Serializable) {
					byte[] arr = serialize((Serializable) v);
					bos.write(NumberUtil.toBytes(arr.length));
					bos.write(arr);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}

	/**
	 * 将一个二进制数组反序列化为Mapx
	 */
	public static Mapx<String, Object> bytesToMap(byte[] arr) {
		if (arr == null) {
			return null;
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(arr);
		int b = -1;
		Mapx<String, Object> map = new Mapx<String, Object>();
		byte[] kbs = new byte[4];
		byte[] vbs = null;
		try {
			while ((b = bis.read()) != -1) {
				bis.read(kbs);
				int len = NumberUtil.toInt(kbs);
				vbs = new byte[len];
				bis.read(vbs);
				String k = new String(vbs);
				Object v = null;
				if (b == 1) {
					bis.read(kbs);
					len = NumberUtil.toInt(kbs);
					vbs = new byte[len];
					bis.read(vbs);
					v = new String(vbs);
				} else if (b == 2) {
					vbs = new byte[8];
					bis.read(vbs);
					v = Long.valueOf(NumberUtil.toLong(vbs));
				} else if (b == 3) {
					vbs = new byte[4];
					bis.read(vbs);
					v = Integer.valueOf(NumberUtil.toInt(vbs));
				} else if (b == 4) {
					int i = bis.read();
					v = Boolean.valueOf(i == 1 ? true : false);
				} else if (b == 5) {
					vbs = new byte[8];
					bis.read(vbs);
					v = new Date(NumberUtil.toLong(vbs));
				} else if (b == 6) {
					bis.read(kbs);
					len = NumberUtil.toInt(kbs);
					vbs = new byte[len];
					bis.read(vbs);
					v = bytesToMap(vbs);
				} else if (b == 7) {
					bis.read(kbs);
					len = NumberUtil.toInt(kbs);
					vbs = new byte[len];
					bis.read(vbs);
					v = unserialize(vbs);
				}
				map.put(k, v);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static List<File> getAllFile(String inputFolderPath) {
		return listFiles(inputFolderPath, null);
	}
	
	public static List<File> listFilesByExt(String inputFolderPath, List<String> extendes) {
		return listFiles(inputFolderPath, new FileFilter() {
					
					@Override
					public boolean accept(File pathname) {
						System.out.println(pathname.getName());
						return extendes.contains(getExtension(pathname.getName()));
					}
				});
	}
	public static List<File> listFiles(String inputFolderPath, FileFilter fileFilter) {

		List<File> fileList = new ArrayList<>();

		File inputFolder = new File(inputFolderPath);
		if (inputFolder.isDirectory()) {
			File[] files = inputFolder.listFiles();	
			
			for (File file : files) {
				fileList.addAll(listFiles(file.getPath(), fileFilter));
			}
		} else {
			if(fileFilter != null) {
				if(fileFilter.accept(inputFolder)) {
					fileList.add(inputFolder);
				}
			} else {
				fileList.add(inputFolder);
			}
		}

		return fileList;
	}
	
	public static void main2(String[] args) {
		File f = new File("F:/Workspace_Product\\XCMS\\UI\\Framework\\Controls/../../..");
		System.out.println(f.list().length);
		System.out.println(f.getAbsolutePath());
	}
	
	public static void main3(String[] args) {

		List<File> filse = getAllFile("E:/Projects/MyEclipse9_32/JAVA.NET_B_ORM/bbs_schema/classes");
		for (File file : filse) {
			System.out.println(file.getName());
		}
	}

}
