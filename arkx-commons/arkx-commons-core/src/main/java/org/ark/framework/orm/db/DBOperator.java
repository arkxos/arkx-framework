package org.ark.framework.orm.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.ark.framework.orm.DBExporter;
import org.ark.framework.orm.DBImporter;
import org.ark.framework.orm.schema.MySqlDataBaseSchemaGenerator;
import org.ark.framework.orm.schema.OracleDataBaseSchemaGenerator;
import org.ark.framework.orm.schema.SchemaGenerator;
import org.ark.framework.orm.schema.SqlServerDataBaseSchemaGenerator;
import org.ark.framework.orm.sql.DBContext;

import io.arkx.framework.Config;
import io.arkx.framework.commons.util.ClassLoadUtil;
import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.JarUtil;
import io.arkx.framework.data.db.connection.ConnectionConfig;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;

import lombok.extern.slf4j.Slf4j;

/**   
 * @class org.ark.framework.orm.db.DBOperator
 * @author Darkness
 * @date 2012-9-6 上午10:35:31 
 * @version V1.0   
 */
@Slf4j
public class DBOperator {
	
//	private static Logger logger = log.getLogger(DBOperator.class);

	private static String defaultPaht;
	
	public static String getDefaultPath()  {
		
		if(defaultPaht == null) {
			String prefix = Config.getClassesPath();
			prefix = prefix.substring(0, prefix.length() - 1);
			prefix = prefix.substring(0, prefix.lastIndexOf("/") + 1);
			
			defaultPaht = prefix;
		}
		
		return defaultPaht;
	}
	
	public static String getSchemaJarPath() {
		return getDefaultPath() + "/schema_jar";
	}
	
	//String db = "carbon";"d:/test2.db"
	public static void exportDB(ConnectionConfig config, String exportFilePath) throws Exception {
		Session session = SessionFactory.openSessionInThread();
		session.beginTransaction();
//		DBContext.setCurrentContext(db);
		
		String db = config.DBName.toLowerCase();
		
		String packageStr = SchemaGenerator.PACKAGE + "." + db + ".schema";
		
		String javapath = getDefaultPath() + db + "_schema/src/" + packageStr.replaceAll("\\.", "/");
		FileUtil.mkdir(javapath);
		FileUtil.deleteEx(javapath + "/.+java");
		
		SchemaGenerator schemaGenerator = null;
		if (config.isOracle()) {
			schemaGenerator = new OracleDataBaseSchemaGenerator(packageStr, javapath);
		} else if(config.isMysql()) {
			schemaGenerator = new MySqlDataBaseSchemaGenerator(config.DBName, packageStr, javapath);
		} else if(config.isSQLServer()) {
			schemaGenerator = new SqlServerDataBaseSchemaGenerator(config.DBName, packageStr, javapath);
		}
		schemaGenerator.generate();
		
		String[] schemas = new File(javapath).list();
		
		FileUtil.mkdir(getDefaultPath() + db + "_schema/classes");
		
		List<String> schemaNameList = new ArrayList<String>();
		
		for (String schema : schemas) {
			
			if (schema.endsWith("Schema.java")) {
				schemaNameList.add(packageStr + "." + schema.replace(".java", ""));
			}
			
			if(schema.endsWith("Set.java")) {
				continue;
			}
			String[] javacArgs = new String[] { 
					"-sourcepath", javapath, 
					"-d", getDefaultPath() + db + "_schema/classes", 
					javapath + "/"+schema.replace("Schema", "Set"), 
					javapath + "/"+schema,
					"-encoding", "UTF-8",
				};
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			compiler.run(null, null, null, javacArgs);
//			Main.compile(javacArgs);
		}
		
		FileUtil.mkdir(getSchemaJarPath());
		
		String jarFile = getSchemaJarPath() + "/"+db+"_schema.jar";
		JarUtil.writeJar((getDefaultPath() + db + "_schema\\classes").replace("/", "\\"), jarFile);
		
		//FileUtil.delete(getDefaultPath() + db + "_schema");
		
		log.info("导出数据库生成schema jar 文件地址：" + jarFile);
		
		ClassLoader classLoader = ClassLoadUtil.getClassLoad(jarFile, true);
		
		new DBExporter().exportDB(exportFilePath, schemaNameList.toArray(new String[0]), classLoader);
		
		SessionFactory.clearCurrentSession();
	}
	
	//"d:/test2.db", "carbon"
	public static void importDB(String db, String exportFilePath) throws Exception {

		DBContext.setCurrentContext(db);
		
		log.info("导入数据库所需schema jar 文件目录：" + getSchemaJarPath());
		
		ClassLoadUtil.addJarPath(getSchemaJarPath());
		
		new DBImporter().importDB(exportFilePath, db);
	}
	
	public static void main(String[] args) throws Exception {
		
//		importDB("carbon", "d:/test2.db");
		System.out.println(Config.getContextRealPath());
	}

}
