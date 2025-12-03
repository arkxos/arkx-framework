package io.arkx.framework.commons.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 16:09
 * @since 1.0
 */
@Slf4j
public class DynamicCompilationUtils {

    /**
     * Builds a comprehensive classpath string for the Java compiler.
     *
     * @param actualClassesPath
     *            Path to the directory where compiled .class files will be placed
     *            (and where pre-existing dependencies might be copied).
     * @param sourcePath
     *            Path to the .java source files.
     * @return A string suitable for use with the -classpath javac option.
     */
    public static String buildComprehensiveClasspath(String actualClassesPath, String sourcePath) {
        StringBuilder cp = new StringBuilder();

        // 1. 编译输出目录 (包含我们复制的核心框架类和动态编译的输出)
        // 规范化路径以确保跨平台兼容性
        if (actualClassesPath != null && !actualClassesPath.isEmpty()) {
            File normalizedClassesPath = new File(actualClassesPath);
            cp.append(normalizedClassesPath.getAbsolutePath());
            log.debug("Compiler CP: Added (1) classes output/dependency path: {}",
                    normalizedClassesPath.getAbsolutePath());
        }

        // 2. 添加外部lib目录中的JARs
        String baseDir = System.getProperty("user.dir"); // 当前工作目录，通常是JAR运行的目录
        String appHome = System.getenv("APP_HOME");
        if (appHome != null && !appHome.isEmpty()) {
            File appHomeDir = new File(appHome);
            if (appHomeDir.exists() && appHomeDir.isDirectory()) {
                baseDir = appHome;
                log.debug("Using APP_HOME as base directory for external lib: {}", baseDir);
            } else {
                log.warn("APP_HOME ({}) is defined but does not exist or is not a directory. Falling back to user.dir.",
                        appHome);
            }
        }

        // 使用File对象确保路径分隔符的正确处理
        File libDir = new File(baseDir, "lib");
        log.debug("Attempting to scan for external JARs in: {}", libDir.getAbsolutePath());
        if (libDir.exists() && libDir.isDirectory()) {
            File[] externalJars = libDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
            if (externalJars != null && externalJars.length > 0) {
                for (File jar : externalJars) {
                    // 使用File.pathSeparator确保classpath条目之间的分隔符正确
                    // Windows: ';', Unix/Linux: ':'
                    cp.append(File.pathSeparator).append(jar.getAbsolutePath());
                    log.debug("Compiler CP: Added (2) external JAR from ./lib: {}", jar.getAbsolutePath());
                }
            } else {
                log.debug("Compiler CP: No JAR files found in external ./lib directory: {}", libDir.getAbsolutePath());
            }
        } else {
            log.debug("Compiler CP: External ./lib directory not found or not a directory at: {}",
                    libDir.getAbsolutePath());
        }

        // 3. 源文件路径
        if (sourcePath != null && !sourcePath.isEmpty()) {
            // 规范化源文件路径
            File normalizedSourcePath = new File(sourcePath);
            cp.append(File.pathSeparator).append(normalizedSourcePath.getAbsolutePath());
            log.debug("Compiler CP: Added (3) source path: {}", normalizedSourcePath.getAbsolutePath());
        }

        // 4. 原始系统类路径 (通常是 fat JAR 本身, 作为后备)
        String systemClassPath = System.getProperty("java.class.path");
        if (systemClassPath != null && !systemClassPath.isEmpty()) {
            cp.append(File.pathSeparator).append(systemClassPath);
            log.debug("Compiler CP: Added (4) system classpath (e.g., fat JAR): {}", systemClassPath);
        }

        log.debug("Final compiler classpath string for dynamic compilation: {}", cp.toString());
        // 使用File.pathSeparator来分割classpath，确保跨平台兼容性
        // Pattern.quote确保路径分隔符被正确转义
        String[] cpEntries = cp.toString().split(java.util.regex.Pattern.quote(File.pathSeparator));
        log.debug("Final compiler classpath entries ({} total):", cpEntries.length);
        for (int i = 0; i < cpEntries.length; i++) {
            log.debug("  CP[{}]: {}", i, cpEntries[i]);
        }
        log.debug("Final compiler classpath length: {} chars", cp.length());
        return cp.toString();
    }

    /**
     * Closes a URLClassLoader and attempts to release its resources.
     *
     * @param classLoader
     *            The URLClassLoader to close.
     */
    public static void closeClassLoader(URLClassLoader classLoader) {
        if (classLoader == null) {
            return;
        }

        try {
            log.debug("Closing URLClassLoader to release JAR file resources.");
            classLoader.close();
        } catch (Exception e) {
            // In Java 7+, URLClassLoader implements Closeable.
            // For older versions, or if other issues occur, log a warning.
            log.warn("Failed to close URLClassLoader or it does not support close(): {}", e.getMessage());
        }
    }

    /**
     * Extracts all *.class files from a given JAR file into a specified output
     * directory, maintaining their package structure.
     *
     * @param jarFile
     *            The JAR file to extract classes from.
     * @param outputDir
     *            The directory where .class files will be extracted.
     */
    public static void extractAllClassesFromJar(File jarFile, String outputDir) {
        log.debug("Extracting all classes from JAR: {} into directory: {}", jarFile.getAbsolutePath(), outputDir);
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    // JAR内部路径使用'/'分隔符，需要转换为本地文件系统路径
                    // 例如: "com/arkxos/framework/SomeClass.class" ->
                    // "com\arkxos\framework\SomeClass.class" (Windows)
                    String entryPath = entry.getName().replace('/', File.separatorChar);
                    File outFile = new File(outputDir, entryPath);

                    // Ensure parent directories exist
                    if (!outFile.getParentFile().exists()) {
                        boolean created = outFile.getParentFile().mkdirs();
                        if (!created) {
                            log.warn("Failed to create parent directories for: {}",
                                    outFile.getParentFile().getAbsolutePath());
                        }
                    }
                    try (InputStream in = jar.getInputStream(entry);
                            FileOutputStream out = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        log.debug("Extracted class: {} to {}", entry.getName(), outFile.getAbsolutePath());
                    } catch (IOException e) {
                        log.error("Error extracting class file {} from JAR {}: {}", entry.getName(), jarFile.getName(),
                                e.getMessage(), e);
                    }
                }
            }
            log.debug("Finished extracting classes from JAR: {}", jarFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Error opening or reading JAR file {}: {}", jarFile.getAbsolutePath(), e.getMessage(), e);
        }
    }

    /**
     * Ensures that necessary framework classes are available for compilation by
     * extracting them from specified JAR files located in an external 'lib'
     * directory into the compilation classesPath.
     *
     * @param classesPath
     *            The directory where .class files will be extracted (compiler's -d
     *            output).
     * @param criticalJarFilenames
     *            A list of JAR filenames (e.g., "my-framework.jar") to scan for
     *            classes. These JARs are expected to be in the './lib' directory
     *            relative to the application's runtime location.
     */
    public static void ensureFrameworkClassesAvailable(String classesPath, List<String> criticalJarFilenames) {
        // 规范化输出目录路径，确保跨平台兼容性
        File normalizedClassesPath = new File(classesPath);
        String normalizedClassesPathStr = normalizedClassesPath.getAbsolutePath();
        log.debug("Ensuring framework classes are available in: {} by extracting from specified JARs.",
                normalizedClassesPathStr);

        String baseDir = System.getProperty("user.dir");
        String appHome = System.getenv("APP_HOME");
        if (appHome != null && !appHome.isEmpty()) {
            File appHomeDir = new File(appHome);
            if (appHomeDir.exists() && appHomeDir.isDirectory()) {
                baseDir = appHome;
                log.debug("Using APP_HOME as base directory for external lib: {}", baseDir);
            } else {
                log.warn(
                        "APP_HOME ({}) is defined but does not exist or is not a directory. Falling back to user.dir for lib scanning.",
                        appHome);
            }
        }

        // 使用File对象确保路径分隔符的正确处理
        File externalLibDir = new File(baseDir, "lib");

        if (!externalLibDir.exists() || !externalLibDir.isDirectory()) {
            log.warn("External lib directory not found at: {}. Cannot extract framework classes from JARs.",
                    externalLibDir.getAbsolutePath());
            return;
        }

        log.debug("Scanning for critical JARs in: {}", externalLibDir.getAbsolutePath());
        for (String jarFilename : criticalJarFilenames) {
            // 使用File对象确保JAR文件路径的正确处理
            File jarFile = new File(externalLibDir, jarFilename);
            if (jarFile.exists() && jarFile.isFile()) {
                log.debug("Found critical JAR: {}. Attempting to extract all classes.", jarFile.getAbsolutePath());
                extractAllClassesFromJar(jarFile, normalizedClassesPathStr);
            } else {
                log.warn("Critical JAR file not found: {} in {}", jarFilename, externalLibDir.getAbsolutePath());
            }
        }
    }

}
