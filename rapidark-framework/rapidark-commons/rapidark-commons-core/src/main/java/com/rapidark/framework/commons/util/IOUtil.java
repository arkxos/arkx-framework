package com.rapidark.framework.commons.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.rapidark.framework.Config;
import com.rapidark.framework.commons.collection.Mapx;

/**
 * IO工具类
 * 
 */
public class IOUtil {

	public static byte[] getBytesFromStream(InputStream is) throws IOException {
		return getBytesFromStream(is, Integer.MAX_VALUE);
	}

	public static byte[] getBytesFromStream(InputStream is, int max) throws IOException {
		byte[] buffer = new byte[1024];
		int read = -1;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] data = null;
		try {
			while ((read = is.read(buffer)) != -1) {
				if (bos.size() > max) {
					throw new IOException("InputStream length is out of range,max=" + max);
				}
				if (read > 0) {
					byte[] chunk = null;
					if (read == 1024) {
						chunk = buffer;
					} else {
						chunk = new byte[read];
						System.arraycopy(buffer, 0, chunk, 0, read);
					}
					bos.write(chunk);
				}
			}
			data = bos.toByteArray();
		} finally {
			if (bos != null) {
				bos.close();
				bos = null;
			}
		}
		return data;
	}

	public static void download(HttpServletRequest request, HttpServletResponse response, String fileName, InputStream is) {
		try {
			setDownloadFileName(request, response, fileName);
			if (is == null) {
				return;
			}
			OutputStream os = response.getOutputStream();
			byte[] buffer = new byte[1024];
			int read = -1;
			try {
				while ((read = is.read(buffer)) != -1) {
					if (read > 0) {
						byte[] chunk = null;
						if (read == 1024) {
							chunk = buffer;
						} else {
							chunk = new byte[read];
							System.arraycopy(buffer, 0, chunk, 0, read);
						}
						os.write(chunk);
						os.flush();
					}
				}
			} finally {
				is.close();
			}
			os.flush();
			os.close();
		} catch (IOException e) {
			LogUtil.warn("IOUtil.download:IO ends by user!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置下载文件名,通过设置http-header命名得文件下载时的文件名按照fileName参数呈现。
	 * 
	 * @param request
	 * @param response
	 * @param fileName
	 */
	public static void setDownloadFileName(HttpServletRequest request, HttpServletResponse response, String fileName) {
		try {
			response.reset();
			response.setContentType("application/octet-stream");
			String userAgent = request.getHeader("User-Agent");
			if (StringUtil.isNotEmpty(userAgent)
					&& (userAgent.toLowerCase().indexOf("msie") >= 0 || userAgent.toLowerCase().indexOf("trident") >= 0)) {
				fileName = new String(fileName.getBytes("GBK"), "ISO-8859-1");
			} else {
				fileName = new String(fileName.getBytes(Config.getGlobalCharset()), "ISO-8859-1");
			}
			response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final long ONE_KB = 1024;
	public static final long ONE_MB = ONE_KB * ONE_KB;
	public static final long ONE_GB = ONE_KB * ONE_MB;

	/**
	 * 字节数可读性转换
	 */
	public static String byteCountToDisplaySize(long size) {
		String displaySize;
		if (size / ONE_GB > 0) {
			displaySize = String.valueOf(size / ONE_GB) + " GB";
		} else if (size / ONE_MB > 0) {
			displaySize = String.valueOf(size / ONE_MB) + " MB";
		} else if (size / ONE_KB > 0) {
			displaySize = String.valueOf(size / ONE_KB) + " KB";
		} else {
			displaySize = String.valueOf(size);
		}
		return displaySize;
	}
	
	public static byte[] mapToBytes(Mapx<String, Object> map) {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		try {
			for (String k : map.keyArray()) {
				Object v = map.get(k);
				int type = 0;
				byte[] bs = (byte[]) null;
				if ((v instanceof String)) {
					type = 1;
					bs = ((String) v).getBytes("UTF-8");
				} else if ((v instanceof byte[])) {
					type = 2;
					bs = (byte[]) v;
				} else {
					if (!(v instanceof Serializable))
						continue;
					type = 0;
					bs = FileUtil.serialize((Serializable) v);
				}

				bo.write(NumberUtil.toBytes(k.length()));
				bo.write(k.getBytes());
				bo.write(NumberUtil.toBytes(type));
				bo.write(NumberUtil.toBytes(bs.length));
				bo.write(bs);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bo.toByteArray();
	}

	public static Mapx<String, Object> bytesToMap(byte[] src) {
		Mapx map = new Mapx();
		if ((src == null) || (src.length == 0)) {
			return map;
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(src);
		try {
			while (true) {
				byte[] bs = new byte[4];
				if (bis.read(bs) == -1) {
					break;
				}
				int len = NumberUtil.toInt(bs);

				bs = new byte[len];
				bis.read(bs);
				String k = new String(bs);

				bs = new byte[4];
				bis.read(bs);
				int type = NumberUtil.toInt(bs);

				bis.read(bs);
				len = NumberUtil.toInt(bs);

				bs = new byte[len];
				bis.read(bs);

				if (type == 1) {
					map.put(k, new String(bs, "UTF-8"));
					continue;
				}
				if (type == 2) {
					map.put(k, bs);
					continue;
				}
				if (type == 0)
					map.put(k, FileUtil.unserialize(bs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return map;
	}
}
