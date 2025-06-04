//package org.ark.framework.orm.db;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.Map;
//
//import org.ark.framework.orm.connection.DBConfig;
//import org.junit.Assert;
//import org.junit.Test;
//
//import connection.db.io.arkx.framework.data.ConnectionConfig;
//import connection.db.io.arkx.framework.data.ConnectionPool;
//import connection.db.io.arkx.framework.data.ConnectionPoolManager;
//
///**
//  * ConnectionConfig 测试用例
//  *  
//  * @author Darkness
//  * @date 2011-12-10 下午09:18:01 
//  * @version V1.0
// */
//public class XConnectionConfigTest {
//
////	@Test
//	public void checkConnectionInfo() {
//
//		ConnectionConfig connectionInfo = new ConnectionConfig();
//		connectionInfo.setDatabaseName("bbs");
//		connectionInfo.setUserName("rapidark");
//		connectionInfo.setPassword("password");
//		connectionInfo.setDatabaseType("mysql");
//		connectionInfo.setHost("localhost");
//		connectionInfo.setPoolName("bbsPool");
//
////		connectionInfo.validate();
//		
//		Assert.assertEquals(connectionInfo.getDatabaseType(), "mysql");
//		Assert.assertEquals(connectionInfo.getPort(), 3306);
//	}
//
////	@Test
//	public void readConnectionConfig() {
//		Map<String, ConnectionConfig> dbList = DBConfig.getDatabaseList();
//		Assert.assertEquals(dbList.size(), 1);
//	}
//
//	/**
//	 * if poolName is exist, edit it, else add a new node
//	 * @author Darkness
//	 * @date 2011-12-9 下午05:02:50 
//	 * @version V1.0
//	 */
//	public void writeConnectionConfig() {
//		System.out.println("=================write before=================");
//		Map<String, ConnectionConfig> dbList = DBConfig.getDatabaseList();
//		for (String poolName : dbList.keySet()) {
//			System.out.println(dbList.get(poolName));
//		}
//		
//		ConnectionConfig connectionInfo = new ConnectionConfig();
//		connectionInfo.setDatabaseName("bbs2");
//		connectionInfo.setUserName("root");
//		connectionInfo.setPassword("depravedAngel");
//		connectionInfo.setDatabaseType("mysql");
//		connectionInfo.setHost("localhost");
//		connectionInfo.setPoolName("bbsPool");
//
////		connectionInfo.validate();
//		
//		DBConfig.writeConnectionConfig(connectionInfo);
//		
//		connectionInfo = new ConnectionConfig();
//		connectionInfo.setDatabaseName("bbs");
//		connectionInfo.setUserName("root");
//		connectionInfo.setPassword("depravedAngel");
//		connectionInfo.setDatabaseType("mysql");
//		connectionInfo.setHost("localhost");
//		connectionInfo.setPoolName("bbsNewPool");
//
////		connectionInfo.validate();
//		
//		DBConfig.writeConnectionConfig(connectionInfo);
//		
//		System.out.println("=================write after=================");
//		dbList = DBConfig.getDatabaseList();
//		for (String poolName : dbList.keySet()) {
//			System.out.println(dbList.get(poolName));
//		}
//		
//		DBConfig.removeConnectionConfig("bbsPool");
//		System.out.println("=================delete after=================");
//		dbList = DBConfig.getDatabaseList();
//		for (String poolName : dbList.keySet()) {
//			System.out.println(dbList.get(poolName));
//		}
//	}
//
////	@Test
//	public void createConnection() throws SQLException {
////		Map<String, ConnectionConfig> dbList = DBConfig.getDatabaseList();
////		for (String poolName : dbList.keySet()) {
////			ConnectionConfig connectionInfo = dbList.get(poolName);
////			Connection conn = connectionInfo.createConnection();
////			Assert.assertNotNull(conn);
////			conn.close();
////		}
//	}
//	
////	@Test
//	public void createConnectionPool() throws SQLException {
//		Map<String, ConnectionConfig> dbList = DBConfig.getDatabaseList();
//		for (Map.Entry<String, ConnectionConfig> entry : dbList.entrySet()) {
//			ConnectionPool pool = new ConnectionPool(entry.getValue());
//			Connection connectionOld = null;
//			for (int i = 0; i < 1000; i++) {
//				Connection connectionNew = pool.getConnection();
//				
//				if(i!=0) {
//					Assert.assertEquals(connectionOld, connectionNew);
//				}
//				
//				connectionOld = connectionNew;
//				connectionNew.close();
//			}
//			
//		}
//	}
//	
////	@Test
//	public void getConnFromPool() throws SQLException {
//		
//		Connection connectionOld = null;
//		
//		for (int i = 0; i < 1000; i++) {
//			Connection connectionNew = ConnectionPoolManager.getConnection();
//			if(i!=0) {
//				Assert.assertEquals(connectionOld, connectionNew);
//			}
//			
//			connectionOld = connectionNew;
//			connectionNew.close();
//		}
//		
////		for (int i = 0; i < 1000; i++) {
////			XConnection connection = XConnectionPoolManager.getConnection("bbsNewPool");
////			System.out.println(connection);;
////			connection.close();
////		}
//	}
//}
//
//
//
