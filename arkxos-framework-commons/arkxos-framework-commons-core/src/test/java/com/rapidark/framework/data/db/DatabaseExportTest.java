package com.rapidark.framework.data.db;

import org.ark.framework.orm.db.DBOperator;
import org.ark.framework.orm.schema.MySqlDataBaseSchemaGenerator;
import org.junit.jupiter.api.Test;

import com.arkxos.framework.commons.util.FileUtil;
import com.arkxos.framework.data.db.connection.ConnectionConfig;
import com.arkxos.framework.extend.plugin.ExtendPluginProvider;
import com.arkxos.framework.extend.plugin.PluginManager;
import com.rapidark.framework.Config;

/**   
 * 
 * @author Darkness
 * @date 2012-9-21 上午9:49:01 
 * @version V1.0   
 */
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
	
	@Test
	public void exportAndImport() {
		Config.withTestMode();
		Config.loadConfig();
		
		PluginManager.initTestPlugin();
		
		ExtendPluginProvider.getInstance().start();
		
//		ConnectionConfig config = new ConnectionConfig();
//		config.setDatabaseType(ConnectionConfig.MSSQL);
//		config.setHost("127.0.0.1");
//		config.setDatabaseName("yiliaozerogo");
//		config.setUserName("root");
//		config.setPassword("root");
		
		ConnectionConfig config = new ConnectionConfig();
		config.setDatabaseType(ConnectionConfig.MSSQL);
		config.setHost("192.168.132.13");
		config.setDatabaseName("njsys_ThreeInOne");
		config.setUserName("zbsa");
		config.setPassword("sysjg321#@!");
		
		// export db
		try {
			String exportFilePath = getDbFileName();
			DBOperator.exportDB(config, exportFilePath);
			
			
//			String db = config.DBName.toLowerCase();
//			String packageStr = SchemaGenerator.PACKAGE + "." + db + ".schema";
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
//			String jarFile = DBOperator.getSchemaJarPath() + "/"+db+"_schema.jar";
//			ClassLoader classLoader = ClassLoadUtil.getClassLoad(jarFile, true);
//			
//			new DBExporter().exportDB(exportFilePath, schemaNameList.toArray(new String[0]), classLoader);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// import db
		try {
			DBOperator.importDB("Default_oracle", getDbFileName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
