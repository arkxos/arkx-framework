package org.ark.framework.orm.db;
// package org.ark.framework.orm.db;
//
// import java.io.File;
//
// import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
//
// import org.ark.framework.Config;
// import org.ark.framework.infrastructure.repositories.DatabaseConfig;
//
/// **
// *
// * @author Darkness
// * @date 2012-9-21 上午9:48:22
// * @version V1.0
// */
// public class DBExport {
//
//// private Database database;
////
//// public DBExport(DatabaseConfig config) {
//// database = DatabaseManager.registerDatabase(config);
//// }
//
// public void export(String file) {
//
// ClassGenerator classGenerator = new ClassGenerator();
//
// String classGeneratePath = Config.getClassesPath() + "schema_generate_temp/"
// + packageName().replaceAll("\\.", "/");
// System.out.println(classGeneratePath);
// File classGeneratePathFile = new File(classGeneratePath);
// if(!classGeneratePathFile.exists()) {
// classGeneratePathFile.mkdirs();
// }
// classGenerator.generate(classGeneratePath, packageName(),
// database.getTables());
// }
//
// /**
// * 导出过程中生成的java文件所属包
// *
// * @author Darkness
// * @date 2012-9-21 上午10:06:30
// * @version V1.0
// */
// protected String packageName() {
// return "org.ark." + database.getConfig().getDatabaseName().toLowerCase() +
// ".schema";
// }
//
// }
