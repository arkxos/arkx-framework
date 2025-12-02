// package org.ark.framework.orm.db.dbkeeper;
//
// import java.io.File;
//
// import org.ark.framework.orm.schema.DatabaseConfig;
// import org.ark.framework.orm.schema.DatabaseManager;
// import org.ark.framework.orm.schema.Table;
// import org.ark.framework.util.StringUtil;
//
/// **
// *
// * @author Darkness
// * @date 2012-9-18 下午8:13:44
// * @version V1.0
// */
// public class GenerateService {
//
// private IGenerateConfigProvider configProvider;
//
// public GenerateService(IGenerateConfigProvider configProvider) {
// this.configProvider = configProvider;
// }
//
// public void generate() {
//
// DatabaseConfig databaseConfig = new DatabaseConfig();
// databaseConfig.setName("Default");
// databaseConfig.setHost(configProvider.getHost());
// databaseConfig.setDatabaseName(configProvider.getDatabase());
// databaseConfig.setUserName(configProvider.getUserName());
// databaseConfig.setPassword(configProvider.getPassword());
//
// DatabaseManager.registerDatabase(databaseConfig);
//
// Table[] tables = null;
// String tableNames = configProvider.getTableNames();
// if (!StringUtil.isEmpty(tableNames)) {
// String[] _tableNames = tableNames.split(",");
// tables = DatabaseManager.getDatabase("Default").getTables(_tableNames);
// } else {
// tables = DatabaseManager.getDatabase("Default").getTables();
// }
//
// String packname = configProvider.getPackageName();
// String dirstr = configProvider.getDir();
//
// if (dirstr != null && !dirstr.isEmpty()) {
// if (!dirstr.endsWith("/")) {
// dirstr += "/";
// }
// }
// File dir = new File(dirstr);
// if (packname != null && !packname.equals("")) {
// dir = new File(dirstr + packname.replaceAll("\\.", "/"));
// if (!dir.exists()) {
// dir.mkdirs();
// }
// }
// new ClassGenerator().generate(dir.getAbsolutePath(), packname, tables);
// }
// }
