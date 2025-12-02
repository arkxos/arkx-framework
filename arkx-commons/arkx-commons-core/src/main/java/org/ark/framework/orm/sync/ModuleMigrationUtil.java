package org.ark.framework.orm.sync;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:33
 * @since 1.0
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * 模块迁移工具类 用于将类及其依赖从一个模块复制到另一个模块
 */
@Slf4j
public class ModuleMigrationUtil {

	// 匹配Java import语句的正则表达式
	private static final Pattern IMPORT_PATTERN = Pattern.compile("import\\s+([\\w\\.]+)\\s*;");

	// 匹配Java包声明的正则表达式
	private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([\\w\\.]+)\\s*;");

	/**
	 * 将一个类及其所有依赖复制到新的模块
	 * @param sourceModulePath 源模块根目录的路径
	 * @param targetModulePath 目标模块根目录的路径
	 * @param rootClassName 要迁移的根类的完全限定名
	 * @param includePatterns 要包含的类的包名模式（正则表达式）
	 * @param excludePatterns 要排除的类的包名模式（正则表达式）
	 * @return 复制的类数量
	 */
	public static int migrateClass(String sourceModulePath, String targetModulePath, String rootClassName,
			List<String> includePatterns, List<String> excludePatterns) {

		log.info("开始迁移类 {} 及其依赖从 {} 到 {}", rootClassName, sourceModulePath, targetModulePath);

		// 确保目标目录存在
		new File(targetModulePath).mkdirs();

		// 查找根类文件
		String classFilePath = getClassFilePath(sourceModulePath, rootClassName);
		File rootClassFile = new File(classFilePath);

		if (!rootClassFile.exists()) {
			log.error("找不到根类文件: {}", classFilePath);
			return 0;
		}

		// 收集所有需要复制的类
		Set<String> processedClasses = new HashSet<>();
		Set<String> pendingClasses = new HashSet<>();
		pendingClasses.add(rootClassName);

		int copiedCount = 0;

		while (!pendingClasses.isEmpty()) {
			String className = pendingClasses.iterator().next();
			pendingClasses.remove(className);

			if (processedClasses.contains(className)) {
				continue;
			}

			processedClasses.add(className);

			// 检查类是否符合包含/排除模式
			if (!matchesIncludePatterns(className, includePatterns)
					|| matchesExcludePatterns(className, excludePatterns)) {
				log.debug("跳过类 {}: 不符合包含/排除模式", className);
				continue;
			}

			// 获取类文件路径
			String classPath = getClassFilePath(sourceModulePath, className);
			File classFile = new File(classPath);

			if (!classFile.exists()) {
				log.debug("找不到类文件 {}, 可能是外部依赖", classPath);
				continue;
			}

			// 复制类文件
			try {
				String content = new String(Files.readAllBytes(classFile.toPath()));

				// 获取该类的所有imports
				Set<String> imports = extractImports(content);
				pendingClasses.addAll(imports);

				// 复制到目标目录
				String targetFilePath = getClassFilePath(targetModulePath, className);
				File targetFile = new File(targetFilePath);
				targetFile.getParentFile().mkdirs();

				log.debug("复制类 {} 到 {}", className, targetFilePath);
				Files.copy(classFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				copiedCount++;

			}
			catch (IOException e) {
				log.error("复制类 {} 时出错: {}", className, e.getMessage(), e);
			}
		}

		log.info("迁移完成，共复制了 {} 个类", copiedCount);
		return copiedCount;
	}

	/**
	 * 复制指定的类列表到新模块
	 * @param sourceModulePath 源模块根目录的路径
	 * @param targetModulePath 目标模块根目录的路径
	 * @param classNames 要复制的类的完全限定名列表
	 * @return 复制的类数量
	 */
	public static int copySpecificClasses(String sourceModulePath, String targetModulePath, List<String> classNames) {
		log.info("开始复制 {} 个指定类从 {} 到 {}", classNames.size(), sourceModulePath, targetModulePath);

		int copiedCount = 0;
		for (String className : classNames) {
			String classPath = getClassFilePath(sourceModulePath, className);
			File classFile = new File(classPath);

			if (!classFile.exists()) {
				log.warn("找不到类文件: {}", classPath);
				continue;
			}

			try {
				String targetFilePath = getClassFilePath(targetModulePath, className);
				File targetFile = new File(targetFilePath);
				targetFile.getParentFile().mkdirs();

				log.debug("复制类 {} 到 {}", className, targetFilePath);
				Files.copy(classFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				copiedCount++;
			}
			catch (IOException e) {
				log.error("复制类 {} 时出错: {}", className, e.getMessage(), e);
			}
		}

		log.info("复制完成，共复制了 {} 个类", copiedCount);
		return copiedCount;
	}

	/**
	 * 扫描源模块中与增量同步相关的所有类
	 * @param sourceModulePath 源模块根目录的路径
	 * @param packageName 要扫描的包名（例如 org.ark.framework.orm.sync）
	 * @return 找到的类的完全限定名列表
	 */
	public static List<String> scanSyncRelatedClasses(String sourceModulePath, String packageName) {
		log.info("扫描包 {} 中的同步相关类", packageName);

		String packagePath = packageName.replace('.', '/');
		File packageDir = new File(sourceModulePath + "/src/main/java/" + packagePath);

		if (!packageDir.exists() || !packageDir.isDirectory()) {
			log.error("包目录不存在: {}", packageDir.getAbsolutePath());
			return new ArrayList<>();
		}

		List<String> classes = new ArrayList<>();
		scanDirectory(packageDir, packageName, classes);

		log.info("找到 {} 个同步相关类", classes.size());
		return classes;
	}

	/**
	 * 迁移与Demo相关的所有类
	 * @param sourceModulePath 源模块根目录的路径
	 * @param targetModulePath 目标模块根目录的路径
	 * @return 复制的类数量
	 */
	public static int migrateDemoAndDependencies(String sourceModulePath, String targetModulePath) {
		// 定义要包含的包名模式
		List<String> includePatterns = new ArrayList<>();
		// 包含所有与Demo相关的包
		includePatterns.add("org\\.ark\\.framework\\.orm\\.sync.*");
		includePatterns.add("org\\.ark\\.framework\\.orm\\.Schema.*");
		includePatterns.add("org\\.ark\\.framework\\.orm\\.SchemaSet.*");
		includePatterns.add("org\\.ark\\.framework\\.orm\\.SchemaUtil.*");
		includePatterns.add("org\\.ark\\.framework\\.orm\\.SchemaColumn.*");
		includePatterns.add("org\\.ark\\.framework\\.orm\\.sql.*");
		includePatterns.add("org\\.ark\\.framework\\.orm.*");
		includePatterns.add("com\\.arkxos\\.framework\\.data\\.db.*");
		includePatterns.add("com\\.arkxos\\.framework\\.data\\.jdbc.*");
		includePatterns.add("com\\.arkxos\\.framework\\.commons\\.util.*");
		includePatterns.add("com\\.arkxos\\.framework\\.commons\\.collection.*");

		// 定义要排除的包名模式
		List<String> excludePatterns = new ArrayList<>();
		// 排除Spring Boot相关的包
		excludePatterns.add("org\\.springframework\\..*");
		excludePatterns.add("org\\.springdoc\\..*");
		excludePatterns.add("org\\.springframework\\.boot\\..*");
		excludePatterns.add("org\\.springframework\\.web\\..*");
		excludePatterns.add("org\\.springframework\\.context\\..*");
		excludePatterns.add("org\\.springframework\\.beans\\..*");

		log.info("开始迁移Demo类及其所有非Spring Boot依赖");

		// 迁移Demo类及其依赖
		return migrateClass(sourceModulePath, targetModulePath, "org.ark.framework.orm.sync.Demo", includePatterns,
				excludePatterns);
	}

	/**
	 * 扫描目录及其子目录，查找Java类文件
	 */
	private static void scanDirectory(File dir, String currentPackage, List<String> classes) {
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				scanDirectory(file, currentPackage + "." + file.getName(), classes);
			}
			else if (file.getName().endsWith(".java")) {
				String className = currentPackage + "." + file.getName().replace(".java", "");
				classes.add(className);
			}
		}
	}

	/**
	 * 从Java源代码中提取import语句
	 */
	private static Set<String> extractImports(String content) {
		Set<String> imports = new HashSet<>();
		Matcher matcher = IMPORT_PATTERN.matcher(content);

		while (matcher.find()) {
			String importClass = matcher.group(1);
			// 只处理非静态导入且不是标准库和常见第三方库的导入
			if (!importClass.startsWith("java.") && !importClass.startsWith("javax.")
					&& !importClass.startsWith("org.springframework.") && !importClass.startsWith("org.springdoc.")
					&& !importClass.startsWith("com.google.") && !importClass.startsWith("lombok.")
					&& !importClass.startsWith("org.slf4j.") && !importClass.startsWith("org.apache.commons.")
					&& !importClass.contains(".*")) {
				imports.add(importClass);
			}
		}

		return imports;
	}

	/**
	 * 获取类文件的路径
	 */
	private static String getClassFilePath(String modulePath, String className) {
		return modulePath + "/src/main/java/" + className.replace('.', '/') + ".java";
	}

	/**
	 * 检查类名是否符合包含模式
	 */
	private static boolean matchesIncludePatterns(String className, List<String> includePatterns) {
		if (includePatterns == null || includePatterns.isEmpty()) {
			return true;
		}

		for (String pattern : includePatterns) {
			if (className.matches(pattern)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 检查类名是否符合排除模式
	 */
	private static boolean matchesExcludePatterns(String className, List<String> excludePatterns) {
		if (excludePatterns == null || excludePatterns.isEmpty()) {
			return false;
		}

		for (String pattern : excludePatterns) {
			if (className.matches(pattern)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 生成模块迁移报告
	 */
	public static void generateMigrationReport(String sourceModulePath, String targetModulePath,
			List<String> migratedClasses) {
		log.info("生成迁移报告");

		StringBuilder report = new StringBuilder();
		report.append("# 模块迁移报告\n\n");
		report.append("## 迁移详情\n\n");
		report.append("- 源模块: ").append(sourceModulePath).append("\n");
		report.append("- 目标模块: ").append(targetModulePath).append("\n");
		report.append("- 迁移类数量: ").append(migratedClasses.size()).append("\n\n");

		report.append("## 迁移的类列表\n\n");
		migratedClasses.forEach(className -> report.append("- ").append(className).append("\n"));

		// 保存报告
		try {
			Path reportPath = Paths.get(targetModulePath, "migration-report.md");
			Files.write(reportPath, report.toString().getBytes());
			log.info("迁移报告已保存到: {}", reportPath);
		}
		catch (IOException e) {
			log.error("保存迁移报告时出错: {}", e.getMessage(), e);
		}
	}

	/**
	 * 使用示例
	 */
	public static void main(String[] args) {
		// 源模块和目标模块的路径
		String sourceModulePath = "E:/plugin/arkxos-framework/arkxos-framework-commons/arkxos-framework-commons-core";
		String targetModulePath = "E:/ark-sync/src/main/java/com/ark/data/sync/old/lib";

		// 迁移Demo类及其依赖，排除Spring Boot相关
		int count = migrateDemoAndDependencies(sourceModulePath, targetModulePath);
		System.out.println("共迁移了 " + count + " 个类文件");

		// 生成迁移报告
		List<String> migratedClasses = scanSyncRelatedClasses(sourceModulePath, "org.ark.framework.orm.sync");
		// 过滤掉Spring Boot相关类
		migratedClasses = migratedClasses.stream()
			.filter(className -> !className.contains("org.springframework"))
			.collect(Collectors.toList());
		generateMigrationReport(sourceModulePath, targetModulePath, migratedClasses);

		System.out.println("迁移完成，请检查目标目录: " + targetModulePath);
	}

}
