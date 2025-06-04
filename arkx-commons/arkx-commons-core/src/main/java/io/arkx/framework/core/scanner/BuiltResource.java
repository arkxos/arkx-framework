package io.arkx.framework.core.scanner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;

/**
 * 编译后的资源，包括class文件和源代码目录下的资源文件
 */
public class BuiltResource {
	
	private String jarEntryName;
	private String fileName;

	/**
	 * 构造器
	 * 
	 * @param fileName 文件名(有可能是jar文件名)
	 * @param jarEntryName jar中的entry名称
	 */
	BuiltResource(String fileName, String jarEntryName) {
		this.jarEntryName = jarEntryName;
		this.fileName = fileName;
	}
	
	InputStream inputStream;
	
	public BuiltResource(String fileName, InputStream inputStream) {
		this.fileName = fileName;
		this.inputStream = inputStream;
	}

	/**
	 * @return 资源对应的InputStream
	 */
	public InputStream getInputStream() {
		try {
			if(this.inputStream != null) {
				return inputStream;
			}
			if (jarEntryName != null) {
				JarFile jf = new JarFile(fileName);
				return jf.getInputStream(jf.getEntry(jarEntryName));
			} else {
				return new FileInputStream(fileName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param innerClassName 内部类名称
	 * @return 内部类对应的InputStream
	 */
	public InputStream getInnerClassInputStream(String innerClassName) {
		try {
			innerClassName = innerClassName.substring(innerClassName.lastIndexOf("$"));
			if (jarEntryName != null) {
				JarFile jf = new JarFile(fileName);
				String entryName = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")) + innerClassName + ".class";
				return jf.getInputStream(jf.getEntry(entryName));
			} else {
				String innerFileName = fileName.substring(0, fileName.lastIndexOf(".")) + innerClassName + ".class";
				return new FileInputStream(innerFileName);
			}
		} catch (FileNotFoundException e) {
			System.out.println("@TODO need handle");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return 资源全路径
	 */
	public String getFullName() {
		String fullName = fileName;
		if (jarEntryName != null) {
			fullName = fileName + "!" + jarEntryName;
		}
		return fullName.replace('\\', '/');
	}

	/**
	 * @return 是否是一个class文件
	 */
	public boolean isClass() {
		if (jarEntryName != null && jarEntryName.endsWith(".class")) {
			return true;
		}
		if (jarEntryName == null && fileName.endsWith(".class")) {
			return true;
		}
		return false;
	}

	/**
	 * @return 资源是否处于一个jar文件中
	 */
	public boolean isInJar() {
		return jarEntryName != null;
	}

	/**
	 * @return jar文件中的entry名称
	 */
	public String getJarEntryName() {
		return jarEntryName;
	}

	/**
	 * @return 文件名称，有可能是所在jar文件的文件名称
	 */
	public String getFileName() {
		return fileName;
	}
}
