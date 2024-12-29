package com.arkxos.framework.commons.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @class org.ark.framework.util.ClassLoadUtil
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:58:40 
 * @version V1.0
 */
public class ClassLoadUtil {

	public static void maidn(String[] args) {

		// URLClassLoader clazzLoader =
		// getClassLoad("C:\\Users\\Darkness\\Desktop\\rapidark-connection.jar",
		// true);
		URLClassLoader clazzLoader = getClassLoad("E:\\Projects\\MyEclipse9_32\\JAVA.NET_B_ORM\\schema_jar\\bbs_schema.jar", true);
		try {
			// System.out.println(clazzLoader.loadClass("com.rapidark.java.net.connection.DBConfig"));
			System.out.println(clazzLoader.loadClass("com.rapidark.orm.db.bbs.schema.ArticleSchema"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Demo:
	 * addURLs("lib");
		
		try {
			Class<?> c = Class.forName("com.rapidark.orm.db.permission.schema.UserSet");
			System.out.println(c + " loaded successfully");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	 * @author Darkness
	 * @date 2011-12-7 下午01:57:20 
	 * @version V1.0  
	 * @param path
	 * @throws MalformedURLException
	 */
	public static void addJarPath(String path) throws MalformedURLException {
		File lib = new File(path);
		File[] jars = lib.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if ( name.toLowerCase().endsWith(".jar") ) {
					return true;
				} else {
					return false;
				}
			}
		});
		URL[] urls = new URL[jars.length];
		for(int i=0; i<jars.length; i++) {
			urls[i] = jars[i].toURI().toURL();
		}
		addJarsURL(urls);
	}
	
	public static void addJarsURL(URL[] urls) {
		try {
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			if ( cl instanceof URLClassLoader ) {
				URLClassLoader ul = (URLClassLoader)cl;
				Class<?>[] paraTypes = new Class[1];
				paraTypes[0] = URL.class;
				Method method = URLClassLoader.class.getDeclaredMethod("addURL", paraTypes);
				method.setAccessible(true);
				Object[] args = new Object[1];
				for(int i=0; i<urls.length; i++) {
					args[0] = urls[i];
					method.invoke(ul, args);
				}
			}
		} catch ( Exception e ) {
			// @TODO
		}
	}

	static URLClassLoader loader = null;

	/**
	 * 在默认的目录加载jar
	 * 
	 * @return
	 */
	public static URLClassLoader getClassLoad() {
		// Configuration config = new Configuration(Configuration.getRoot() +
		// File.separator + "classpath.properties");
		// if (loader == null) {
		// URLClassLoaderUtil urlClass = new URLClassLoaderUtil(
		// config.getValue("classpath1"), false);
		// loader = urlClass.getClassLoader();
		// }
		return loader;
	}

	/**
	 * 在给定的路径加载jar文件
	 * 
	 * @param url
	 *            指定路径
	 * @param isFile
	 *            true 文件 false 目录
	 * @return
	 */
	public static URLClassLoader getClassLoad(String url, boolean isFile) {
		URLClassLoaderUtil urlClass = new URLClassLoaderUtil(url, isFile);
		URLClassLoader loader = urlClass.getClassLoader();
		return loader;
	}

	/**
	 * URLClassLoader classLoader = ClassLoadUtil.loadClass("E:\\Projects\\MyEclipse9_32\\JAVA.NET_B_ORM\\schema_jar\\permission_schema.jar", true);

		Class<?> clazz = classLoader.loadClass("com.rapidark.orm.db.permission.schema.UserSet");
		System.out.println(clazz);
	 * @author Darkness
	 * @date 2011-12-7 下午02:27:06 
	 * @version V1.0  
	 * @param url
	 * @param isFile
	 * @return
	 */
	public static URLClassLoader loadClass(String url, boolean isFile) {
		URLClassLoaderUtil urlClass = new URLClassLoaderUtil(url, isFile);
		urlClass.callBack();
		return urlClass.getClassLoader();
	}
}

class URLClassLoaderUtil {
	URLClassLoader classLoader = null;// URLClassLoader类载入器
	private String jarFileName;
	private boolean isFile = true;
	List<String> jars = new ArrayList<String>(0);

	/**
	 * 加载具体的某一jar包
	 * 
	 * @param jarFileName
	 */
	public URLClassLoaderUtil(String jarFileName) {
		this.setJarFileName(jarFileName);
		this.inti();
	}

	/**
	 * 加载jar包 当isFile为false是加载文件夹下所有jar
	 * 
	 * @param jarFileName
	 *            路径
	 * @param isFile
	 */
	public URLClassLoaderUtil(String jarFileName, boolean isFile) {
		this.setJarFileName(jarFileName);
		this.setFile(isFile);
		this.inti();
	}

	/**
	 * 初始化，读取文件信息，并将jar文件路径加入到classpath
	 */
	private void inti() {
		// 添加jar文件路径到classpath
		if (this.isFile) {
			File f = new File(jarFileName);
			addPath(f.toURI().toString());
			jars.add(f.getAbsolutePath());
		} else {
			ReadJarFile df = new ReadJarFile(jarFileName, new String[] { "jar", "zip" });
			this.jars = df.getFiles();
			List<String> jarURLs = df.getFilesURL();
			Object[] o = jarURLs.toArray();
			addPath(o);
		}
	}

	/**
	 * 回叫方法，class操作
	 * 
	 * @paramcallBack
	 */
	public void callBack() {
		for (String s : this.jars) {
			loadClass(s);
		}
	}

	/**
	 * 添加单个jar路径到classpath
	 * 
	 * @paramjarURL
	 */
	private void addPath(String jarURL) {
		try {
			System.out.println("s");
			System.out.println(Thread.currentThread().getContextClassLoader());
			classLoader = new URLClassLoader(new URL[] { new URL(jarURL) }, ClassLoader.getSystemClassLoader());//Thread.currentThread().getContextClassLoader());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加jar路径到classpath
	 * 
	 * @paramjarURLs
	 */
	private void addPath(Object[] jarURLs) {
		URL[] urls = new URL[jarURLs.length];
		for (int i = 0; i < jarURLs.length; i++) {
			try {
				System.out.println(jarURLs[i].toString());
				urls[i] = new URL(jarURLs[i].toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		classLoader = new URLClassLoader(urls);
	}

	/**
	 * 动态载入class
	 * 
	 * @paramjarFileName
	 * @paramcallBack
	 */
	// private void loadClass(String jarFileName, ClassCallBack callBack) {
	private void loadClass(String jarFileName) {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(jarFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Enumeration<JarEntry> en = jarFile.entries();
		while (en.hasMoreElements()) {
			JarEntry je = en.nextElement();
			String name = je.getName();
			String s5 = name.replace('/', '.');
			if (s5.lastIndexOf(".class") > 0) {
				String className = je.getName().substring(0, je.getName().length() - ".class".length()).replace('/', '.');
				Class<?> c = null;
				try {
					c = this.classLoader.loadClass(className);
					System.out.println(c.newInstance());
					System.out.println(className);
				} catch (ClassNotFoundException e) {
					System.out.println("NO CLASS: " + className);
					// continue;
				} catch (NoClassDefFoundError e) {
					System.out.println("NO CLASS: " + className);
					// continue;
				}
				// callBack.operate(c);
				catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public String getJarFileName() {
		return jarFileName;
	}

	/**
	 * 设置jar路径
	 * 
	 * @param jarFileName
	 */
	public void setJarFileName(String jarFileName) {
		this.jarFileName = jarFileName;
	}

	public boolean isFile() {
		return isFile;
	}

	public URLClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(URLClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}
}

/**
 * 读取jarwenjian
 * 
 * @author hml
 * 
 */
class ReadJarFile {
	List<String> jarList = new ArrayList<String>();
	List<String> filesURL = new ArrayList<String>();

	/**
	 * 读取指定文件夹的文件
	 * 
	 * @param jarFileName
	 *            路径
	 * @param strings
	 *            后缀
	 */
	public ReadJarFile(String jarFileName, String[] strings) {
		// TODO Auto-generated constructor stub
		File f = new File(jarFileName);
		File[] fl = f.listFiles();
		for (File file : fl) {
			for (String str : strings) {
				if (file.getName().endsWith(str)) {
					jarList.add(file.getName());
					filesURL.add(file.toURI().toString());
				}
			}
		}
	}

	/**
	 * 获取文件名
	 * 
	 * @return
	 */
	public List<String> getFiles() {
		return filesURL;
	}

	/**
	 * 获取文件路径
	 * 
	 * @return
	 */
	public List<String> getFilesURL() {
		return filesURL;
	}
}
