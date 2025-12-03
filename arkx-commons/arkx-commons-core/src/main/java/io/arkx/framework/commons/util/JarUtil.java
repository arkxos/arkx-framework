package io.arkx.framework.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.jar.*;
import java.util.zip.ZipEntry;

import lombok.extern.slf4j.Slf4j;

/**
 * @class org.ark.framework.util.JarUtil
 * @author Darkness
 * @date 2011-10-18 下午05:07:21
 * @version V1.0
 */
@Slf4j
public class JarUtil {

    /**
     * readJar("c:\\test.jar"); 写文件到jar包中 例子中，读取一个文件，并将这个文件存储到jar包中的文件中 同时新建一个新的文件
     *
     * @param inputFileName
     * @throws Exception
     */
    public static void readJar(String inputFileName) throws Exception {

        JarInputStream in = new JarInputStream(new FileInputStream(inputFileName));
        Manifest manifest = in.getManifest();
        Attributes atts = manifest.getMainAttributes();
        // 输入所有的manifest信息
        Iterator ite = atts.keySet().iterator();
        while (ite.hasNext()) {
            Object key = ite.next();
            System.out.println(key + ":" + atts.getValue(key.toString()));
        }
        ZipEntry entry = null;
        while ((entry = in.getNextEntry()) != null) {
            // 输入每个文件的名称
            System.out.println(entry.getName());
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {// 输出文件内容
                // System.out.println(new String(buffer));
            }
            in.closeEntry();
        }
        in.close();
    }

    /**
     * writeJar("c:\\1.txt", "c:\\打包.jar"); 写文件到jar包中 例子中，读取一个文件，并将这个文件存储到jar包中的文件中
     * 同时新建一个新的文件
     *
     * @param inputFolderName
     *            输入文件夹路径
     * @param outputFileName
     *            输出JAR文件路径
     * @throws Exception
     *             处理过程中发生的异常
     */
    public static void writeJar(String inputFolderName, String outputFileName) throws Exception {

        File inputFolder = new File(inputFolderName);
        if (!inputFolder.isDirectory()) {
            throw new RuntimeException("文件" + inputFolderName + "必须是一个目录");
        }

        // 获取规范化的输入目录路径，确保跨平台兼容性
        String normalizedInputPath;
        try {
            normalizedInputPath = inputFolder.getCanonicalPath();
        } catch (IOException e) {
            normalizedInputPath = inputFolder.getAbsolutePath();
        }

        // 确保路径以分隔符结尾，便于后续计算相对路径
        if (!normalizedInputPath.endsWith(File.separator)) {
            normalizedInputPath += File.separator;
        }

        log.debug("JAR创建 - 输入目录规范路径: {}", normalizedInputPath);

        // 获取所有要打包的文件
        List<File> files = FileUtil.getAllFile(inputFolderName);
        if (files.isEmpty()) {
            log.warn("输入目录 {} 中没有找到文件，将创建空JAR包", inputFolderName);
            throw new RuntimeException("输入目录 {} 中没有找到文件".formatted(inputFolderName));
        }

        JarOutputStream out = null;

        try {
            // Mainifest是jar包特有的说明文件，不能通过手动编写实现
            // 它可以帮助你实现META-INF的目录保存了一个叫MANIFEST.MF的文件，记录版本，入口程序等信息
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
            manifest.getMainAttributes().putValue("author", "darkness");

            // 确保输出目录存在
            File outfile = new File(outputFileName);
            if (!outfile.getParentFile().exists()) {
                outfile.getParentFile().mkdirs();
            }

            // 创建JAR输出流
            out = new JarOutputStream(new FileOutputStream(outfile), manifest);

            // 如果没有文件，创建一个空的JAR
            if (files.isEmpty()) {
                out.flush();
                return;
            }

            // 处理每个文件
            for (File file : files) {
                // 跳过目录
                if (file.isDirectory()) {
                    continue;
                }

                FileInputStream in = null;
                try {
                    // 计算文件相对路径 - 强化跨平台兼容性
                    String fileCanonicalPath;
                    try {
                        fileCanonicalPath = file.getCanonicalPath();
                    } catch (IOException e) {
                        fileCanonicalPath = file.getAbsolutePath();
                    }

                    log.debug("JAR创建 - 处理文件: {}", fileCanonicalPath);

                    // 计算相对路径
                    String relativePath = null;

                    // 方法1：直接字符串替换（最可靠）
                    if (fileCanonicalPath.startsWith(normalizedInputPath)) {
                        relativePath = fileCanonicalPath.substring(normalizedInputPath.length());
                        log.debug("JAR创建 - 相对路径计算成功: {}", relativePath);
                    } else {
                        // 方法2：如果路径不匹配，尝试其他方式
                        log.warn("JAR创建 - 路径不匹配，尝试替代方案");
                        log.warn("文件路径: {}", fileCanonicalPath);
                        log.warn("基础路径: {}", normalizedInputPath);

                        // 尝试使用原始路径计算
                        String fileAbsPath = file.getAbsolutePath();
                        String inputAbsPath = inputFolder.getAbsolutePath();
                        if (!inputAbsPath.endsWith(File.separator)) {
                            inputAbsPath += File.separator;
                        }

                        if (fileAbsPath.startsWith(inputAbsPath)) {
                            relativePath = fileAbsPath.substring(inputAbsPath.length());
                            log.debug("JAR创建 - 使用绝对路径计算相对路径: {}", relativePath);
                        } else {
                            // 最后的fallback：构建相对路径
                            String fileParent = file.getParent();
                            String relativeDir = "";

                            if (fileParent != null && !fileParent.equals(inputFolder.getAbsolutePath())) {
                                // 尝试计算相对目录
                                File currentDir = new File(fileParent);
                                StringBuilder pathBuilder = new StringBuilder();

                                while (currentDir != null && !currentDir.equals(inputFolder)) {
                                    if (pathBuilder.length() > 0) {
                                        pathBuilder.insert(0, "/");
                                    }
                                    pathBuilder.insert(0, currentDir.getName());
                                    currentDir = currentDir.getParentFile();
                                }

                                if (currentDir != null && currentDir.equals(inputFolder)) {
                                    relativeDir = pathBuilder.toString();
                                }
                            }

                            if (!relativeDir.isEmpty()) {
                                relativePath = relativeDir + "/" + file.getName();
                            } else {
                                relativePath = file.getName();
                            }

                            log.debug("JAR创建 - 使用构建方式计算相对路径: {}", relativePath);
                        }
                    }

                    // 确保相对路径不为空且格式正确
                    if (relativePath == null || relativePath.isEmpty()) {
                        relativePath = file.getName();
                        log.warn("JAR创建 - 无法计算相对路径，使用文件名: {}", relativePath);
                    }

                    // JAR内部路径必须使用正斜杠，不管操作系统
                    relativePath = relativePath.replace(File.separator, "/");

                    // 移除可能的开头斜杠
                    while (relativePath.startsWith("/")) {
                        relativePath = relativePath.substring(1);
                    }

                    log.debug("JAR创建 - 最终JAR Entry路径: {}", relativePath);

                    // 验证路径格式
                    if (relativePath.contains("\\") || relativePath.startsWith(".") || relativePath.contains("..")
                            || relativePath.contains("//")) {
                        log.error("JAR创建 - 检测到异常的JAR Entry路径: {}", relativePath);
                        throw new RuntimeException("异常的JAR Entry路径: " + relativePath);
                    }

                    // 创建JAR条目
                    JarEntry entry = new JarEntry(relativePath);
                    out.putNextEntry(entry);

                    // 读取文件内容并写入JAR
                    in = new FileInputStream(file);
                    byte[] buffer = new byte[8192]; // 更大的缓冲区提高性能
                    int n;
                    while ((n = in.read(buffer)) != -1) {
                        out.write(buffer, 0, n);
                    }
                    // 确保输入流被关闭
                    try {
                        in.close();
                    } catch (IOException e) {
                        log.warn("关闭文件输入流时出错", e);
                    }
                    // 关闭条目和输入流
                    out.closeEntry();
                } catch (Exception e) {
                    log.error("处理文件时出错: {}", file.getPath(), e);
                    throw e; // 抛出异常，不要忽略
                }
            }

            // 确保所有数据都写入
            out.flush();
        } catch (Exception e) {
            log.error("创建JAR文件时出错: {}", outputFileName, e);
            throw e;
        } finally {
            // 确保JAR输出流被关闭
            if (out != null) {
                try {
                    out.close();
                    log.debug("JAR输出流已关闭: {}", outputFileName);
                } catch (IOException e) {
                    log.warn("关闭JAR输出流时出错", e);
                }
            }

            // 主动释放资源
            System.gc();
        }

        log.info("成功创建JAR文件: {}", outputFileName);
    }

    public static void main(String[] args) throws Exception {
        writeJar("E:\\Projects\\MyEclipse9_32\\JAVA.NET_B_ORM\\bbs_schema\\classes",
                "E:\\Projects\\MyEclipse9_32\\JAVA.NET_B_ORM\\schema_jar\\bbs_schema.jar");
    }

}
