package com.rapidark.framework.data.simplejdbc.connection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**   
 * 连接管理类
 * @author Darkness
 * @website www.rapidark.com
 * @date 2013-4-11 下午08:20:05 
 * @version V1.0   
 */
public class ConnectionManager {
	
	private static Logger log = LoggerFactory.getLogger(ConnectionManager.class);
	
	private static Map<Connection, Boolean> connectionsMap = new HashMap<Connection, Boolean>();
	
	/**
	 * 获取数据库连接
	 * 
	 * @author Darkness
	 * @date 2013-4-11 下午08:20:51 
	 * @version V1.0
	 */
	public static Connection getConnection() {
		
		for (Connection	connection : connectionsMap.keySet()) {
			boolean isUsing = connectionsMap.get(connection);
			if(!isUsing) {
				synchronized (connection) {
					isUsing = connectionsMap.get(connection);
					if(!isUsing) {
						connectionsMap.put(connection, true);
						return connection;
					}
				}
			}
		}
		
		Properties properties = loadDatabaseProperties();
		// 驱动程序名
		String driver = properties.getProperty("jdbc.driverClassName");
		// URL指向要访问的数据库名scutcs
		String url = properties.getProperty("jdbc.url");
		// 用户名
		String user = properties.getProperty("jdbc.username");
		// 密码
		String password = properties.getProperty("jdbc.password");
		
		Connection connection = getConnection(driver, url, user, password);
		connectionsMap.put(connection, true);
		return connection;
	}
	
	public static void closeConnection(Connection connection) {
		connectionsMap.put(connection, false);
	}
	
	/**
	 * 加载数据库连接配置文件
	 * 
	 * @author Darkness
	 * @date 2013-4-12 下午02:36:09 
	 * @version V1.0
	 */
	private static Properties loadDatabaseProperties() {
		InputStream in = ConnectionManager.class.getResourceAsStream("/database.properties");
		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return properties;
	}
	
	/**
	 * 
	 * 
	 * @author Darkness
	 * @date 2013-4-12 下午02:20:08 
	 * @version V1.0
	 */
	private static Connection getConnection(String driver, String url, String user, String password) {
		Connection conn = null;
		try {
			// 加载驱动程序
			Class.forName(driver);

			// 连续数据库
			conn = DriverManager.getConnection(url, user, password);

			if (conn != null && !conn.isClosed())
				log.info("Succeeded connecting to the Database!");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.info("Sorry,can`t find the Driver!");
			e.printStackTrace();
		}

		return conn;
	}
}
