package io.arkx.framework.commons.util;

import java.io.*;

/**
 * @class CountCodeLines 统计系统代码行数
 * @private
 * @author Darkness
 * @date 2012-4-6 上午9:56:50
 * @version V1.0
 */
class CountCodeLines {

    public static void main(String[] args) {

        StringBuffer pathName = new StringBuffer("D:\\git\\P201801Chemicals");
        ComputeDirectoryAndFiles(pathName, 0);
        // pathName = new
        // StringBuffer("C:\\Users\\Administrator\\Workspaces\\MyEclipse10\\ark-exam");
        // ComputeDirectoryAndFiles(pathName, 0);
        // pathName = new
        // StringBuffer("C:\\Users\\Administrator\\Workspaces\\MyEclipse10\\ark-platform\\ark-util");
        // ComputeDirectoryAndFiles(pathName, 0);
        // pathName = new
        // StringBuffer("C:\\Users\\Administrator\\Workspaces\\MyEclipse10\\ark-platform\\ark-core");
        // ComputeDirectoryAndFiles(pathName, 0);
        // pathName = new
        // StringBuffer("C:\\Users\\Administrator\\Workspaces\\MyEclipse10\\ark-platform\\ark-model");
        // ComputeDirectoryAndFiles(pathName, 0);
        // pathName = new
        // StringBuffer("C:\\Users\\Administrator\\Workspaces\\MyEclipse10\\ark-platform\\ark-admin");
        // ComputeDirectoryAndFiles(pathName, 0);
        // pathName = new
        // StringBuffer("C:\\Users\\Administrator\\Workspaces\\MyEclipse10\\ark-platform\\ark-plat-web");
        // ComputeDirectoryAndFiles(pathName, 0);

        System.out.println("All Lines : " + (tatolLines));
        System.out.println("Code Lines : " + (codeLines = tatolLines - commentLines - whiteLines));
        System.out.println("White Lines : " + whiteLines);
        System.out.println("Comment Lines : " + commentLines);

    }

    private static String fileToBeCounted[] = {"java", "jsp", "zhtml", "js", "css", "html", "htm", "xml"};// 所需计算的源码文件类型

    private static boolean isFileToBeCounted(String type) {
        for (int i = 0; i < fileToBeCounted.length; i++) {
            if (type.matches("^[a-zA-Z[^0-9]]\\w*." + fileToBeCounted[i] + "$"))
                return true;
        }
        return false;
    }

    static int codeLines = 0;

    static int whiteLines = 0;

    static int commentLines = 0;

    static int tatolLines = 0;

    static boolean bComment = false;

    public static void ComputeDirectoryAndFiles(StringBuffer pathName, int level) {
        File directory = new File(pathName.toString());
        File[] files = directory.listFiles();
        String prefix = "";
        for (int i = 0; i < files.length; i++) {
            prefix += "** ";
        }
        if (directory.isDirectory()) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && isFileToBeCounted(files[i].getName())) {

                    computeLines(files[i]);
                }
                if (files[i].isDirectory()) {

                    pathName.append("/" + files[i].getName());
                    level++;
                    ComputeDirectoryAndFiles(pathName, level);
                    int start = pathName.toString().length() - files[i].getName().length() - 1;
                    int end = pathName.toString().length();
                    pathName.delete(start, end);

                    level--;
                }
            }
        }
    }

    public static void computeLines(File file) {
        BufferedReader bf = null;

        try {
            bf = new BufferedReader(new FileReader(file));
            String lineStr = "";
            while ((lineStr = bf.readLine()) != null) {
                // 总行数
                tatolLines++;
                // 计算空行
                whiteLines(lineStr);
                // 统计代码行数
                commendLines(lineStr);
                // 计算代码的行数
                // codeLines(lineStr);
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件没有找到");
        } catch (IOException ee) {
            System.out.println("输入输出异常　");
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                    bf = null;
                } catch (Exception e) {
                    System.out.println("关闭BufferReader时出错");
                }
            }
        }
    }

    public static void whiteLines(String lineStr) {
        if (lineStr.matches("^[\\s&&[^\\n]]*$")) {
            whiteLines++;
        }
    }

    public static void commendLines(String lineStr) {

        // 判断是否是一个注释行
        // 这里是单行注释的如 /*..... */或/**.... */
        if (lineStr.matches("\\s*/\\*{1,}.*(\\*/).*")) {
            commentLines++;
        }
        /**
         * 这里是多行注释的
         */
        // 这里的是当开始为/**或/*但是没有 */ 关闭时
        else if (lineStr.matches("\\s*/\\*{1,}.*[^\\*/].*")) {
            commentLines++;
            bComment = true;
        } else if (true == bComment) {
            commentLines++;
            if (lineStr.matches("\\s*[\\*/]+\\s*")) {
                bComment = false;
            }
        } else if (lineStr.matches("^[\\s]*//.*")) {
        }

    }
    // public static void codeLines(String lineStr)
    // {
    // if(lineStr.matches("\\s*[[^/\\*]|[//]]+.*"))
    // codeLines++;
    // }
}
