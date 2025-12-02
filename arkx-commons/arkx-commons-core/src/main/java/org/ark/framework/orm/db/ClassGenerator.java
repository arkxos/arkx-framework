package org.ark.framework.orm.db;
// package org.ark.framework.orm.db;
//
// import java.io.File;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.text.SimpleDateFormat;
// import java.util.Date;
//
//
/// **
// *
// * @author Darkness
// * @date 2012-9-18 下午5:32:50
// * @version V1.0
// */
// public class ClassGenerator {
//
// static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
// /**
// * 根据Table信息创建java文件
// *
// * @author Darkness
// * @date 2012-9-18 下午5:33:21
// * @version V1.0
// */
// public void generate(String outputdir, String packname, Table... tables) {
// for (Table table : tables) {
//
// File file = new File(outputdir, upperFirestChar(table.getTableName()) +
// ".java");
//
// StringBuilder classInfo = new StringBuilder("\t/**\r\n\t*");
// StringBuilder fields = new StringBuilder();
// StringBuilder methods = new StringBuilder();
//
// for (int i = 0; i < table.getColumnSize(); i++) {
// Column column = table.getColumn(i);
//
// fields.append(getFieldStr(column));
// methods.append(getMethodStr(column));
// }
//
// classInfo.append("此类由" + getClass().getSimpleName() + "工具自动生成\r\n");
// classInfo.append("\t*备注(数据表的comment字段)：");
// classInfo.append(table.getComment());
//
// classInfo.append("\r\n");
// classInfo.append("\t*@author
// childlikeman@gmail.com,http://t.qq.com/lostpig\r\n");
// classInfo.append("\t*@since ");
// classInfo.append(sdf.format(new Date()));
// classInfo.append("\r\n\t*/\r\n\r\n");
//
// classInfo.append("\tpublic class
// ").append(upperFirestChar(table.getTableName())).append("{\r\n");
// classInfo.append(fields);
// classInfo.append(methods);
// classInfo.append("\r\n");
// classInfo.append("}");
//
// writePojo(file, "package " + packname + ";\n", classInfo.toString());
// }
// }
//
// public String getFieldStr(Column column) {
// StringBuilder sb = new StringBuilder();
// sb.append("\t").append("private ").append(column.getType()).append("
// ").append(column.getColumnName()).append(";");
// if (column.getComment() != null) {
// sb.append("//").append(column.getComment());
// }
// sb.append("\r\n");
// return sb.toString();
// }
//
// /**
// *
// * @param type
// * @return
// */
// public String getMethodStr(Column column) {
//
// String type = column.getType();
// String field = column.getColumnName();
//
// StringBuilder get = new StringBuilder("\tpublic ");
// get.append(type).append(" ");
// if (type.equals("boolean")) {
// get.append(field);
// } else {
// get.append("get");
// get.append(upperFirestChar(field));
// }
// get.append("(){").append("\r\n\t\treturn
// this.").append(field).append(";\r\n\t}\r\n");
// StringBuilder set = new StringBuilder("\tpublic void ");
//
// if (type.equals("boolean")) {
// set.append(field);
// } else {
// set.append("set");
// set.append(upperFirestChar(field));
// }
// set.append("(").append(type).append("
// ").append(field).append("){\r\n\t\tthis.").append(field).append("=").append(field).append(";\r\n\t}\r\n");
// get.append(set);
// return get.toString();
// }
//
// public String upperFirestChar(String src) {
// return src.substring(0, 1).toUpperCase().concat(src.substring(1));
// }
//
// public void writePojo(File file, String packageinfo, String classInfo) {
// try {
// // 生成.java文件
// FileWriter fw = new FileWriter(file);
// fw.write(packageinfo);
// fw.write(classInfo);
// fw.flush();
// fw.close();
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
//
// /**
// * mysql的數據类型转换到java 數據类型参考文章
// * http://hi.baidu.com/wwtvanessa/blog/item/9fe555945a07bd16d31b70cd.html
// */
// public static String typeTrans(String type) {
// if (type.contains("tinyint")) {
// return "boolean";
// } else if (type.contains("int")) {
// return "int";
// } else if (type.contains("varchar") || type.contains("date") ||
// type.contains("time") || type.contains("datetime") ||
// type.contains("timestamp") || type.contains("text")
// || type.contains("enum") || type.contains("set")) {
// return "String";
// } else if (type.contains("binary") || type.contains("blob")) {
// return "byte[]";
// } else {
// return "String";
// }
// }
// }
