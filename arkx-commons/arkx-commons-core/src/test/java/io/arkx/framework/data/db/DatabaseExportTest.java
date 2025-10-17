package io.arkx.framework.data.db;

import io.arkx.framework.commons.util.ClassLoadUtil;
import io.arkx.framework.data.db.connection.ConnectionPoolManager;
import io.arkx.framework.data.jdbc.SessionFactory;
import lombok.extern.slf4j.Slf4j;
import org.ark.framework.orm.DBExporter;
import org.ark.framework.orm.db.DBOperator;
import org.ark.framework.orm.schema.MySqlDataBaseSchemaGenerator;
import org.ark.framework.orm.schema.SchemaGenerator;
import org.junit.jupiter.api.Test;

import io.arkx.framework.Config;
import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.data.db.connection.ConnectionConfig;
import io.arkx.framework.extend.plugin.ExtendPluginProvider;
import io.arkx.framework.extend.plugin.PluginManager;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

//import static org.ark.framework.orm.sync.Demo.schemaSetSourceDemo;

/**
 *
 * @author Darkness
 * @date 2012-9-21 上午9:49:01
 * @version V1.0
 */
@Slf4j
public class DatabaseExportTest {

	public static void main(String[] args) {
		System.out.println(Config.getClassesPath());
	}

	//	@Test
	public void generateMysql() {

		new MySqlDataBaseSchemaGenerator("rapidark").generate();
	}

	private String getDbFileName()  {
		String prefix = Config.getClassesPath();
		prefix = prefix.substring(0, prefix.length() - 1);
		prefix = prefix.substring(0, prefix.lastIndexOf("/") + 1);

		String dbFilePath = prefix + "/db";

		FileUtil.mkdir(dbFilePath);

		return dbFilePath + "/rapidark.db";
	}

//	@Test
//	public void exportAndImport() {
//		Config.withTestMode();
//		Config.loadConfig();
//		PluginManager.initTestPlugin();
//
//		ExtendPluginProvider.getInstance().start();
////
//////		ConnectionConfig config = new ConnectionConfig();
//////		config.setDatabaseType(ConnectionConfig.MSSQL);
//////		config.setHost("127.0.0.1");
//////		config.setDatabaseName("");
//////		config.setUserName("root");
//////		config.setPassword("root");
////
////		ConnectionConfig config = new ConnectionConfig();
////		config.setDatabaseType(ConnectionConfig.MYSQL);
////		config.setHost("127.0.0.1");
////		config.setPort("33061");
////		config.setDatabaseName("testdb");
////		config.setUserName("root");
////		config.setPassword("root1234");
////
////		// export db
////		try {
////			String exportFilePath = getDbFileName();
////			DBOperator.exportDB(config, exportFilePath);
////
////
////			String db = config.DBName.toLowerCase();
////			String packageStr = SchemaGenerator.PACKAGE  + "." + db + ".schema";
////			String javapath = DBOperator.getDefaultPath() + db + "_schema/src/" + packageStr.replaceAll("\\.", "/");
////
////			String[] schemas = new File(javapath).list();
////
////			List<String> schemaNameList = new ArrayList<String>();
////
////			for (String schema : schemas) {
////
////				if (schema.endsWith("Schema.java")) {
////					schemaNameList.add(packageStr + "." + schema.replace(".java", ""));
////				}
////
////				if(schema.endsWith("Set.java")) {
////					continue;
////				}
////			}
////
////			String jarFile = DBOperator.getSchemaJarPath()+ "/" +db +"_schema.jar";
////			ClassLoader classLoader = ClassLoadUtil.getClassLoad(jarFile, true);
////
////			new DBExporter().exportDB(exportFilePath, schemaNameList.toArray(new String[0]), classLoader);
////
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
//
//		// import db
//		try {
//			DBOperator.importDB("TargetDb02", getDbFileName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//
//	@Test
//	public void export() {
//		Config.withTestMode();
//		Config.loadConfig();
//		PluginManager.initTestPlugin();
//
//		ExtendPluginProvider.getInstance().start();
////
//////		ConnectionConfig config = new ConnectionConfig();
//////		config.setDatabaseType(ConnectionConfig.MSSQL);
//////		config.setHost("127.0.0.1");
//////		config.setDatabaseName("yiliaozerogo");
//////		config.setUserName("root");
//////		config.setPassword("root");
//
//		ConnectionConfig config = new ConnectionConfig();
//		config.setDatabaseType(ConnectionConfig.DM);
//		config.setHost("127.0.0.1");
//		config.setPort("5236");
//		config.setDatabaseName("test_DEV");
//		config.setUserName("SYSDBA");
//		config.setPassword("SYSDBA");
//		config.setTestTable("dual");
//		config.setPoolName("DM");
//
//		ConnectionPoolManager.addPool(config);
//		ConnectionPoolManager.setThreadCurrentPool("DM");
//		;
////		config.setDatabaseType(ConnectionConfig.MYSQL);
////		config.setHost("127.0.0.1");
////		config.setPort("33061");
////		config.setDatabaseName("testdb");
////		config.setUserName("root");
////		config.setPassword("root1234");
//
//
//		// export db
//		try {
//			String exportFilePath = getDbFileName();
//			DBOperator.exportDB(config, exportFilePath);
//
//
//			String db = config.DBName.toLowerCase();
//			String packageStr = SchemaGenerator.PACKAGE  + "." + db + ".schema";
//			String javapath = DBOperator.getDefaultPath() + db + "_schema/src/" + packageStr.replaceAll("\\.", "/");
//
//			String[] schemas = new File(javapath).list();
//
//			List<String> schemaNameList = new ArrayList<String>();
//
//			for (String schema : schemas) {
//
//				if (schema.endsWith("Schema.java")) {
//					schemaNameList.add(packageStr + "." + schema.replace(".java", ""));
//				}
//
//				if(schema.endsWith("Set.java")) {
//					continue;
//				}
//			}
//
//			String jarFile = DBOperator.getSchemaJarPath()+ "/" +db +"_schema.jar";
//			ClassLoader classLoader = ClassLoadUtil.getClassLoad(jarFile, true);
//
//			new DBExporter().exportDB(exportFilePath, schemaNameList.toArray(new String[0]), classLoader);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//
//
//	@Test
//	public void DMImportToDM() {
//		Config.withTestMode();
//		Config.loadConfig();
//		PluginManager.initTestPlugin();
//
//		ExtendPluginProvider.getInstance().start();
////
//////		ConnectionConfig config = new ConnectionConfig();
//////		config.setDatabaseType(ConnectionConfig.MSSQL);
//////		config.setHost("127.0.0.1");
//////		config.setDatabaseName("test");
//////		config.setUserName("root");
//////		config.setPassword("root");
////
////		ConnectionConfig config = new ConnectionConfig();
////		config.setDatabaseType(ConnectionConfig.MYSQL);
////		config.setHost("127.0.0.1");
////		config.setPort("33061");
////		config.setDatabaseName("testdb");
////		config.setUserName("root");
////		config.setPassword("root1234");
////
////		// export db
////		try {
////			String exportFilePath = getDbFileName();
////			DBOperator.exportDB(config, exportFilePath);
////
////
////			String db = config.DBName.toLowerCase();
////			String packageStr = SchemaGenerator.PACKAGE  + "." + db + ".schema";
////			String javapath = DBOperator.getDefaultPath() + db + "_schema/src/" + packageStr.replaceAll("\\.", "/");
////
////			String[] schemas = new File(javapath).list();
////
////			List<String> schemaNameList = new ArrayList<String>();
////
////			for (String schema : schemas) {
////
////				if (schema.endsWith("Schema.java")) {
////					schemaNameList.add(packageStr + "." + schema.replace(".java", ""));
////				}
////
////				if(schema.endsWith("Set.java")) {
////					continue;
////				}
////			}
////
////			String jarFile = DBOperator.getSchemaJarPath()+ "/" +db +"_schema.jar";
////			ClassLoader classLoader = ClassLoadUtil.getClassLoad(jarFile, true);
////
////			new DBExporter().exportDB(exportFilePath, schemaNameList.toArray(new String[0]), classLoader);
////
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
//
//		// import db
//		try {
//			DBOperator.importDB("SYNC_TEST", getDbFileName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//
//	@Test
//	public void DMImportToMysql() {
//		Config.withTestMode();
//		Config.loadConfig();
//		PluginManager.initTestPlugin();
//
//		ExtendPluginProvider.getInstance().start();
//
//		// import db
//		try {
//			DBOperator.importDB("TestMysql02", getDbFileName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//
//	@Test
//	public void test() {
//		try {
//			log.info("开始演示增量同步功能");
//
//			Config.withTestMode();
//			Config.loadConfig();
//			PluginManager.initTestPlugin();
//
//			ExtendPluginProvider.getInstance().start();
//
//			String databaseType = ConnectionPoolManager.getDBConnConfig("TestMysql02").getDatabaseType();
//
//			//先加载jar包
//			try {
//				ClassLoadUtil.addJarPath("E:\\db-data\\");
//			} catch (MalformedURLException e) {
//				throw new RuntimeException(e);
//			}
//
//			// 指定.db文件路径
//			String dbFilePath = "E:\\db-data\\rapidark.db";
//
//			// SchemaSet数据源模式 - 使用真实的数据文件
////			schemaSetSourceDemo(dbFilePath);
//
//			// 独立SchemaSet数据源 - 使用真实的数据文件
//			// standaloneSchemaSetDataSourceDemo(dbFilePath);
//
//			log.info("增量同步演示完成");
//		} catch (Exception e) {
//			log.error("演示过程中发生错误", e);
//		}
//	}
}
