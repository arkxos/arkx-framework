package com.arkxos.framework.cosyui.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Jar中的资源项
 * 
 */
public class JarResourceEntry {
	private String jarFileName;
	private long lastModified;
	private String path;
	private boolean isDirectory = false;;
	private long length;

	public boolean isDirectory() {
		return isDirectory;
	}

	public boolean isFile() {
		return !isDirectory;
	}

	public JarResourceEntry(String jarFileName, long lastModified, String path, boolean dirFlag, long length) {
		this.jarFileName = jarFileName;
		this.lastModified = lastModified;
		this.path = path;
		isDirectory = dirFlag;
		this.length = length;
	}

	public InputStream toStream() throws IOException {
		JarFile jf = new JarFile(jarFileName);
		try {
			ZipEntry ze = jf.getEntry(path);
			if (ze != null) {
				return jf.getInputStream(ze);
			}
		} finally {
//			jf.close();
		}
		return null;
	}

	public List<JarResourceEntry> listFiles() {
		return JarResourceRoot.listFiles(path);
	}

	public String getJarFileName() {
		return jarFileName;
	}

	public void setJarFileName(String jarFileName) {
		this.jarFileName = jarFileName;
	}

	public long getLastModified() {
		return lastModified;
	}

	public String getFullPath() {
		return path;
	}

	public long length() {
		return length;
	}

	public String getName() {
		if (path.indexOf("/") >= 0) {
			return path.substring(path.lastIndexOf("/") + 1);
		}
		return path;
	}
}
