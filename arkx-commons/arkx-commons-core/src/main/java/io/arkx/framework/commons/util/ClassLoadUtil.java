package io.arkx.framework.commons.util;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @class org.ark.framework.util.ClassLoadUtil
 * @author Darkness
 * @date 2013-1-31 下午12:58:40
 * @version V1.0
 */
public class ClassLoadUtil {

	public static void maidn(String[] args) {

		// URLClassLoader clazzLoader =
		// getClassLoad("C:\\Users\\Darkness\\Desktop\\arkxos-connection.jar",
		// true);
		URLClassLoader clazzLoader = getClassLoad(
				"E:\\Projects\\MyEclipse9_32\\JAVA.NET_B_ORM\\schema_jar\\bbs_schema.jar", true);
		try {
			// System.out.println(clazzLoader.loadClass("io.arkx.java.net.connection.DBConfig"));
			System.out.println(clazzLoader.loadClass("io.arkx.orm.db.bbs.schema.ArticleSchema"));
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Demo: addURLs("lib");
	 *
	 * try { Class<?> c = Class.forName("io.arkx.orm.db.permission.schema.UserSet");
	 * System.out.println(c + " loaded successfully"); } catch (ClassNotFoundException e)
	 * { e.printStackTrace(); }
	 *
	 * @author Darkness
	 * @date 2011-12-7 下午01:57:20
	 * @version V1.0
	 * @param path
	 * @throws MalformedURLException
	 */
	public static void addJarPath(String path) throws MalformedURLException {
		// 使用新方法加载目录下的所有jar文件
		loadJarsFromDirectory(path, true);
	}

	/**
	 * 加载目录下的所有jar文件，并打印每个加载的类
	 * @param directoryPath jar文件目录的路径
	 * @param printClasses 是否打印加载的类
	 * @return 加载的jar文件数量
	 * @throws MalformedURLException 如果URL格式错误
	 */
	public static int loadJarsFromDirectory(String directoryPath, boolean printClasses) throws MalformedURLException {
		LogUtil.info("开始加载目录下的jar文件: " + directoryPath);

		File directory = new File(directoryPath);
		if (!directory.exists() || !directory.isDirectory()) {
			LogUtil.error("指定的路径不存在或不是目录: " + directoryPath);
			return 0;
		}

		// 查找所有jar文件
		File[] jarFiles = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jar");
			}
		});

		if (jarFiles == null || jarFiles.length == 0) {
			LogUtil.warn("目录中未找到jar文件: " + directoryPath);
			return 0;
		}

		LogUtil.info("在目录 " + directoryPath + " 中找到 " + jarFiles.length + " 个jar文件");

		// 创建URL数组
		URL[] urls = new URL[jarFiles.length];
		for (int i = 0; i < jarFiles.length; i++) {
			urls[i] = jarFiles[i].toURI().toURL();
			LogUtil.info("准备加载jar文件: " + jarFiles[i].getName());
		}

		// 添加到类路径
		addJarsURL(urls);

		// 加载并打印类
		int totalClassCount = 0;
		if (printClasses) {
			LogUtil.info("开始加载并打印jar文件中的类...");
			for (File jarFile : jarFiles) {
				int classCount = loadAndPrintClasses(jarFile.getAbsolutePath());
				totalClassCount += classCount;
			}
		}

		LogUtil.info("成功加载 " + jarFiles.length + " 个jar文件，包含 " + totalClassCount + " 个类");
		return jarFiles.length;
	}

	/**
	 * 加载jar文件中的所有类并打印类信息
	 * @param jarFilePath jar文件的路径
	 * @return 加载的类数量
	 */
	public static int loadAndPrintClasses(String jarFilePath) {
		int classCount = 0;
		JarFile jarFile = null;

		try {
			LogUtil.info("打开jar文件: " + jarFilePath);
			jarFile = new JarFile(jarFilePath);

			// 首先收集所有类文件
			List<String> classNames = new ArrayList<>();
			Map<String, Set<String>> classDependencies = new HashMap<>();

			// 遍历所有条目，收集类文件
			Enumeration<JarEntry> allEntries = jarFile.entries();
			while (allEntries.hasMoreElements()) {
				JarEntry entry = allEntries.nextElement();
				if (entry.getName().endsWith(".class")) {
					String className = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');
					classNames.add(className);
					classDependencies.put(className, new HashSet<>());
				}
			}

			LogUtil.info("在JAR文件中找到 " + classNames.size() + " 个类文件");

			// 分析类依赖关系
			for (String className : classNames) {
				try {
					// 使用ASM分析类依赖
					JarEntry entry = jarFile.getJarEntry(className.replace('.', '/') + ".class");
					if (entry != null) {
						try (InputStream is = jarFile.getInputStream(entry)) {
							ClassReader reader = new ClassReader(is);
							ClassDependencyVisitor visitor = new ClassDependencyVisitor(className);
							reader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
							Set<String> deps = visitor.getDependencies();
							if (deps != null) {
								classDependencies.put(className, deps);
							}
							else {
								classDependencies.put(className, new HashSet<>());
							}
						}
					}
				}
				catch (Exception e) {
					LogUtil.debug("分析类依赖时出错: " + className + ", " + e.getMessage());
					classDependencies.put(className, new HashSet<>());
				}
			}

			// 拓扑排序，确定加载顺序
			List<String> loadOrder = topologicalSort(classNames, classDependencies);

			// 获取上下文类加载器和系统类加载器
			ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
			ClassLoader systemLoader = ClassLoader.getSystemClassLoader();

			// 获取当前应用的类加载层次结构
			List<ClassLoader> classLoaderChain = new ArrayList<>();
			ClassLoader tempLoader = contextLoader;
			while (tempLoader != null) {
				classLoaderChain.add(tempLoader);
				tempLoader = tempLoader.getParent();
			}

			// 创建自定义类加载器 - 使用当前上下文加载器作为父加载器
			URLClassLoader customLoader = null;
			try {
				URL jarUrl = new File(jarFilePath).toURI().toURL();
				customLoader = new URLClassLoader(new URL[] { jarUrl }, contextLoader);

				// 设置为线程上下文类加载器，便于Spring等框架使用
				Thread.currentThread().setContextClassLoader(customLoader);
				LogUtil.info("已创建自定义类加载器并设置为线程上下文类加载器");
			}
			catch (MalformedURLException e) {
				LogUtil.error("创建自定义类加载器失败: " + e.getMessage());
			}

			// 检测是否在Spring Boot环境中
			boolean isSpringBootEnv = false;
			String springBootLoaderClassName = "org.springframework.boot.loader.LaunchedURLClassLoader";
			for (ClassLoader loader : classLoaderChain) {
				if (loader.getClass().getName().equals(springBootLoaderClassName)) {
					isSpringBootEnv = true;
					LogUtil.info("检测到Spring Boot环境，使用特殊的类加载策略");
					break;
				}
			}

			// 按顺序加载类
			for (String className : loadOrder) {
				boolean classLoaded = false;

				try {
					// 首先尝试使用自定义类加载器加载
					Class<?> loadedClass = null;
					try {
						if (customLoader != null) {
							loadedClass = customLoader.loadClass(className);
							classLoaded = true;
						}
					}
					catch (ClassNotFoundException customLoaderEx) {
						// 记录详细信息，但继续尝试其他加载策略
						LogUtil.debug("自定义类加载器无法加载类: " + className + ", 尝试其他加载策略");
					}

					// 如果自定义加载器失败且处于Spring Boot环境中，尝试使用反射手动加载类
					if (!classLoaded && isSpringBootEnv) {
						JarEntry classEntry = jarFile.getJarEntry(className.replace('.', '/') + ".class");
						if (classEntry != null) {
							try (InputStream is = jarFile.getInputStream(classEntry)) {
								byte[] classBytes = readAllBytes(is);

								// 尝试使用适当的类加载器手动定义类
								for (ClassLoader loader : classLoaderChain) {
									try {
										// 使用defineClass方法
										Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass",
												String.class, byte[].class, int.class, int.class);
										defineClassMethod.setAccessible(true);

										loadedClass = (Class<?>) defineClassMethod.invoke(loader, className, classBytes,
												0, classBytes.length);

										if (loadedClass != null) {
											LogUtil.info("通过反射手动定义类成功: " + className);
											classLoaded = true;
											break;
										}
									}
									catch (Exception e) {
										// 继续尝试下一个类加载器
										LogUtil.debug("尝试使用类加载器 " + loader.getClass().getName() + " 手动定义类 " + className
												+ " 失败: " + e.getMessage());
									}
								}
							}
						}
					}

					// 如果前面的方法都失败了，尝试使用上下文类加载器
					if (!classLoaded) {
						try {
							loadedClass = Class.forName(className, true, contextLoader);
							classLoaded = true;
						}
						catch (ClassNotFoundException e) {
							// 最后尝试使用系统类加载器
							try {
								loadedClass = Class.forName(className, true, systemLoader);
								classLoaded = true;
							}
							catch (ClassNotFoundException e2) {
								// 所有尝试都失败
								throw e2;
							}
						}
					}

					if (classLoaded && loadedClass != null) {
						classCount++;

						// 构建类信息字符串
						StringBuilder classInfo = new StringBuilder();
						classInfo.append("已加载类: ").append(className);

						// 添加类型信息
						if (loadedClass.isInterface()) {
							classInfo.append(" [接口]");
						}
						else if (java.lang.reflect.Modifier.isAbstract(loadedClass.getModifiers())) {
							classInfo.append(" [抽象类]");
						}
						else {
							classInfo.append(" [类]");
						}

						// 添加父类信息
						Class<?> superClass = loadedClass.getSuperclass();
						if (superClass != null && !superClass.equals(Object.class)) {
							classInfo.append(", 父类: ").append(superClass.getName());
						}

						// 添加实现的接口
						Class<?>[] interfaces = loadedClass.getInterfaces();
						if (interfaces.length > 0) {
							classInfo.append(", 实现接口: ");
							for (int i = 0; i < interfaces.length; i++) {
								if (i > 0) {
									classInfo.append(", ");
								}
								classInfo.append(interfaces[i].getName());
							}
						}

						// 添加类加载器信息
						classInfo.append(", 加载器: ").append(loadedClass.getClassLoader().getClass().getName());

						LogUtil.info(classInfo.toString());
					}

				}
				catch (ClassNotFoundException e) {
					// 尝试查找类文件是否确实存在于JAR中
					String classPathInJar = className.replace('.', '/') + ".class";
					JarEntry classEntry = jarFile.getJarEntry(classPathInJar);

					StringBuilder errorInfo = new StringBuilder();
					errorInfo.append("无法加载类: ").append(className).append(", 原因: ").append(e.getMessage());

					if (classEntry == null) {
						errorInfo.append(" - JAR中不存在此类文件");
					}
					else {
						errorInfo.append(" - JAR中存在此类文件，但加载失败，可能是缺少依赖项");

						// 显示依赖关系
						Set<String> deps = classDependencies.get(className);
						if (deps != null && !deps.isEmpty()) {
							errorInfo.append("\n    依赖项:");
							for (String dep : deps) {
								errorInfo.append("\n      - ").append(dep);
								try {
									Class.forName(dep);
									errorInfo.append(" [已加载]");
								}
								catch (ClassNotFoundException ex) {
									errorInfo.append(" [未加载]");
								}
							}
						}
					}

					LogUtil.info(errorInfo.toString());

					// 如果在Spring Boot环境中，提供更详细的诊断
					if (isSpringBootEnv && classEntry != null) {
						String diagnosis = diagnoseClassNotFound(className);
						LogUtil.info("Spring Boot环境类加载诊断:\n" + diagnosis);
					}
				}
				catch (NoClassDefFoundError e) {
					LogUtil.debug("类定义找不到: " + className + ", 原因: " + e.getMessage());
				}
				catch (Exception e) {
					LogUtil
						.warn("加载类时出现异常: " + className + ", 类型: " + e.getClass().getName() + ", 原因: " + e.getMessage());
				}
			}

			// 恢复原始上下文类加载器
			Thread.currentThread().setContextClassLoader(contextLoader);

			LogUtil.info("从jar文件 " + jarFilePath + " 中加载了 " + classCount + " 个类");

		}
		catch (IOException e) {
			LogUtil.error("无法读取jar文件: " + jarFilePath + ", 原因: " + e.getMessage());
		}
		finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				}
				catch (IOException e) {
					// 忽略关闭异常
				}
			}
		}

		return classCount;
	}

	// 读取输入流中的所有字节
	private static byte[] readAllBytes(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[4096];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		return buffer.toByteArray();
	}

	// 拓扑排序实现
	private static List<String> topologicalSort(List<String> classNames, Map<String, Set<String>> dependencies) {
		List<String> result = new ArrayList<>();
		Set<String> visited = new HashSet<>();
		Set<String> temp = new HashSet<>();

		// 确保所有类名都有对应的依赖集合
		for (String className : classNames) {
			if (!dependencies.containsKey(className)) {
				dependencies.put(className, new HashSet<>());
			}
		}

		for (String className : classNames) {
			if (!visited.contains(className)) {
				if (!topologicalSortUtil(className, visited, temp, result, dependencies)) {
					// 检测到循环依赖，记录详细信息
					StringBuilder cycleInfo = new StringBuilder();
					cycleInfo.append("检测到循环依赖，涉及类: ").append(className).append("\n");
					cycleInfo.append("依赖关系:\n");
					for (String dep : dependencies.get(className)) {
						cycleInfo.append("  ").append(className).append(" -> ").append(dep).append("\n");
					}
					LogUtil.warn(cycleInfo.toString());
				}
			}
		}

		Collections.reverse(result);
		return result;
	}

	private static boolean topologicalSortUtil(String className, Set<String> visited, Set<String> temp,
			List<String> result, Map<String, Set<String>> dependencies) {
		if (temp.contains(className)) {
			return false; // 检测到循环
		}
		if (visited.contains(className)) {
			return true;
		}

		temp.add(className);

		Set<String> deps = dependencies.get(className);
		if (deps != null) { // 添加空检查
			for (String dep : deps) {
				if (!topologicalSortUtil(dep, visited, temp, result, dependencies)) {
					return false;
				}
			}
		}

		temp.remove(className);
		visited.add(className);
		result.add(className);
		return true;
	}

	// ASM类依赖分析器
	private static class ClassDependencyVisitor extends ClassVisitor {

		private final Set<String> dependencies = new HashSet<>();

		private final String currentClassName;

		public ClassDependencyVisitor(String currentClassName) {
			super(Opcodes.ASM9);
			this.currentClassName = currentClassName;
		}

		@Override
		public void visit(int version, int access, String name, String signature, String superName,
				String[] interfaces) {
			if (superName != null && !superName.equals("java/lang/Object")) {
				String superClassName = superName.replace('/', '.');
				if (!superClassName.equals(currentClassName)) {
					dependencies.add(superClassName);
				}
			}
			if (interfaces != null) {
				for (String iface : interfaces) {
					String interfaceName = iface.replace('/', '.');
					if (!interfaceName.equals(currentClassName)) {
						dependencies.add(interfaceName);
					}
				}
			}
		}

		@Override
		public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
			Type type = Type.getType(desc);
			if (type.getSort() == Type.OBJECT) {
				String fieldType = type.getClassName();
				if (!fieldType.equals(currentClassName)) {
					dependencies.add(fieldType);
				}
			}
			return null;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			Type[] argTypes = Type.getArgumentTypes(desc);
			for (Type type : argTypes) {
				if (type.getSort() == Type.OBJECT) {
					String argType = type.getClassName();
					if (!argType.equals(currentClassName)) {
						dependencies.add(argType);
					}
				}
			}
			Type returnType = Type.getReturnType(desc);
			if (returnType.getSort() == Type.OBJECT) {
				String returnTypeName = returnType.getClassName();
				if (!returnTypeName.equals(currentClassName)) {
					dependencies.add(returnTypeName);
				}
			}
			return null;
		}

		public Set<String> getDependencies() {
			return dependencies;
		}

	}

	public static void addJarsURL(URL[] urls) {
		addJarsURL(urls, ClassLoader.getSystemClassLoader());
	}

	/**
	 * 将URL数组添加到指定的类加载器中
	 * @param urls 要添加到类加载器中的URL数组
	 * @param classLoader 要添加URL的类加载器，如果为null则默认使用系统类加载器
	 */
	public static void addJarsURL(URL[] urls, ClassLoader classLoader) {
		// 检查输入参数是否有效
		if (urls == null || urls.length == 0) {
			return;
		}

		// 如果未指定类加载器，则使用系统类加载器
		if (classLoader == null) {
			classLoader = ClassLoader.getSystemClassLoader();
		}

		String classLoaderName = classLoader.getClass().getName();

		try {
			// 针对JDK 9+的兼容性 - 尝试使用适用于各种Java版本的方法
			try {
				// 尝试查找并使用目标类加载器的addURL方法
				Method method = classLoader.getClass().getDeclaredMethod("addURL", URL.class);
				method.setAccessible(true);

				for (URL url : urls) {
					method.invoke(classLoader, url);
					LogUtil.info("已添加URL到类加载器: " + url);
				}
				return;
			}
			catch (NoSuchMethodException e) {
				// 如果找不到addURL方法，继续尝试其他方法
				LogUtil.debug("类加载器没有addURL方法: " + classLoaderName + ", 尝试其他方法");
			}

			// 针对URLClassLoader (JDK 8及更早版本常见)
			if (classLoader instanceof URLClassLoader) {
				URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
				Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
				method.setAccessible(true);

				for (URL url : urls) {
					method.invoke(urlClassLoader, url);
					LogUtil.info("已添加URL到URLClassLoader: " + url);
				}
				return;
			}

			// 针对不使用URLClassLoader的JDK 9+
			// 方法1：尝试使用反射查找和操作内部URLClassPath
			try {
				// 搜索包含"ucp"或类名含有"URLClassPath"的字段
				Field ucpField = null;

				for (Field field : classLoader.getClass().getDeclaredFields()) {
					String fieldName = field.getName().toLowerCase();
					String fieldTypeName = field.getType().getName();

					if (fieldName.contains("ucp") || fieldTypeName.contains("URLClassPath")
							|| fieldTypeName.contains("classloader")) {
						ucpField = field;
						LogUtil.debug("找到潜在的UCP字段: " + field.getName() + ", 类型: " + fieldTypeName);
						break;
					}
				}

				if (ucpField != null) {
					ucpField.setAccessible(true);
					Object ucp = ucpField.get(classLoader);

					// 查找addURL或类似的方法
					Method addURLMethod = null;
					for (Method m : ucp.getClass().getDeclaredMethods()) {
						if (m.getName().contains("add") && m.getParameterCount() == 1
								&& m.getParameterTypes()[0] == URL.class) {
							addURLMethod = m;
							LogUtil.debug("在UCP对象中找到add方法: " + m.getName());
							break;
						}
					}

					if (addURLMethod != null) {
						addURLMethod.setAccessible(true);
						for (URL url : urls) {
							addURLMethod.invoke(ucp, url);
							LogUtil.info("已使用URLClassPath添加URL: " + url);
						}
						return;
					}
				}
			}
			catch (Exception e) {
				// 此方法失败，记录错误并继续尝试其他方法
				LogUtil.debug("使用URLClassPath方法添加URL失败: " + e.getMessage());
			}

			// 方法2：使用UrlClassLoader作为桥接类加载器
			try {
				LogUtil.debug("尝试创建新的URLClassLoader作为代理加载器");
				// 创建一个包含所有URL的新URLClassLoader，使用当前类加载器作为父加载器
				URLClassLoader bridgeLoader = new URLClassLoader(urls, classLoader);

				// 设置线程上下文类加载器为新创建的类加载器
				// 注意：这只会影响当前线程，而不是全局类加载器
				Thread.currentThread().setContextClassLoader(bridgeLoader);

				LogUtil.info("已创建桥接类加载器并设置为线程上下文类加载器");
				return;
			}
			catch (Exception e) {
				LogUtil.error("创建桥接类加载器失败: " + e.getMessage());
			}

			// 方法3：对于AppClassLoader，尝试通过instrumention API
			if (classLoaderName.contains("jdk.internal.loader.ClassLoaders$AppClassLoader")) {
				try {
					// 使用服务提供者接口 (SPI) 尝试获取Instrumentation实例
					Class<?> instrClass = Class.forName("java.lang.instrument.Instrumentation");
					Class<?> instrProviderClass = Class.forName("java.lang.instrument.InstrumentationProvider");

					// 查找所有Instrumentation提供者
					ServiceLoader<?> loader = ServiceLoader.load(instrProviderClass);
					Iterator<?> iterator = loader.iterator();

					if (iterator.hasNext()) {
						Object provider = iterator.next();
						Method getInstMethod = instrProviderClass.getMethod("getInstrumentation");
						Object instr = getInstMethod.invoke(provider);

						// 使用instrumentation对象来修改类加载器
						Method appendToMethod = instrClass.getMethod("appendToSystemClassLoaderSearch",
								java.util.jar.JarFile.class);

						for (URL url : urls) {
							if (url.getProtocol().equals("file")) {
								File file = new File(url.toURI());
								if (file.exists() && file.getName().endsWith(".jar")) {
									JarFile jarFile = new JarFile(file);
									appendToMethod.invoke(instr, jarFile);
									LogUtil.info("使用Instrumentation API添加JAR: " + file.getAbsolutePath());
								}
							}
						}
						return;
					}
				}
				catch (Exception e) {
					LogUtil.debug("使用Instrumentation API添加URL失败: " + e.getMessage());
				}

				// 尝试另一种方法：使用jdk.internal.module.Modules类 (需要特权访问)
				try {
					// 获取AppClassLoader的模块
					Class<?> modulesClass = Class.forName("jdk.internal.module.Modules");
					Method addModuleExportsMethod = modulesClass.getDeclaredMethod("addExportsToAllUnnamed",
							java.lang.Module.class, String.class);
					addModuleExportsMethod.setAccessible(true);

					// 使模块可访问
					java.lang.Module javaBase = ClassLoader.class.getModule();
					addModuleExportsMethod.invoke(null, javaBase, "jdk.internal.loader");

					LogUtil.debug("已添加jdk.internal.loader包的exports权限");

					// 现在可以尝试访问AppClassLoader的内部结构
					Class<?> appLoaderClass = Class.forName("jdk.internal.loader.ClassLoaders$AppClassLoader");
					Field ucpField = appLoaderClass.getDeclaredField("ucp");
					ucpField.setAccessible(true);

					Object ucp = ucpField.get(classLoader);
					Method addURLMethod = ucp.getClass().getMethod("addURL", URL.class);
					addURLMethod.setAccessible(true);

					for (URL url : urls) {
						addURLMethod.invoke(ucp, url);
						LogUtil.info("已通过模块系统访问添加URL: " + url);
					}
					return;
				}
				catch (Exception e) {
					LogUtil.debug("通过模块系统访问添加URL失败: " + e.getMessage());
				}
			}

			// 如果到达这里，说明所有方法都失败了
			// 在这种情况下，创建一个独立的自定义类加载器
			URLClassLoader customLoader = new URLClassLoader(urls);
			LogUtil.warn("无法向系统类加载器添加URL，已创建独立的类加载器: " + customLoader);
			LogUtil.warn("注意：使用此类加载器加载的类将与主应用程序类隔离");

			// 通过日志提供更多详细信息
			LogUtil.error("类加载器详情: " + classLoaderName);
			if (classLoader instanceof URLClassLoader) {
				URL[] existingURLs = ((URLClassLoader) classLoader).getURLs();
				LogUtil.debug("已有的URLs: " + java.util.Arrays.toString(existingURLs));
			}

		}
		catch (Exception e) {
			LogUtil.error("向类加载器添加URL时出错: " + e.getMessage());
			e.printStackTrace();
		}
	}

	static URLClassLoader loader = null;

	/**
	 * 在默认的目录加载jar
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
	 * @param url 指定路径
	 * @param isFile true 文件 false 目录
	 * @return
	 */
	public static URLClassLoader getClassLoad(String url, boolean isFile) {
		URLClassLoaderUtil urlClass = new URLClassLoaderUtil(url, isFile);
		URLClassLoader loader = urlClass.getClassLoader();
		return loader;
	}

	/**
	 * URLClassLoader classLoader =
	 * ClassLoadUtil.loadClass("E:\\Projects\\MyEclipse9_32\\JAVA.NET_B_ORM\\schema_jar\\permission_schema.jar",
	 * true);
	 *
	 * Class<?> clazz = classLoader.loadClass("io.arkx.orm.db.permission.schema.UserSet");
	 * System.out.println(clazz);
	 *
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

	/**
	 * 诊断特定类无法加载的原因
	 * @param className 要诊断的完整类名
	 * @return 诊断结果详细信息
	 */
	public static String diagnoseClassNotFound(String className) {
		StringBuilder diagnosis = new StringBuilder("开始诊断类加载问题: " + className + "\n");

		// 1. 检查类是否可以直接加载
		try {
			Class<?> cls = Class.forName(className);
			return "类已成功加载: " + cls.getName() + ", 来自类加载器: " + cls.getClassLoader().getClass().getName();
		}
		catch (ClassNotFoundException | NoClassDefFoundError e) {
			diagnosis.append("无法直接加载类, 原因: ")
				.append(e.getClass().getName())
				.append(" - ")
				.append(e.getMessage())
				.append("\n");
		}

		// 2. 检查当前类加载器的类路径
		ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
		diagnosis.append("当前线程上下文类加载器: ").append(contextLoader.getClass().getName()).append("\n");

		if (contextLoader instanceof URLClassLoader) {
			URLClassLoader urlLoader = (URLClassLoader) contextLoader;
			diagnosis.append("URLClassLoader的类路径:\n");
			for (URL url : urlLoader.getURLs()) {
				diagnosis.append("  - ").append(url.toString()).append("\n");
			}
		}
		else {
			diagnosis.append("当前类加载器不是URLClassLoader，无法直接获取类路径\n");

			// 尝试通过反射获取AppClassLoader或其他类加载器的URLs
			try {
				Field field = contextLoader.getClass().getDeclaredField("ucp");
				field.setAccessible(true);
				Object ucp = field.get(contextLoader);
				Method getURLs = ucp.getClass().getDeclaredMethod("getURLs");
				URL[] urls = (URL[]) getURLs.invoke(ucp);

				diagnosis.append("通过反射获取的类路径:\n");
				for (URL url : urls) {
					diagnosis.append("  - ").append(url.toString()).append("\n");
				}
			}
			catch (Exception ex) {
				diagnosis.append("无法通过反射获取类路径信息: ").append(ex.getMessage()).append("\n");
			}
		}

		// 3. 检查类文件在JAR中是否存在
		String classAsPath = className.replace('.', '/') + ".class";
		diagnosis.append("尝试在类路径中查找文件: ").append(classAsPath).append("\n");

		URL classUrl = contextLoader.getResource(classAsPath);
		if (classUrl != null) {
			diagnosis.append("在类路径中找到类文件，位置: ").append(classUrl).append("\n");
			diagnosis.append("但仍然无法加载，可能是依赖项缺失或命名空间/可见性问题\n");
		}
		else {
			diagnosis.append("在类路径中未找到类文件\n");
		}

		// 4. 检查包的可访问性
		String packageName = "";
		if (className.contains(".")) {
			packageName = className.substring(0, className.lastIndexOf('.'));
			diagnosis.append("检查包的可访问性: ").append(packageName).append("\n");

			try {
				Package pkg = Package.getPackage(packageName);
				if (pkg != null) {
					diagnosis.append("包存在于JVM中，规范标题: ").append(pkg.getSpecificationTitle()).append("\n");
				}
				else {
					diagnosis.append("包在JVM中不存在\n");
				}
			}
			catch (Exception e) {
				diagnosis.append("检查包信息时出错: ").append(e.getMessage()).append("\n");
			}

			// 检查是否能访问包中的其他类
			diagnosis.append("尝试访问同包中的package-info类...\n");
			try {
				Class.forName(packageName + ".package-info");
				diagnosis.append("成功访问package-info类，说明包可访问\n");
			}
			catch (ClassNotFoundException e) {
				diagnosis.append("无法访问package-info类: ").append(e.getMessage()).append("\n");
			}
		}

		// 5. 检查系统属性和环境变量
		diagnosis.append("系统属性:\n");
		diagnosis.append("  java.class.path: ").append(System.getProperty("java.class.path")).append("\n");
		diagnosis.append("  java.version: ").append(System.getProperty("java.version")).append("\n");
		diagnosis.append("  java.home: ").append(System.getProperty("java.home")).append("\n");
		diagnosis.append("  user.dir: ").append(System.getProperty("user.dir")).append("\n");

		// 6. 尝试搜索类路径中包含目标类的JAR文件
		diagnosis.append("尝试在类路径中查找可能包含此类的JAR文件:\n");

		try {
			if (contextLoader instanceof URLClassLoader) {
				URLClassLoader urlLoader = (URLClassLoader) contextLoader;
				for (URL url : urlLoader.getURLs()) {
					if (url.getProtocol().equals("file") && url.getPath().endsWith(".jar")) {
						try {
							File file = new File(url.toURI());
							JarFile jarFile = new JarFile(file);

							JarEntry entry = jarFile.getJarEntry(classAsPath);
							if (entry != null) {
								diagnosis.append("  找到包含此类的JAR文件: ").append(file.getAbsolutePath()).append("\n");

								// 检查JAR文件中是否包含同包中的其他类
								String packagePath = packageName.replace('.', '/') + "/";
								int otherClassesInPackage = 0;
								Enumeration<JarEntry> entries = jarFile.entries();
								StringBuilder packageClasses = new StringBuilder();

								while (entries.hasMoreElements()) {
									JarEntry je = entries.nextElement();
									if (je.getName().startsWith(packagePath) && je.getName().endsWith(".class")
											&& !je.getName().equals(classAsPath)) {
										otherClassesInPackage++;
										packageClasses.append("    - ").append(je.getName()).append("\n");
										if (otherClassesInPackage >= 5) {
											packageClasses.append("    - (更多类省略...)\n");
											break;
										}
									}
								}

								if (otherClassesInPackage > 0) {
									diagnosis.append("  在同一JAR中找到").append(otherClassesInPackage).append("个同包中的其他类:\n");
									diagnosis.append(packageClasses.toString());
								}
								else {
									diagnosis.append("  在JAR中未找到同包中的其他类\n");
								}
							}

							jarFile.close();
						}
						catch (Exception e) {
							diagnosis.append("  检查JAR文件时出错: ")
								.append(url)
								.append(" - ")
								.append(e.getMessage())
								.append("\n");
						}
					}
				}
			}
		}
		catch (Exception e) {
			diagnosis.append("搜索JAR文件时出错: ").append(e.getMessage()).append("\n");
		}

		// 7. 检查类名是否符合命名规范
		if (className.contains("_")) {
			diagnosis.append("警告：类名包含下划线（'_'），这在Java中不是标准命名约定，但不会导致技术上的加载问题\n");
		}

		// 8. 检查是否涉及不同的类加载器
		diagnosis.append("当前类加载器继承链:\n");
		ClassLoader loader = contextLoader;
		int depth = 0;
		while (loader != null) {
			diagnosis.append("  ").append(depth++).append(": ").append(loader.getClass().getName()).append("\n");
			loader = loader.getParent();
		}

		// 9. 提供可能的解决方案
		diagnosis.append("\n可能的解决方案:\n");
		diagnosis.append("1. 检查类名和包名是否正确拼写\n");
		diagnosis.append("2. 确保JAR文件已正确添加到类路径中\n");
		diagnosis.append("3. 检查类的依赖项是否可用\n");
		diagnosis.append("4. 如果使用自定义类加载器，确保其正确委托给父类加载器\n");
		diagnosis.append("5. 检查是否存在类加载器隔离问题\n");
		diagnosis.append("6. 使用'-verbose:class'JVM参数运行应用程序，查看类加载详情\n");

		return diagnosis.toString();
	}

	/**
	 * 尝试加载类，如果遇到ClassNotFoundException，则自动执行诊断
	 * @param className 要加载的类名
	 * @param ignoreErrors 是否忽略错误
	 * @param loggers 日志记录器数组
	 * @return 加载的类，如果加载失败则返回null
	 */
	public static Class<?> loadClassWithDiagnosis(String className, boolean ignoreErrors, Logger... loggers) {
		try {
			return Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			// 执行详细诊断
			String diagnosis = diagnoseClassNotFound(className);

			// 记录诊断信息
			Logger logger = getLogger(loggers);
			if (logger != null) {
				logger.error("加载类 '{}' 失败: {}，\n详细诊断：\n{}", className, e.getMessage(), diagnosis);
			}
			else {
				System.err.println("加载类 '" + className + "' 失败: " + e.getMessage());
				System.err.println("详细诊断：\n" + diagnosis);
			}

			if (!ignoreErrors) {
				throw new RuntimeException("无法加载类 '" + className + "': " + e.getMessage(), e);
			}
			return null;
		}
	}

	/**
	 * 尝试加载类，如果遇到ClassNotFoundException，则自动执行诊断并返回诊断结果
	 * @param className 要加载的类名
	 * @return 成功时返回加载的类，失败时返回null
	 * @throws DiagnosedClassLoadException 包含详细诊断信息的异常
	 */
	public static Class<?> loadClassWithDiagnosisResult(String className) throws DiagnosedClassLoadException {
		try {
			return Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			// 执行详细诊断
			String diagnosis = diagnoseClassNotFound(className);
			throw new DiagnosedClassLoadException("无法加载类 '" + className + "': " + e.getMessage(), e, diagnosis);
		}
	}

	/**
	 * 包含详细类加载诊断信息的异常
	 */
	public static class DiagnosedClassLoadException extends Exception {

		private static final long serialVersionUID = 1L;

		private final String diagnosis;

		public DiagnosedClassLoadException(String message, Throwable cause, String diagnosis) {
			super(message, cause);
			this.diagnosis = diagnosis;
		}

		/**
		 * 获取详细的诊断信息
		 * @return 诊断信息字符串
		 */
		public String getDiagnosis() {
			return diagnosis;
		}

		@Override
		public String toString() {
			return super.toString() + "\n诊断信息：\n" + diagnosis;
		}

	}

	/**
	 * 获取有效的日志记录器
	 */
	private static Logger getLogger(Logger... loggers) {
		if (loggers != null && loggers.length > 0 && loggers[0] != null) {
			return loggers[0];
		}
		return LoggerFactory.getLogger(ClassLoadUtil.class);
	}

	/**
	 * 专门为Spring Boot环境设计的JAR包类加载方法
	 * @param jarFilePath jar文件路径
	 * @return 加载的类数量
	 */
	public static int loadJarInSpringBootEnv(String jarFilePath) {
		LogUtil.info("使用Spring Boot专用加载器加载JAR文件: " + jarFilePath);

		// 保存原始上下文类加载器
		ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();

		try {
			File jarFile = new File(jarFilePath);
			if (!jarFile.exists() || !jarFile.isFile()) {
				LogUtil.error("JAR文件不存在或不是文件: " + jarFilePath);
				return 0;
			}

			// 创建URL并确保正确格式
			URL jarUrl = jarFile.toURI().toURL();
			LogUtil.info("JAR文件URL: " + jarUrl);

			// 检测Spring Boot环境
			boolean isSpringBootEnv = false;
			ClassLoader currentLoader = originalContextLoader;
			while (currentLoader != null) {
				if (currentLoader.getClass().getName().contains("LaunchedURLClassLoader")) {
					isSpringBootEnv = true;
					LogUtil.info("检测到Spring Boot环境，类加载器: " + currentLoader.getClass().getName());
					break;
				}
				currentLoader = currentLoader.getParent();
			}

			if (!isSpringBootEnv) {
				LogUtil.warn("未检测到Spring Boot环境，将使用标准类加载方法");
				return loadAndPrintClasses(jarFilePath);
			}

			// 在Spring Boot环境中使用自定义策略

			// 1. 尝试获取Spring Boot类加载器的addURL方法
			URLClassLoader bootLoader = null;
			try {
				bootLoader = (URLClassLoader) currentLoader;
				Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
				addUrlMethod.setAccessible(true);
				addUrlMethod.invoke(bootLoader, jarUrl);
				LogUtil.info("成功将JAR添加到Spring Boot类加载器");
			}
			catch (Exception e) {
				LogUtil.warn("无法直接添加到Spring Boot类加载器: " + e.getMessage());
				LogUtil.info("尝试创建新的类加载器");

				// 2. 如果直接添加失败，创建新的类加载器并设置为上下文类加载器
				try {
					bootLoader = new URLClassLoader(new URL[] { jarUrl }, originalContextLoader);
					Thread.currentThread().setContextClassLoader(bootLoader);
					LogUtil.info("已创建新的类加载器并设置为上下文类加载器");
				}
				catch (Exception ex) {
					LogUtil.error("创建新类加载器失败: " + ex.getMessage());
					return 0;
				}
			}

			// 3. 读取JAR中的类并尝试加载
			int classCount = 0;

			try (JarFile jar = new JarFile(jarFile)) {
				Enumeration<JarEntry> entries = jar.entries();
				List<String> classNames = new ArrayList<>();

				// 收集所有类名
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (entry.getName().endsWith(".class")) {
						String className = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');
						classNames.add(className);
					}
				}

				LogUtil.info("在JAR中找到 " + classNames.size() + " 个类");

				// 按顺序加载类
				for (String className : classNames) {
					try {
						// 首先检查类是否已经加载
						try {
							Class.forName(className, false, originalContextLoader);
							LogUtil.debug("类 " + className + " 已经在原始类加载器中加载");
							classCount++;
							continue;
						}
						catch (ClassNotFoundException ignored) {
							// 类未加载，继续尝试加载
						}

						// 尝试使用Spring Boot类加载器加载
						Class<?> loadedClass = bootLoader.loadClass(className);

						// 如果能到达这里，说明类加载成功
						classCount++;
						LogUtil
							.info("已加载类: " + className + ", 加载器: " + loadedClass.getClassLoader().getClass().getName());

					}
					catch (ClassNotFoundException e) {
						LogUtil.debug("无法加载类: " + className + ", 原因: " + e.getMessage());

						// 尝试手动加载
						try {
							JarEntry entry = jar.getJarEntry(className.replace('.', '/') + ".class");
							if (entry != null) {
								try (InputStream is = jar.getInputStream(entry)) {
									byte[] classBytes = readAllBytes(is);

									// 使用反射调用defineClass方法
									Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass",
											String.class, byte[].class, int.class, int.class);
									defineClassMethod.setAccessible(true);

									Class<?> definedClass = (Class<?>) defineClassMethod.invoke(bootLoader, className,
											classBytes, 0, classBytes.length);

									if (definedClass != null) {
										classCount++;
										LogUtil.info("通过手动定义成功加载类: " + className);
									}
								}
							}
						}
						catch (Exception ex) {
							LogUtil.warn("手动加载类 " + className + " 失败: " + ex.getMessage());
						}
					}
					catch (NoClassDefFoundError e) {
						LogUtil.warn("类定义找不到: " + className + ", 可能缺少依赖项: " + e.getMessage());
					}
					catch (Exception e) {
						LogUtil.warn("加载类 " + className + " 时出现异常: " + e.getMessage());
					}
				}
			}
			catch (IOException e) {
				LogUtil.error("读取JAR文件失败: " + e.getMessage());
				return classCount;
			}

			LogUtil.info("成功从JAR文件 " + jarFilePath + " 加载了 " + classCount + " 个类");
			return classCount;

		}
		catch (Exception e) {
			LogUtil.error("加载JAR文件时发生错误: " + e.getMessage());
			return 0;
		}
		finally {
			// 恢复原始上下文类加载器
			Thread.currentThread().setContextClassLoader(originalContextLoader);
		}
	}

	/**
	 * 智能选择合适的类加载方法，自动检测Spring Boot环境
	 * @param jarFilePath jar文件路径
	 * @return 加载的类数量
	 */
	public static int smartLoadJar(String jarFilePath) {
		// 检查当前是否在Spring Boot环境中
		boolean isSpringBootEnv = false;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		while (loader != null) {
			if (loader.getClass().getName().contains("LaunchedURLClassLoader")) {
				isSpringBootEnv = true;
				break;
			}
			loader = loader.getParent();
		}

		if (isSpringBootEnv) {
			LogUtil.info("检测到Spring Boot环境，使用专用加载器");
			return loadJarInSpringBootEnv(jarFilePath);
		}
		else {
			LogUtil.info("标准环境，使用通用加载器");
			return loadAndPrintClasses(jarFilePath);
		}
	}

	/**
	 * 从Spring Boot fat JAR内部提取和加载嵌套的JAR文件 Spring
	 * Boot将JAR文件嵌套在BOOT-INF/lib/目录下，这个方法用于处理这种情况
	 * @param springBootJarPath Spring Boot fat JAR文件路径
	 * @param nestedJarPattern 要提取的嵌套JAR文件名模式（支持简单通配符*）
	 * @return 加载的类数量
	 */
	public static int extractAndLoadFromSpringBootJar(String springBootJarPath, String nestedJarPattern) {
		LogUtil.info("从Spring Boot JAR提取和加载嵌套的JAR文件: " + springBootJarPath + ", 模式: " + nestedJarPattern);

		int totalClassCount = 0;
		JarFile springBootJar = null;
		File tempDir = null;

		try {
			// 创建临时目录用于提取嵌套JAR文件
			tempDir = createTempDirectory("spring-boot-extract-");
			LogUtil.info("创建临时目录用于提取JAR: " + tempDir.getAbsolutePath());

			// 打开Spring Boot JAR文件
			springBootJar = new JarFile(new File(springBootJarPath));

			// 查找BOOT-INF/lib/目录下的嵌套JAR文件
			// 注意：JAR内部路径始终使用'/'作为分隔符，不受操作系统影响
			List<JarEntry> nestedJars = new ArrayList<>();
			Enumeration<JarEntry> entries = springBootJar.entries();

			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();

				// 检查是否匹配BOOT-INF/lib/下的JAR文件
				// JAR内部路径固定使用'/'分隔符
				if (name.startsWith("BOOT-INF/lib/") && name.endsWith(".jar")) {
					// 提取JAR文件名进行模式匹配
					// JAR内部路径使用'/'分隔符
					String jarFileName = name.substring(name.lastIndexOf('/') + 1);

					if (matchesPattern(jarFileName, nestedJarPattern)) {
						nestedJars.add(entry);
						LogUtil.info("找到匹配的嵌套JAR: " + name);
					}
				}
			}

			if (nestedJars.isEmpty()) {
				LogUtil.warn("在Spring Boot JAR中未找到匹配的嵌套JAR文件");
				return 0;
			}

			LogUtil.info("找到 " + nestedJars.size() + " 个匹配的嵌套JAR文件");

			// 提取并加载每个匹配的嵌套JAR
			for (JarEntry nestedJarEntry : nestedJars) {
				String nestedJarName = nestedJarEntry.getName();
				// JAR内部路径使用'/'分隔符
				String simpleJarName = nestedJarName.substring(nestedJarName.lastIndexOf('/') + 1);
				// 本地文件系统路径使用File构造函数自动处理
				File extractedJar = new File(tempDir, simpleJarName);

				// 提取JAR文件
				try (InputStream is = springBootJar.getInputStream(nestedJarEntry);
						OutputStream fos = new FileOutputStream(extractedJar)) {

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						fos.write(buffer, 0, bytesRead);
					}

					LogUtil.info("已提取嵌套JAR到: " + extractedJar.getAbsolutePath());

					// 加载提取的JAR文件
					int classCount = smartLoadJar(extractedJar.getAbsolutePath());
					totalClassCount += classCount;

					LogUtil.info("从嵌套JAR " + simpleJarName + " 加载了 " + classCount + " 个类");
				}
				catch (IOException e) {
					LogUtil.error("提取或加载嵌套JAR失败: " + simpleJarName + ", 原因: " + e.getMessage());
				}
			}

			LogUtil.info("完成嵌套JAR处理，总共加载了 " + totalClassCount + " 个类");
			return totalClassCount;

		}
		catch (IOException e) {
			LogUtil.error("处理Spring Boot JAR文件时出错: " + e.getMessage());
			return totalClassCount;
		}
		finally {
			// 关闭JAR文件
			if (springBootJar != null) {
				try {
					springBootJar.close();
				}
				catch (IOException e) {
					// 忽略关闭异常
				}
			}

			// 删除临时文件（可选，根据需要保留或删除）
			if (tempDir != null && tempDir.exists()) {
				// 如果需要保留文件进行调试，可以注释掉删除代码
				boolean deleteSuccess = deleteDirectory(tempDir);
				if (deleteSuccess) {
					LogUtil.debug("已删除临时目录: " + tempDir.getAbsolutePath());
				}
				else {
					LogUtil.warn("无法删除临时目录: " + tempDir.getAbsolutePath());
				}
			}
		}
	}

	// 创建临时目录
	private static File createTempDirectory(String prefix) throws IOException {
		File tempDir = File.createTempFile(prefix, "");
		if (!tempDir.delete()) {
			throw new IOException("无法删除临时文件: " + tempDir.getAbsolutePath());
		}
		if (!tempDir.mkdir()) {
			throw new IOException("无法创建临时目录: " + tempDir.getAbsolutePath());
		}
		return tempDir;
	}

	// 递归删除目录
	private static boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteDirectory(file);
					}
					else {
						if (!file.delete()) {
							return false;
						}
					}
				}
			}
		}
		return directory.delete();
	}

	// 简单的通配符模式匹配
	private static boolean matchesPattern(String text, String pattern) {
		// 如果模式为空或*，匹配所有
		if (pattern == null || pattern.isEmpty() || pattern.equals("*")) {
			return true;
		}

		// 转换为正则表达式
		String regex = pattern.replace(".", "\\.").replace("*", ".*");
		return text.matches(regex);
	}

	/**
	 * 专门用于从用户上传的JAR包加载类的便捷方法 适用于各种环境，包括Spring Boot
	 * @param jarPath JAR文件路径
	 * @param className 要加载的特定类名（可选，如果为null则加载所有类）
	 * @return 如果className为null，返回加载的类数量；否则返回是否成功加载了指定的类
	 */
	public static Object loadUserUploadedJar(String jarPath, String className) {
		LogUtil.info("加载用户上传的JAR: " + jarPath + (className != null ? ", 目标类: " + className : ""));

		// 检测当前运行环境
		boolean isSpringBootEnv = false;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		while (loader != null) {
			if (loader.getClass().getName().contains("LaunchedURLClassLoader")) {
				isSpringBootEnv = true;
				break;
			}
			loader = loader.getParent();
		}

		// 创建URL类加载器
		URLClassLoader jarLoader = null;
		try {
			URL jarUrl = new File(jarPath).toURI().toURL();

			// 保存原始类加载器
			ClassLoader originalLoader = Thread.currentThread().getContextClassLoader();

			try {
				// 根据环境选择适当的父加载器
				if (isSpringBootEnv) {
					LogUtil.info("在Spring Boot环境中加载JAR");
					jarLoader = new URLClassLoader(new URL[] { jarUrl }, originalLoader);
				}
				else {
					LogUtil.info("在标准环境中加载JAR");
					jarLoader = new URLClassLoader(new URL[] { jarUrl }, ClassLoader.getSystemClassLoader());
				}

				// 临时设置为线程上下文类加载器
				Thread.currentThread().setContextClassLoader(jarLoader);

				// 如果指定了类名，尝试加载特定类
				if (className != null) {
					try {
						Class<?> loadedClass = jarLoader.loadClass(className);
						LogUtil.info("成功加载类: " + className);
						return true;
					}
					catch (ClassNotFoundException e) {
						LogUtil.error("无法加载指定的类: " + className + ", 原因: " + e.getMessage());

						// 尝试在JAR中查找该类
						try (JarFile jar = new JarFile(new File(jarPath))) {
							// JAR内部路径固定使用'/'分隔符，不受操作系统影响
							String classPath = className.replace('.', '/') + ".class";
							JarEntry entry = jar.getJarEntry(classPath);

							if (entry != null) {
								LogUtil.info("类文件存在于JAR中，但无法加载，可能是类加载器问题");

								// 提供详细诊断
								String diagnosis = diagnoseClassNotFound(className);
								LogUtil.info("类加载诊断:\n" + diagnosis);

								// 尝试使用手动定义类的方式加载
								try (InputStream is = jar.getInputStream(entry)) {
									byte[] classBytes = readAllBytes(is);

									Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass",
											String.class, byte[].class, int.class, int.class);
									defineClassMethod.setAccessible(true);

									Class<?> definedClass = (Class<?>) defineClassMethod.invoke(jarLoader, className,
											classBytes, 0, classBytes.length);

									if (definedClass != null) {
										LogUtil.info("通过手动定义成功加载类: " + className);
										return true;
									}
								}
								catch (Exception ex) {
									LogUtil.error("手动定义类失败: " + ex.getMessage());
								}
							}
							else {
								LogUtil.error("在JAR中未找到类文件: " + classPath);
							}
						}
						catch (IOException ex) {
							LogUtil.error("读取JAR文件失败: " + ex.getMessage());
						}

						return false;
					}
				}
				else {
					// 加载JAR中的所有类
					return smartLoadJar(jarPath);
				}
			}
			finally {
				// 恢复原始类加载器
				Thread.currentThread().setContextClassLoader(originalLoader);
			}
		}
		catch (Exception e) {
			LogUtil.error("加载JAR文件时出错: " + e.getMessage());
			return className != null ? false : 0;
		}
	}

	/**
	 * 创建一个适用于Spring Boot环境的自定义类加载器
	 * @param jarPath 要加载的JAR文件路径
	 * @return 配置好的类加载器
	 * @throws MalformedURLException 如果URL格式错误
	 */
	public static ClassLoader createSpringBootCompatibleClassLoader(String jarPath) throws MalformedURLException {
		LogUtil.info("为Spring Boot环境创建兼容的类加载器: " + jarPath);

		// 获取当前上下文类加载器和类加载器链
		ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
		List<ClassLoader> classLoaderChain = new ArrayList<>();
		ClassLoader tempLoader = contextLoader;

		while (tempLoader != null) {
			classLoaderChain.add(tempLoader);
			String loaderName = tempLoader.getClass().getName();
			LogUtil.debug("类加载器链: " + loaderName);

			// 检测是否为Spring Boot类加载器
			if (loaderName.contains("LaunchedURLClassLoader")) {
				LogUtil.info("检测到Spring Boot类加载器: " + loaderName);
			}

			tempLoader = tempLoader.getParent();
		}

		// 创建指向JAR文件的URL
		URL jarUrl = new File(jarPath).toURI().toURL();

		// 检测是否有可用的Spring Boot加载器
		ClassLoader springBootLoader = null;
		for (ClassLoader loader : classLoaderChain) {
			if (loader.getClass().getName().contains("LaunchedURLClassLoader")) {
				springBootLoader = loader;
				break;
			}
		}

		// 选择合适的父加载器
		ClassLoader parentLoader = springBootLoader != null ? springBootLoader : contextLoader;

		// 创建自定义类加载器
		URLClassLoader customLoader = new URLClassLoader(new URL[] { jarUrl }, parentLoader);
		LogUtil.info("已创建Spring Boot兼容的类加载器");

		// 尝试将自定义类加载器注册到Spring Boot的资源处理器（如果可能）
		if (springBootLoader != null) {
			try {
				// 获取Spring Boot类加载器中的资源处理器（仅适用于某些版本）
				Class<?> launchedURLClassLoaderClass = springBootLoader.getClass();

				// 尝试注册资源
				try {
					Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
					addUrlMethod.setAccessible(true);
					addUrlMethod.invoke(springBootLoader, jarUrl);
					LogUtil.info("已将JAR直接添加到Spring Boot类加载器");
				}
				catch (Exception e) {
					LogUtil.debug("无法直接添加到Spring Boot类加载器: " + e.getMessage());
				}

				// 尝试通过反射获取更多资源处理器并注册
				for (Field field : launchedURLClassLoaderClass.getDeclaredFields()) {
					String fieldName = field.getName();
					if (fieldName.contains("resource") || fieldName.contains("archive") || fieldName.contains("handler")
							|| fieldName.contains("resolver")) {
						LogUtil.debug("发现潜在的资源处理字段: " + fieldName);
					}
				}

			}
			catch (Exception e) {
				LogUtil.debug("尝试与Spring Boot加载器集成时出错: " + e.getMessage());
			}
		}

		return customLoader;
	}

	/**
	 * 为用户上传的JAR包创建适用于当前环境的类加载器，自动检测并适应Spring Boot环境
	 * @param jarPath JAR文件路径
	 * @return 配置好的类加载器
	 * @throws MalformedURLException 如果URL格式错误
	 */
	public static ClassLoader createSmartClassLoader(String jarPath) throws MalformedURLException {
		// 检测环境
		boolean isSpringBootEnv = isSpringBootEnvironment();

		if (isSpringBootEnv) {
			LogUtil.info("检测到Spring Boot环境，创建兼容的类加载器");
			return createSpringBootCompatibleClassLoader(jarPath);
		}
		else {
			LogUtil.info("标准环境，创建普通类加载器");
			URL jarUrl = new File(jarPath).toURI().toURL();
			return new URLClassLoader(new URL[] { jarUrl }, Thread.currentThread().getContextClassLoader());
		}
	}

	/**
	 * 检测当前是否运行在Spring Boot环境中
	 * @return 如果是Spring Boot环境则返回true
	 */
	public static boolean isSpringBootEnvironment() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		while (loader != null) {
			if (loader.getClass().getName().contains("LaunchedURLClassLoader")) {
				return true;
			}
			loader = loader.getParent();
		}
		return false;
	}

	/**
	 * 在线程池环境(如ForkJoinPool)中加载JAR文件 专门处理在线程池工作线程中加载类的情况
	 * @param jarPath JAR文件路径
	 * @return 加载的类数量
	 */
	public static int loadJarInThreadPoolEnv(String jarPath) {
		LogUtil.info("在线程池环境中加载JAR: " + jarPath + ", 当前线程: " + Thread.currentThread().getName());

		// 保存当前线程的上下文类加载器
		ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();
		LogUtil
			.info("当前线程类加载器: " + (originalContextLoader != null ? originalContextLoader.getClass().getName() : "null"));

		try {
			// 获取主应用的类加载器
			ClassLoader appClassLoader = ClassLoader.getSystemClassLoader();
			LogUtil.info("系统类加载器: " + (appClassLoader != null ? appClassLoader.getClass().getName() : "null"));

			// 创建新的类加载器
			URL jarUrl = new File(jarPath).toURI().toURL();
			URLClassLoader jarClassLoader = new URLClassLoader(new URL[] { jarUrl }, appClassLoader);

			// 将新类加载器设置为当前线程的上下文类加载器
			Thread.currentThread().setContextClassLoader(jarClassLoader);
			LogUtil.info("已设置新的线程上下文类加载器: " + jarClassLoader.getClass().getName());

			// 读取JAR中的类
			int classCount = 0;
			JarFile jarFile = null;

			try {
				jarFile = new JarFile(jarPath);
				Enumeration<JarEntry> entries = jarFile.entries();
				List<String> classNames = new ArrayList<>();

				// 收集所有类名
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (entry.getName().endsWith(".class")) {
						String className = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');
						classNames.add(className);
					}
				}

				LogUtil.info("在JAR中找到 " + classNames.size() + " 个类");

				// 注册JAR到系统类加载器(如果可能)
				try {
					// 尝试向系统类加载器添加URL
					addJarsURL(new URL[] { jarUrl }, appClassLoader);
					LogUtil.info("已将JAR添加到系统类加载器");
				}
				catch (Exception e) {
					LogUtil.warn("无法将JAR添加到系统类加载器: " + e.getMessage());
				}

				// 逐个加载类
				for (String className : classNames) {
					try {
						// 使用新的类加载器加载类
						Class<?> loadedClass = jarClassLoader.loadClass(className);
						classCount++;

						// 确保类可以在任何线程中访问 - 创建全局引用
						try {
							// 将加载的类添加到一个静态映射中以防止垃圾回收
							registerLoadedClass(className, loadedClass);
						}
						catch (Exception e) {
							LogUtil.debug("注册全局类引用失败: " + e.getMessage());
						}

						LogUtil.info("已加载类: " + className);
					}
					catch (ClassNotFoundException e) {
						LogUtil.debug("无法加载类: " + className + ", 原因: " + e.getMessage());

						// 尝试手动定义类
						try {
							JarEntry entry = jarFile.getJarEntry(className.replace('.', '/') + ".class");
							if (entry != null) {
								try (InputStream is = jarFile.getInputStream(entry)) {
									byte[] classBytes = readAllBytes(is);

									// 使用反射调用defineClass
									Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass",
											String.class, byte[].class, int.class, int.class);
									defineClassMethod.setAccessible(true);

									// 1. 首先尝试在AppClassLoader中定义类
									try {
										Class<?> definedClass = (Class<?>) defineClassMethod.invoke(appClassLoader,
												className, classBytes, 0, classBytes.length);

										if (definedClass != null) {
											classCount++;
											registerLoadedClass(className, definedClass);
											LogUtil.info("在AppClassLoader中成功定义类: " + className);
											continue;
										}
									}
									catch (Exception ex) {
										LogUtil.debug("在AppClassLoader中定义类失败: " + ex.getMessage());
									}

									// 2. 尝试在线程上下文类加载器中定义类
									try {
										Class<?> definedClass = (Class<?>) defineClassMethod.invoke(jarClassLoader,
												className, classBytes, 0, classBytes.length);

										if (definedClass != null) {
											classCount++;
											registerLoadedClass(className, definedClass);
											LogUtil.info("在线程上下文类加载器中成功定义类: " + className);
										}
									}
									catch (Exception ex) {
										LogUtil.debug("在线程上下文类加载器中定义类失败: " + ex.getMessage());
									}
								}
							}
						}
						catch (Exception ex) {
							LogUtil.warn("手动定义类失败: " + className + ", " + ex.getMessage());
						}
					}
					catch (Exception e) {
						LogUtil.warn("加载类时出错: " + className + ", " + e.getMessage());
					}
				}

				LogUtil.info("从JAR文件 " + jarPath + " 加载了 " + classCount + " 个类");

			}
			catch (IOException e) {
				LogUtil.error("读取JAR文件失败: " + e.getMessage());
			}
			finally {
				if (jarFile != null) {
					try {
						jarFile.close();
					}
					catch (IOException e) {
						// 忽略关闭异常
					}
				}
			}

			return classCount;

		}
		catch (Exception e) {
			LogUtil.error("加载JAR文件时出错: " + e.getMessage());
			return 0;
		}
		finally {
			// 恢复原始上下文类加载器
			Thread.currentThread().setContextClassLoader(originalContextLoader);
		}
	}

	// 用于全局类引用的静态映射
	private static final Map<String, Class<?>> loadedClassesMap = new ConcurrentHashMap<>();

	/**
	 * 注册已加载的类到全局映射 确保类不会被垃圾回收，在任何线程中都可用
	 * @param className 类名
	 * @param clazz 类对象
	 */
	public static void registerLoadedClass(String className, Class<?> clazz) {
		loadedClassesMap.put(className, clazz);
		LogUtil.debug("已注册类到全局映射: " + className);
	}

	/**
	 * 从全局映射中获取已加载的类
	 * @param className 类名
	 * @return 类对象，如果未注册则返回null
	 */
	public static Class<?> getLoadedClass(String className) {
		return loadedClassesMap.get(className);
	}

	/**
	 * 智能JAR加载方法 - 增强版 检测当前环境并选择最合适的加载策略
	 * @param jarPath JAR文件路径
	 * @return 加载的类数量
	 */
	public static int smartLoadJarEnhanced(String jarPath) {
		// 获取当前线程信息
		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		ThreadGroup threadGroup = currentThread.getThreadGroup();
		String threadGroupName = threadGroup != null ? threadGroup.getName() : "unknown";

		LogUtil.info("智能加载JAR文件: " + jarPath);
		LogUtil.info("当前线程: " + threadName + ", 线程组: " + threadGroupName);

		// 检查是否在ForkJoinPool中运行
		boolean isInForkJoinPool = threadName.contains("ForkJoin")
				|| (threadGroupName != null && threadGroupName.contains("ForkJoin"));

		// 获取类加载器类型
		ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
		String loaderClassName = contextLoader != null ? contextLoader.getClass().getName() : "null";
		LogUtil.info("当前类加载器: " + loaderClassName);

		// 检查是否在Spring Boot环境中
		boolean isSpringBootEnv = isSpringBootEnvironment();

		// 基于环境选择策略
		if (isInForkJoinPool || threadName.contains("worker") || threadName.contains("pool")
				|| threadName.contains("async")) {
			LogUtil.info("检测到线程池环境，使用专用线程池加载策略");
			return loadJarInThreadPoolEnv(jarPath);
		}
		else if (isSpringBootEnv) {
			LogUtil.info("检测到Spring Boot环境，使用Spring Boot加载策略");
			return loadJarInSpringBootEnv(jarPath);
		}
		else {
			LogUtil.info("标准环境，使用通用加载策略");
			return loadAndPrintClasses(jarPath);
		}
	}

	/**
	 * 预加载并注册启动类 为动态加载的JAR提供启动入口，使其可以在应用的任何地方被调用
	 * @param jarPath JAR文件路径
	 * @param mainClassName 主类名（可选，如果为null则自动查找）
	 * @return 加载和注册的启动类
	 */
	public static Class<?> preloadAndRegisterMainClass(String jarPath, String mainClassName) {
		LogUtil.info("预加载并注册启动类: " + jarPath + (mainClassName != null ? ", 指定主类: " + mainClassName : ""));

		try {
			// 创建类加载器
			URL jarUrl = new File(jarPath).toURI().toURL();
			URLClassLoader jarClassLoader = new URLClassLoader(new URL[] { jarUrl },
					Thread.currentThread().getContextClassLoader());

			// 查找主类
			Class<?> mainClass = null;
			if (mainClassName != null) {
				// 加载指定的主类
				try {
					mainClass = jarClassLoader.loadClass(mainClassName);
					LogUtil.info("已加载指定的主类: " + mainClassName);
				}
				catch (ClassNotFoundException e) {
					LogUtil.error("无法加载指定的主类: " + mainClassName + ", 原因: " + e.getMessage());

					// 尝试在JAR中查找该类
					try (JarFile jar = new JarFile(new File(jarPath))) {
						String classPath = mainClassName.replace('.', '/') + ".class";
						JarEntry entry = jar.getJarEntry(classPath);

						if (entry != null) {
							// 尝试手动加载
							try (InputStream is = jar.getInputStream(entry)) {
								byte[] classBytes = readAllBytes(is);

								Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass",
										String.class, byte[].class, int.class, int.class);
								defineClassMethod.setAccessible(true);

								mainClass = (Class<?>) defineClassMethod.invoke(jarClassLoader, mainClassName,
										classBytes, 0, classBytes.length);

								LogUtil.info("通过手动定义成功加载主类: " + mainClassName);
							}
							catch (Exception ex) {
								LogUtil.error("手动加载主类失败: " + ex.getMessage());
							}
						}
					}
				}
			}
			else {
				// 自动查找主类
				try (JarFile jar = new JarFile(new File(jarPath))) {
					// 1. 尝试从MANIFEST.MF中获取Main-Class属性
					java.util.jar.Manifest manifest = jar.getManifest();
					if (manifest != null) {
						String manifestMainClass = manifest.getMainAttributes().getValue("Main-Class");
						if (manifestMainClass != null && !manifestMainClass.isEmpty()) {
							try {
								mainClass = jarClassLoader.loadClass(manifestMainClass);
								LogUtil.info("从MANIFEST.MF加载主类: " + manifestMainClass);
							}
							catch (ClassNotFoundException e) {
								LogUtil.warn("无法加载MANIFEST.MF中指定的主类: " + manifestMainClass);
							}
						}
					}

					// 2. 如果MANIFEST中没有找到，查找可能的主类
					if (mainClass == null) {
						List<String> possibleMainClasses = new ArrayList<>();

						// 遍历所有类文件
						Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements()) {
							JarEntry entry = entries.nextElement();
							if (entry.getName().endsWith(".class")) {
								String className = entry.getName()
									.substring(0, entry.getName().length() - 6)
									.replace('/', '.');

								// 检查是否包含main方法
								try {
									Class<?> clazz = jarClassLoader.loadClass(className);
									try {
										Method mainMethod = clazz.getMethod("main", String[].class);
										if (mainMethod != null) {
											possibleMainClasses.add(className);
											LogUtil.debug("找到具有main方法的类: " + className);
										}
									}
									catch (NoSuchMethodException ignored) {
										// 没有main方法，忽略
									}
								}
								catch (ClassNotFoundException | NoClassDefFoundError ignored) {
									// 加载失败，忽略
								}
							}
						}

						// 如果找到了可能的主类，选择第一个
						if (!possibleMainClasses.isEmpty()) {
							String selectedMainClass = possibleMainClasses.get(0);
							mainClass = jarClassLoader.loadClass(selectedMainClass);
							LogUtil.info("自动选择主类: " + selectedMainClass);
						}
					}
				}
			}

			// 注册主类到全局映射
			if (mainClass != null) {
				String jarFileName = new File(jarPath).getName();
				String key = "main:" + jarFileName;
				registerLoadedClass(key, mainClass);
				registerLoadedClass(mainClass.getName(), mainClass);

				LogUtil.info("已成功注册启动类: " + mainClass.getName() + " 用于JAR: " + jarFileName);
				return mainClass;
			}
			else {
				LogUtil.warn("未能找到或加载启动类");
				return null;
			}

		}
		catch (Exception e) {
			LogUtil.error("预加载启动类时出错: " + e.getMessage());
			return null;
		}
	}

	/**
	 * 执行预加载的启动类的main方法
	 * @param jarPath JAR文件路径或JAR文件名
	 * @param args 传递给main方法的参数
	 * @return 执行结果，true表示成功，false表示失败
	 */
	public static boolean executeJarMainClass(String jarPath, String[] args) {
		String jarFileName = new File(jarPath).getName();
		String key = "main:" + jarFileName;

		// 从全局映射中获取启动类
		Class<?> mainClass = getLoadedClass(key);

		if (mainClass == null) {
			LogUtil.warn("未找到JAR的预加载启动类: " + jarPath + "，尝试预加载");

			// 尝试预加载
			mainClass = preloadAndRegisterMainClass(jarPath, null);
			if (mainClass == null) {
				LogUtil.error("无法预加载JAR的启动类: " + jarPath);
				return false;
			}
		}

		// 保存当前线程的上下文类加载器
		ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();

		try {
			// 设置类加载器
			ClassLoader mainClassLoader = mainClass.getClassLoader();
			Thread.currentThread().setContextClassLoader(mainClassLoader);

			// 执行main方法
			Method mainMethod = mainClass.getMethod("main", String[].class);
			mainMethod.invoke(null, (Object) (args != null ? args : new String[0]));

			LogUtil.info("成功执行JAR的启动类: " + mainClass.getName());
			return true;
		}
		catch (Exception e) {
			LogUtil.error("执行JAR启动类失败: " + e.getMessage());
			return false;
		}
		finally {
			// 恢复原始上下文类加载器
			Thread.currentThread().setContextClassLoader(originalContextLoader);
		}
	}

	/**
	 * 特殊的类加载器，可以处理跨类加载器的依赖关系 解决Spring Boot环境下不同类加载器之间的隔离问题
	 */
	public static class BridgeClassLoader extends URLClassLoader {

		private final ClassLoader springBootLoader;

		private final List<String> delegatePackages;

		/**
		 * 创建一个桥接类加载器
		 * @param urls 要加载的URL
		 * @param springBootLoader Spring Boot的类加载器
		 * @param delegatePackages 需要委托给Spring Boot加载器的包前缀列表
		 */
		public BridgeClassLoader(URL[] urls, ClassLoader springBootLoader, List<String> delegatePackages) {
			super(urls, null); // 不使用父加载器，我们自己控制委托
			this.springBootLoader = springBootLoader;
			this.delegatePackages = delegatePackages != null ? delegatePackages : new ArrayList<>();

			// 默认添加一些常见的框架包前缀
			if (delegatePackages != null && delegatePackages.isEmpty()) {
				this.delegatePackages.add("org.ark.framework.orm");
				this.delegatePackages.add("io.arkx.framework");
				this.delegatePackages.add("org.springframework");
				this.delegatePackages.add("org.hibernate");
				this.delegatePackages.add("javax.");
				this.delegatePackages.add("java.");
			}
		}

		@Override
		protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
			LogUtil.debug("BridgeClassLoader尝试加载类: " + name);

			// 1. 检查类是否已经加载
			Class<?> loadedClass = findLoadedClass(name);
			if (loadedClass != null) {
				LogUtil.debug("类已加载: " + name);
				return loadedClass;
			}

			// 2. 对于指定的包，委托给Spring Boot类加载器
			for (String packagePrefix : delegatePackages) {
				if (name.startsWith(packagePrefix)) {
					try {
						Class<?> springBootClass = springBootLoader.loadClass(name);
						if (springBootClass != null) {
							LogUtil.debug("从Spring Boot类加载器加载类: " + name);
							if (resolve) {
								resolveClass(springBootClass);
							}
							return springBootClass;
						}
					}
					catch (ClassNotFoundException e) {
						LogUtil.debug("Spring Boot类加载器无法加载: " + name + ", 继续尝试其他方式");
						// 如果Spring Boot加载器无法加载，我们继续尝试其他方式
					}
				}
			}

			// 3. 尝试从当前类加载器的URL中加载
			try {
				Class<?> localClass = findClass(name);
				if (resolve) {
					resolveClass(localClass);
				}
				LogUtil.debug("从BridgeClassLoader的URL中加载类: " + name);
				return localClass;
			}
			catch (ClassNotFoundException e) {
				LogUtil.debug("BridgeClassLoader的URL中未找到类: " + name);
				// 如果在URL中找不到，则可能是系统类
			}

			// 4. 最后尝试委托给Spring Boot类加载器（用于非委托包的依赖类）
			try {
				Class<?> springBootClass = springBootLoader.loadClass(name);
				if (springBootClass != null) {
					LogUtil.debug("从Spring Boot类加载器加载非委托包类: " + name);
					if (resolve) {
						resolveClass(springBootClass);
					}
					return springBootClass;
				}
			}
			catch (ClassNotFoundException e) {
				LogUtil.debug("Spring Boot类加载器无法加载非委托包类: " + name);
			}

			// 5. 最后尝试委托给系统类加载器
			LogUtil.debug("尝试使用系统类加载器加载: " + name);
			return super.loadClass(name, resolve);
		}

	}

	/**
	 * 跨平台安全的JAR文件路径处理方法 确保在Windows和Linux环境下都能正确处理JAR文件路径
	 * @param jarPath JAR文件路径
	 * @return 规范化的JAR文件路径和URL
	 * @throws MalformedURLException 如果URL格式错误
	 */
	public static JarPathInfo normalizeCrossPlatformJarPath(String jarPath) throws MalformedURLException {
		LogUtil.info("开始跨平台JAR路径规范化: " + jarPath);

		// 1. 创建File对象并验证
		File jarFile = new File(jarPath);
		if (!jarFile.exists()) {
			throw new IllegalArgumentException("JAR文件不存在: " + jarFile.getAbsolutePath());
		}
		if (!jarFile.isFile()) {
			throw new IllegalArgumentException("指定路径不是文件: " + jarFile.getAbsolutePath());
		}

		// 2. 获取规范路径（解决符号链接等问题）
		String canonicalPath;
		try {
			canonicalPath = jarFile.getCanonicalPath();
		}
		catch (IOException e) {
			LogUtil.warn("无法获取规范路径，使用绝对路径: " + e.getMessage());
			canonicalPath = jarFile.getAbsolutePath();
		}

		// 3. 创建URL（使用规范路径确保一致性）
		File canonicalFile = new File(canonicalPath);
		URL jarUrl = canonicalFile.toURI().toURL();

		// 4. 验证URL格式
		if (!"file".equals(jarUrl.getProtocol())) {
			throw new MalformedURLException("JAR URL协议不正确: " + jarUrl.getProtocol());
		}

		LogUtil.info("JAR路径规范化完成: " + canonicalPath + " -> " + jarUrl);

		return new JarPathInfo(canonicalPath, jarUrl, canonicalFile);
	}

	/**
	 * JAR路径信息封装类
	 */
	public static class JarPathInfo {

		public final String canonicalPath;

		public final URL jarUrl;

		public final File jarFile;

		public JarPathInfo(String canonicalPath, URL jarUrl, File jarFile) {
			this.canonicalPath = canonicalPath;
			this.jarUrl = jarUrl;
			this.jarFile = jarFile;
		}

	}

	/**
	 * 修复包含错误路径信息的类名 Linux环境下JAR Entry可能包含完整文件路径，需要提取正确的类名
	 * @param className 可能包含错误路径的类名
	 * @return 修复后的类名
	 */
	private static String fixCorruptedClassName(String className) {
		if (className == null || className.isEmpty()) {
			return className;
		}

		// 如果类名包含文件路径信息，尝试提取正确的类名
		if (className.contains(File.separator) || className.contains("/") || className.contains("\\")
				|| className.startsWith(".") || className.contains(".classes.")) {

			// 查找org.ark.framework模式
			String pattern = "org.ark.framework";
			int patternIndex = className.indexOf(pattern);
			if (patternIndex >= 0) {
				return className.substring(patternIndex);
			}

			// 查找io.arkx模式
			pattern = "io.arkx";
			patternIndex = className.indexOf(pattern);
			if (patternIndex >= 0) {
				return className.substring(patternIndex);
			}

			// 查找.classes.模式，这是Linux环境下常见的错误
			pattern = ".classes.";
			patternIndex = className.indexOf(pattern);
			if (patternIndex >= 0) {
				return className.substring(patternIndex + pattern.length());
			}

			// 如果找不到已知模式，尝试从最后一个有效的包名开始
			String[] parts = className.split("[/\\\\.]");
			StringBuilder sb = new StringBuilder();
			boolean foundValidStart = false;

			for (String part : parts) {
				if (part.matches("^[a-zA-Z_$][a-zA-Z0-9_$]*$")) {
					// 如果是已知的包名起始，开始收集
					if ("org".equals(part) || "com".equals(part)) {
						foundValidStart = true;
						sb = new StringBuilder();
					}

					if (foundValidStart) {
						if (sb.length() > 0) {
							sb.append(".");
						}
						sb.append(part);
					}
				}
			}

			if (foundValidStart && sb.length() > 0) {
				return sb.toString();
			}
		}

		return className;
	}

	/**
	 * 创建一个能处理跨类加载器依赖的桥接类加载器
	 * @param jarPath JAR文件路径
	 * @param delegatePackages 需要委托给Spring Boot加载器的包前缀列表
	 * @return 配置好的桥接类加载器
	 * @throws MalformedURLException 如果URL格式错误
	 */
	public static ClassLoader createBridgeClassLoader(String jarPath, List<String> delegatePackages)
			throws MalformedURLException {
		// 使用跨平台安全的路径处理
		JarPathInfo jarPathInfo = normalizeCrossPlatformJarPath(jarPath);
		URL jarUrl = jarPathInfo.jarUrl;

		// 寻找Spring Boot类加载器
		ClassLoader springBootLoader = null;
		ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();

		while (currentLoader != null) {
			if (currentLoader.getClass().getName().contains("LaunchedClassLoader")) {
				springBootLoader = currentLoader;
				break;
			}
			currentLoader = currentLoader.getParent();
		}

		// 如果找不到Spring Boot类加载器，尝试通过框架类获取
		if (springBootLoader == null) {
			try {
				Class<?> frameworkClass = Class.forName("org.ark.framework.orm.Schema");
				if (frameworkClass != null) {
					springBootLoader = frameworkClass.getClassLoader();
				}
			}
			catch (ClassNotFoundException e) {
				// 使用上下文类加载器作为后备
				springBootLoader = Thread.currentThread().getContextClassLoader();
			}
		}

		// 创建桥接类加载器
		return new BridgeClassLoader(new URL[] { jarUrl }, springBootLoader, delegatePackages);
	}

	/**
	 * 使用桥接类加载器加载JAR文件，自动处理跨类加载器依赖
	 * @param jarPath JAR文件路径
	 * @return 加载的类数量
	 */
	public static int loadJarWithBridgeLoader(String jarPath) {
		// 保存当前线程的上下文类加载器
		ClassLoader originalContextLoader = Thread.currentThread().getContextClassLoader();

		try {
			// 使用跨平台安全的路径处理
			JarPathInfo jarPathInfo = normalizeCrossPlatformJarPath(jarPath);
			String absoluteJarPath = jarPathInfo.canonicalPath;

			// 构建需要委托的包列表
			List<String> delegatePackages = new ArrayList<>();
			delegatePackages.add("org.ark.framework.orm");
			delegatePackages.add("io.arkx.framework");

			// 创建桥接类加载器
			ClassLoader bridgeLoader = createBridgeClassLoader(absoluteJarPath, delegatePackages);
			Thread.currentThread().setContextClassLoader(bridgeLoader);

			// 加载JAR中的所有类
			int classCount = 0;
			try (JarFile jarFile = new JarFile(absoluteJarPath)) {
				Enumeration<JarEntry> entries = jarFile.entries();
				List<String> classNames = new ArrayList<>();

				// 收集所有类名
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (entry.getName().endsWith(".class")) {
						String className = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');

						// 验证类名格式（JAR创建源头已修复，这里只做基本验证）
						if (className.matches("^[a-zA-Z_$][a-zA-Z0-9_$]*(?:\\.[a-zA-Z_$][a-zA-Z0-9_$]*)*$")) {
							classNames.add(className);
						}
						else {
							LogUtil.warn("跳过格式异常的类名: " + className);
						}
					}
				}

				// 加载所有类
				for (String className : classNames) {
					try {
						Class<?> loadedClass = Class.forName(className, true, bridgeLoader);
						classCount++;

						// 注册到全局映射
						registerLoadedClass(className, loadedClass);

					}
					catch (ClassNotFoundException e) {
						LogUtil.warn("无法加载类: " + className + ", 原因: " + e.getMessage());
					}
					catch (NoClassDefFoundError e) {
						// 尝试先加载依赖
						String missingClass = e.getMessage();
						if (missingClass != null) {
							missingClass = missingClass.replace('/', '.');
							if (missingClass.startsWith("L") && missingClass.endsWith(";")) {
								missingClass = missingClass.substring(1, missingClass.length() - 1);
							}
						}

						try {
							Class<?> dependencyClass = Class.forName(missingClass);
							// 再次尝试加载原类
							Class<?> loadedClass = Class.forName(className, true, bridgeLoader);
							classCount++;
							registerLoadedClass(className, loadedClass);
						}
						catch (Exception ex) {
							LogUtil.debug("解决依赖问题失败: " + className);
						}
					}
					catch (Exception e) {
						LogUtil.warn("加载类时出现异常: " + className + ", " + e.getMessage());
					}
				}
			}

			LogUtil.info("使用桥接类加载器从JAR文件加载了 " + classCount + " 个类");
			return classCount;

		}
		catch (Exception e) {
			LogUtil.error("使用桥接加载器加载JAR文件时出错: " + e.getMessage());
			return 0;
		}
		finally {
			// 恢复原始上下文类加载器
			Thread.currentThread().setContextClassLoader(originalContextLoader);
		}
	}

	/**
	 * 预加载框架依赖类 确保关键的框架类已经加载到主类加载器中
	 */
	public static void preloadFrameworkClasses() {
		String[] frameworkClasses = { "org.ark.framework.orm.Schema", "org.ark.framework.orm.SchemaColumn",
				"org.ark.framework.orm.SchemaSet"
				// 可以添加更多的核心框架类
		};

		LogUtil.info("预加载框架核心类...");

		for (String className : frameworkClasses) {
			try {
				Class<?> frameworkClass = Class.forName(className);
				LogUtil
					.info("已预加载框架类: " + className + ", 加载器: " + frameworkClass.getClassLoader().getClass().getName());
			}
			catch (ClassNotFoundException e) {
				LogUtil.warn("无法预加载框架类: " + className + ", " + e.getMessage());
			}
		}
	}

	/**
	 * 加载具有框架依赖的JAR包的最佳方法 自动检测环境并使用最合适的策略
	 * @param jarPath JAR文件路径
	 * @return 创建的桥接类加载器
	 */
	public static ClassLoader loadJarWithFrameworkDependencies(String jarPath) {
		// 先预加载框架类
		preloadFrameworkClasses();

		// 检测当前环境
		String threadName = Thread.currentThread().getName();
		ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
		String loaderClassName = contextLoader != null ? contextLoader.getClass().getName() : "null";

		LogUtil.info("加载具有框架依赖的JAR: " + jarPath);
		LogUtil.info("当前线程: " + threadName + ", 类加载器: " + loaderClassName);

		// 创建桥接类加载器
		try {
			// 构建需要委托的包列表
			List<String> delegatePackages = new ArrayList<>();
			delegatePackages.add("org.ark.framework.orm");
			delegatePackages.add("io.arkx.framework");

			// 创建桥接类加载器
			ClassLoader bridgeLoader = createBridgeClassLoader(jarPath, delegatePackages);

			// 加载JAR中的所有类
			loadJarWithBridgeLoader(jarPath);

			// 返回创建的桥接类加载器
			return bridgeLoader;
		}
		catch (Exception e) {
			LogUtil.error("创建桥接类加载器失败: " + e.getMessage());
			return null;
		}
	}

	/**
	 * 通过类名从已加载的类全局映射中获取类
	 * @param className 完整类名
	 * @return 已加载的类，如果未找到则返回null
	 */
	public static Class<?> getClassFromGlobalMap(String className) {
		Class<?> loadedClass = loadedClassesMap.get(className);
		if (loadedClass != null) {
			LogUtil.info("从全局映射中获取类: " + className);
			return loadedClass;
		}

		// 尝试通过Class.forName加载
		try {
			loadedClass = Class.forName(className);
			if (loadedClass != null) {
				// 添加到全局映射
				registerLoadedClass(className, loadedClass);
				LogUtil.info("通过Class.forName加载类并添加到全局映射: " + className);
				return loadedClass;
			}
		}
		catch (ClassNotFoundException e) {
			LogUtil.debug("无法通过Class.forName加载类: " + className);
		}

		return null;
	}

	/**
	 * 使用当前线程上下文类加载器加载类，并添加到全局映射
	 * @param className 完整类名
	 * @return 加载的类，如果加载失败则返回null
	 */
	public static Class<?> loadAndRegisterClass(String className) {
		// 先检查是否已经在全局映射中
		Class<?> loadedClass = getClassFromGlobalMap(className);
		if (loadedClass != null) {
			return loadedClass;
		}

		// 尝试使用当前线程上下文类加载器加载
		try {
			ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
			loadedClass = Class.forName(className, true, contextLoader);
			if (loadedClass != null) {
				registerLoadedClass(className, loadedClass);
				LogUtil.info("已加载并注册类: " + className);
				return loadedClass;
			}
		}
		catch (ClassNotFoundException e) {
			LogUtil.debug("上下文类加载器无法加载类: " + className);
		}

		// 尝试使用系统类加载器加载
		try {
			loadedClass = Class.forName(className, true, ClassLoader.getSystemClassLoader());
			if (loadedClass != null) {
				registerLoadedClass(className, loadedClass);
				LogUtil.info("使用系统类加载器加载并注册类: " + className);
				return loadedClass;
			}
		}
		catch (ClassNotFoundException e) {
			LogUtil.debug("系统类加载器无法加载类: " + className);
		}

		return null;
	}

	/**
	 * 验证JAR文件内容，用于诊断类加载问题
	 * @param jarPath JAR文件路径
	 * @return 验证报告
	 */
	public static String validateJarFile(String jarPath) {
		StringBuilder report = new StringBuilder();
		report.append("===== JAR文件验证报告 =====\n");

		File jarFileObj = new File(jarPath);
		report.append("JAR文件路径: ").append(jarPath).append("\n");
		report.append("JAR文件绝对路径: ").append(jarFileObj.getAbsolutePath()).append("\n");
		report.append("文件存在: ").append(jarFileObj.exists()).append("\n");
		report.append("是文件: ").append(jarFileObj.isFile()).append("\n");
		report.append("可读: ").append(jarFileObj.canRead()).append("\n");
		report.append("文件大小: ").append(jarFileObj.length()).append(" bytes\n");

		if (!jarFileObj.exists() || !jarFileObj.isFile()) {
			report.append("错误: JAR文件不存在或不是文件\n");
			return report.toString();
		}

		try {
			// 验证JAR文件结构
			URL jarUrl = jarFileObj.toURI().toURL();
			report.append("JAR URL: ").append(jarUrl.toString()).append("\n");

			try (JarFile jarFile = new JarFile(jarFileObj)) {
				Enumeration<JarEntry> entries = jarFile.entries();
				int totalEntries = 0;
				int classFiles = 0;
				List<String> sampleClasses = new ArrayList<>();

				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					totalEntries++;

					if (entry.getName().endsWith(".class")) {
						classFiles++;
						String className = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');

						if (sampleClasses.size() < 5) {
							sampleClasses.add(className);
						}
					}
				}

				report.append("总条目数: ").append(totalEntries).append("\n");
				report.append("类文件数: ").append(classFiles).append("\n");
				report.append("示例类名:\n");
				for (String className : sampleClasses) {
					report.append("  - ").append(className).append("\n");
				}

				// 尝试创建URLClassLoader
				try {
					URLClassLoader testLoader = new URLClassLoader(new URL[] { jarUrl });
					report.append("URLClassLoader创建: 成功\n");

					// 尝试加载一个示例类
					if (!sampleClasses.isEmpty()) {
						String testClassName = sampleClasses.get(0);
						try {
							Class<?> testClass = testLoader.loadClass(testClassName);
							report.append("测试类加载: 成功 (").append(testClassName).append(")\n");
							report.append("加载器: ").append(testClass.getClassLoader().getClass().getName()).append("\n");
						}
						catch (Exception e) {
							report.append("测试类加载: 失败 (")
								.append(testClassName)
								.append(") - ")
								.append(e.getMessage())
								.append("\n");
						}
					}

					testLoader.close();
				}
				catch (Exception e) {
					report.append("URLClassLoader创建: 失败 - ").append(e.getMessage()).append("\n");
				}

			}
			catch (Exception e) {
				report.append("JAR文件读取失败: ").append(e.getMessage()).append("\n");
			}

		}
		catch (Exception e) {
			report.append("URL创建失败: ").append(e.getMessage()).append("\n");
		}

		report.append("===== 验证完成 =====\n");
		return report.toString();
	}

}

class URLClassLoaderUtil {

	URLClassLoader classLoader = null;// URLClassLoader类载入器

	private String jarFileName;

	private boolean isFile = true;

	List<String> jars = new ArrayList<String>(0);

	/**
	 * 加载具体的某一jar包
	 * @param jarFileName
	 */
	public URLClassLoaderUtil(String jarFileName) {
		this.setJarFileName(jarFileName);
		this.inti();
	}

	/**
	 * 加载jar包 当isFile为false是加载文件夹下所有jar
	 * @param jarFileName 路径
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
		}
		else {
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
			classLoader = new URLClassLoader(new URL[] { new URL(jarURL) }, ClassLoader.getSystemClassLoader());// Thread.currentThread().getContextClassLoader());
		}
		catch (MalformedURLException e) {
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
			}
			catch (MalformedURLException e) {
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
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		Enumeration<JarEntry> en = jarFile.entries();
		while (en.hasMoreElements()) {
			JarEntry je = en.nextElement();
			String name = je.getName();
			String s5 = name.replace('/', '.');
			if (s5.lastIndexOf(".class") > 0) {
				String className = je.getName()
					.substring(0, je.getName().length() - ".class".length())
					.replace('/', '.');
				Class<?> c = null;
				try {
					c = this.classLoader.loadClass(className);
					System.out.println(c.newInstance());
					System.out.println(className);
				}
				catch (ClassNotFoundException e) {
					System.out.println("NO CLASS: " + className);
					// continue;
				}
				catch (NoClassDefFoundError e) {
					System.out.println("NO CLASS: " + className);
					// continue;
				}
				// callBack.operate(c);
				catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
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
	 * @param jarFileName 路径
	 * @param strings 后缀
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
	 * @return
	 */
	public List<String> getFiles() {
		return filesURL;
	}

	/**
	 * 获取文件路径
	 * @return
	 */
	public List<String> getFilesURL() {
		return filesURL;
	}

}
