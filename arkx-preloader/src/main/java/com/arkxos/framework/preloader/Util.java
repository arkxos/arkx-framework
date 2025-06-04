package com.arkxos.framework.preloader;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class Util {
	private static String PluginPath;

	public static String getPluginPath() {
		if (PluginPath == null) {
			String preloadResourceName = "com/arkxos/preloader/PreClassLoader.class";
			URL url = Thread.currentThread().getContextClassLoader().getResource(preloadResourceName);
			if (url == null) {
				try {
					new Throwable();
				} catch (Throwable t) {
					t.printStackTrace();
				}
				System.err.println("PreClassLoader.getPluginPath() failed!");
				return "";
			}
			try {
				String path = URLDecoder.decode(url.getPath(), System.getProperty("file.encoding"));
				if ((System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) && (path.startsWith("/"))) {
					path = path.substring(1);
				}
				if (path.startsWith("file:/")) {
					path = path.substring(6);
				} else if (path.startsWith("jar:file:/")) {
					path = path.substring(10);
				}
				if (path.indexOf(".jar!") > 0) {
					path = path.substring(0, path.indexOf(".jar!"));
				}
				path = path.substring(0, path.lastIndexOf("/WEB-INF") + 8) + "/plugins/";
				if ((System.getProperty("os.name").toLowerCase().indexOf("windows") < 0) && (!path.startsWith("/"))) {
					path = "/" + path;
				}
				PluginPath = path;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return PluginPath;
	}

	public static byte[] readByte(String file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return readByte(fis);
		} catch (Exception e) {
			throw new RuntimeException("File.readByte() failed");
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static byte[] readByte(InputStream is) {
		byte buffer[] = new byte[8192];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		do {
			int bytesRead = -1;
			try {
				bytesRead = is.read(buffer);
			} catch (IOException e) {
				throw new RuntimeException("File.readByte() failed");
			}
			if (bytesRead != -1)
				try {
					os.write(buffer, 0, bytesRead);
				} catch (Exception e) {
					throw new RuntimeException("File.readByte() failed");
				}
			else
				return os.toByteArray();
		} while (true);
	}

	public static boolean validateLicense() {
		return !new File(getPluginPath() + "license.err").exists();
	}

	public static String getCurrentStack() {
		return getStack(new Throwable());
	}

	public static String getStack(Throwable t) {
		StackTraceElement[] stack = t.getStackTrace();
		StringBuilder sb = new StringBuilder();
		StackTraceElement[] arrayOfStackTraceElement1;
		int j = (arrayOfStackTraceElement1 = stack).length;
		for (int i = 0; i < j; i++) {
			StackTraceElement ste = arrayOfStackTraceElement1[i];
			if (ste.getClassName().indexOf("ObjectUtil.getCurrentStack") == -1) {
				sb.append("\tat ");
				sb.append(ste.getClassName());
				sb.append(".");
				sb.append(ste.getMethodName());
				sb.append("(");
				sb.append(ste.getFileName());
				sb.append(":");
				sb.append(ste.getLineNumber());
				sb.append(")\n");
			}
		}
		return sb.toString();
	}

	public static boolean writeByte(File f, byte[] b) {
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
}
