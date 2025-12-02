package org.ark.framework.orm.sync;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:32
 * @since 1.0
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * 自动依赖提取工具 分析类文件并提取所有依赖
 */
public class DependencyExtractor {

    private final String sourceProjectPath;
    private final String targetProjectPath;
    private final String baseClassName;
    private final Set<String> processedClasses = new HashSet<>();

    public DependencyExtractor(String sourceProjectPath, String targetProjectPath, String baseClassName) {
        this.sourceProjectPath = sourceProjectPath;
        this.targetProjectPath = targetProjectPath;
        this.baseClassName = baseClassName;
    }

    public void extract() throws IOException {
        // 创建目标目录
        new File(targetProjectPath + "/src/main/java").mkdirs();

        // 开始递归分析依赖
        analyzeDependencies(baseClassName);

        System.out.println("依赖提取完成！共处理 " + processedClasses.size() + " 个类。");
    }

    private void analyzeDependencies(String className) throws IOException {
        // 避免重复处理
        if (processedClasses.contains(className)) {
            return;
        }

        processedClasses.add(className);
        System.out.println("处理类: " + className);

        // 将类名转换为文件路径
        String classFilePath = sourceProjectPath + "/target/classes/" + className.replace('.', '/') + ".class";
        String sourceFilePath = sourceProjectPath + "/src/main/java/" + className.replace('.', '/') + ".java";

        File classFile = new File(classFilePath);
        File sourceFile = new File(sourceFilePath);

        // 复制源文件到目标项目
        if (sourceFile.exists()) {
            copyFile(sourceFile);
        }

        // 如果编译后的类文件存在，分析其依赖
        if (classFile.exists()) {
            Set<String> dependencies = findDependencies(classFile);

            // 递归处理所有项目内部依赖
            for (String dependency : dependencies) {
                // 跟项目包名相同的依赖才处理
                if (dependency.startsWith(getBasePackage(className)) && !dependency.startsWith("java.")
                        && !dependency.startsWith("javax.")) {
                    analyzeDependencies(dependency);
                }
            }
        }
    }

    private String getBasePackage(String className) {
        // 获取基础包名（通常是前两个包名部分）
        String[] parts = className.split("\\.");
        if (parts.length >= 2) {
            return parts[0] + "." + parts[1];
        }
        return parts[0];
    }

    private Set<String> findDependencies(File classFile) throws IOException {
        Set<String> dependencies = new HashSet<>();

        try {
            // 使用ASM读取类文件
            ClassReader reader = new ClassReader(Files.readAllBytes(classFile.toPath()));
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);

            // 添加父类
            if (classNode.superName != null) {
                dependencies.add(classNode.superName.replace('/', '.'));
            }

            // 添加接口
            for (String iface : classNode.interfaces) {
                dependencies.add(iface.replace('/', '.'));
            }

            // 添加字段类型
            classNode.fields.forEach(field -> {
                String desc = field.desc;
                extractClassNamesFromDesc(desc, dependencies);
            });

            // 添加方法参数和返回类型
            for (MethodNode method : classNode.methods) {
                extractClassNamesFromDesc(method.desc, dependencies);
                // 处理方法中的局部变量和方法调用需要更复杂的分析
            }

        } catch (Exception e) {
            System.err.println("分析类 " + classFile.getName() + " 时出错: " + e.getMessage());
        }

        return dependencies;
    }

    private void extractClassNamesFromDesc(String desc, Set<String> dependencies) {
        // 处理数组类型
        while (desc.startsWith("[")) {
            desc = desc.substring(1);
        }

        // 处理对象类型
        if (desc.startsWith("L") && desc.endsWith(";")) {
            String className = desc.substring(1, desc.length() - 1).replace('/', '.');
            dependencies.add(className);
        }
    }

    private void copyFile(File sourceFile) throws IOException {
        String relativePath = sourceFile.getPath().substring(sourceProjectPath.length());
        String targetFilePath = targetProjectPath + relativePath;

        // 确保目标目录存在
        Path targetDir = Paths.get(targetFilePath).getParent();
        if (targetDir != null) {
            Files.createDirectories(targetDir);
        }

        // 复制文件
        Files.copy(sourceFile.toPath(), Paths.get(targetFilePath), StandardCopyOption.REPLACE_EXISTING);

        System.out.println("已复制: " + relativePath);
    }

    public static void main(String[] args) {
        try {

            DependencyExtractor extractor = new DependencyExtractor("org.ark.framework.orm.sync",
                    "com.ark.data.sync.old.lib", "org.ark.framework.orm.sync.Demo");
            extractor.extract();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
