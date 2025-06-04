package org.ark.framework.orm.connection;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ark.framework.orm.sql.DBContext;
import org.ark.framework.security.EncryptUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.PropertiesUtil;
import com.arkxos.framework.data.db.connection.ConnectionConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * @class org.ark.framework.orm.connection.DBConfig
 * 配置示例 
 * <database name="Default"> 
 * 	<Type>MYSQL</Type>
 * 	<ServerAddress>localhost</ServerAddress> 
 * 	<Port>3306</Port> 
 * 	<Name>bbs</Name>
 * 	<UserName>root</UserName> 
 * 	<Password>$KEYw//j8IH+fBtReFR4D88/Hw==</Password>
 * 	<MaxConnCount>1000</MaxConnCount> 
 * 	<InitConnCount>0</InitConnCount>
 * 	<TestTable>article</TestTable> 
 * </database>
 * 
 * @author Darkness
 * @version 1.0
 * @since JDF 1.0
 */
@Slf4j
public class DBConfig {

//	private static Logger logger = log.getLogger(DBConfig.class);
	
	private static Map<String, ConnectionConfig> databaseList;

	public static Map<String, ConnectionConfig> getDatabaseList() {
		loadDataBasesConfig();
		return databaseList;
	}

	/**
	 * 转换连接池的名称
	 * 
	 * @param poolName
	 * @return
	 */
	static String convertPoolName(String poolName) {
		if (poolName == null || poolName.equals("")) {
			if (DBContext.getCurrentContext() == null) {
				return "Default";
			}
			poolName = DBContext.getCurrentContext().getPoolName();
			if (poolName == null || poolName.equals("")) {
				poolName = "Default";
			}
		}
		return poolName;
	}

	/**
	 * @tag category
	 * name = "Get Connection Info"
	 * color = "Blue"
	 */
	public static ConnectionConfig getDatabase() {
		return getDatabase(convertPoolName(null));
	}

	/**
	 * @tag category
	 * name = "Get Connection Info"
	 * color = "Blue"
	 */
	public static ConnectionConfig getDatabase(String dbname) {
		
		if (dbname.endsWith(".")) {
			dbname = dbname.replace(".", "");
		}
		loadDataBasesConfig();

		return databaseList.get(dbname);
	}

	/**
	 * 读取xconnection.xml配置文件，用SAX读取出Document
	 * 
	 * @author Darkness
	 * @date 2011-12-9 上午11:11:11
	 * @version V1.0
	 * @return
	 * @tag category name = "配置文件操作" color = "Blue"
	 */
	private static Document getDataBaseConfigRoot() {
		URL url = Thread.currentThread().getContextClassLoader().getResource("xconnection.xml");
		if (url == null) {
			throw new RuntimeException("请确认在classpath根目录下配置了xconnection.xml...");
		}

		return null;
//		return XmlUtil.saxReadDocument(url);
	}

	/**
	 * 将xconnection.xml中的db配置加载到databaseList列表中
	 * 
	 * @author Darkness
	 * @date 2011-12-9 上午11:10:35
	 * @version V1.0
	 * @tag category name = "配置文件操作" color = "Blue"
	 */
	@SuppressWarnings("unchecked")
	static void loadDataBasesConfig() {

		databaseList = new HashMap<String, ConnectionConfig>();

		Document doc = getDataBaseConfigRoot();
		Element root = doc.getRootElement();

		Mapx<String, String> propsMap = new Mapx<String, String>();
		Element properties = root.element("properties");
		if(properties != null) {
			List<Element> propFiles = properties.elements();
			for (Element element : propFiles) {
				URL url = Thread.currentThread().getContextClassLoader().getResource(element.getText());
				log.debug("dbconfig 读取properties文件["+url.getPath()+"]中的配置");
				File file = new File(url.getPath());
				propsMap.putAll(PropertiesUtil.read(file));
			}
			
		}
		Element databases = root.element("databases");
		
		if (databases != null) {
			List<Element> dbs = databases.elements();
			for (int i = 0; i < dbs.size(); i++) {
				Element ele = dbs.get(i);
				
				ConnectionConfig database = null;// BeanCopyer.copy(ele, XConnectionInfo.class);
				
				database.setPoolName(ele.attributeValue("name").trim());

				
				
//				database.setJdbcUrl(new StringFormat(database.getJdbcUrl(), propsMap).toString());
//				database.setDBUserName(new StringFormat(database.getDBUserName(), propsMap).toString());
//				database.setDBPassword(new StringFormat(database.getDBPassword(), propsMap).toString());
//				database.setTestTable(new StringFormat(database.getTestTable(), propsMap).toString());
//				database.setDBPassword(new StringFormat(database.getDBPassword(), propsMap).toString());
				
				String password = database.getPassword();
				if (password.startsWith("$KEY")) {
					password = EncryptUtil.decrypt3DES(password.substring(4), "27jrWz3sxrVbR+pnyg6j");
					database.setPassword(password);
				}
				
//				database.validate();
				
				// dbtype设置必须的数据库信息，以提供连接的创建
//				database.getDatabaseType().setDbInfo(database);
				
				databaseList.put(database.getPoolName(), database);
			}
		}

	}
	
	/**
	 * remove connection config by poolName
	 * 
	 * @author Darkness
	 * @date 2011-12-9 下午05:14:38 
	 * @version V1.0  
	 * @param poolName
	 * @tag category
	 * name = "配置文件操作"
	 * color = "Blue"
	 */
	@SuppressWarnings("unchecked")
	public static void removeConnectionConfig(String poolName) {
		Document doc = getDataBaseConfigRoot();
		Element root = doc.getRootElement();

		Element databases = root.element("databases");
		
		List<Element> dbList = databases.elements();
		for (Element ele : dbList) {// poolName is unique, if exist, edit it
			if(poolName.equals(ele.attribute("name").getText())) {
				databases.remove(ele);
				break;
			}
		}
		
//		XmlUtil.write(Thread.currentThread().getContextClassLoader().getResource("xconnection.xml"), doc);
	}
	
	/**
	 * @author Darkness
	 * @date 2011-11-22 下午07:58:07
	 * @version V1.0
	 * @param poolName
	 * @param dbType
	 * @param serverAddress
	 * @param port
	 * @param dbName
	 * @param userName
	 * @param password
	 * @param initConnCount
	 * @tag category name = "配置文件操作" color = "Blue"
	 */
	@SuppressWarnings("unchecked")
	public static void writeConnectionConfig(ConnectionConfig connectionInfo) {
		Document doc = getDataBaseConfigRoot();
		Element root = doc.getRootElement();

		Element databases = root.element("databases");
		Element database = null;
		
		List<Element> dbList = databases.elements();
		for (Element ele : dbList) {// poolName is unique, if exist, edit it
			if(connectionInfo.getPoolName().equals(ele.attribute("name").getText())) {
				database = ele;
				break;
			}
		}
		if(database == null) {
			database = databases.addElement("database");
		}
		
//		BeanCopyer.copy(connectionInfo, database);
		
		database.addAttribute("name", connectionInfo.getPoolName());
		
//		XmlUtil.write(Thread.currentThread().getContextClassLoader().getResource("xconnection.xml"), doc);
	}

}

