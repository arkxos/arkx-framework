package com.arkxos.framework.data.jdbc.connection;
//package org.ark.framework.persistence.jdbc.connection;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.ark.framework.orm.sql.CurrentConnection;
//import org.ark.framework.orm.sql.DBContext;
//
//import com.arkxos.framework.framework.Config;
//import com.arkxos.framework.framework.collection.Mapx;
//
///**
// * @class org.ark.framework.orm.connection.XConnectionPoolManager
// * 连接池管理器，用于获取数据库连接
// * 
//
// */
//public class ConnectionPoolManager {
//
//	protected static Mapx<String, ConnectionPool> PoolMap = new Mapx<>();
//	private static Object mutex = new Object();
//	private static Timer timer;
//
//	//[start] 获取连接
//	public static Connection getConnection() {
//		if(CurrentConnection.getCurrentThreadConnection() != null) {
//			return CurrentConnection.getCurrentThreadConnection();
//		}
//		return getConnection(getCurrentDefaultPoolName(), false);
//	}
//	
//	private static String getCurrentDefaultPoolName() {
//		String poolName = null;
//		if(DBContext.getCurrentContext() != null) {
//			poolName = DBContext.getCurrentContext().getPoolName();
//		}
//		return poolName;
//	}
//
//	public static Connection getConnection(boolean bLongTimeFlag) {
//		return getConnection(getCurrentDefaultPoolName(), bLongTimeFlag);
//	}
//	
//	public static Connection getConnection(String poolName) {
//		return getConnection(poolName, false);
//	}
//	
//	public static Connection getConnection(String poolName, boolean bLongTimeFlag) {
//		return getConnection(poolName, bLongTimeFlag, true);
//	}
//
//	public static Connection getConnection(String poolName, boolean bLongTimeFlag, boolean bCurrentThreadConnectionFlag) {
//		
//		if (timer == null) {
//			synchronized (mutex) {
//				initTimer();
//			}
//		}
//		
//		poolName = convertPoolName(poolName);
//		
//		if (bCurrentThreadConnectionFlag) {
//			Connection conn = CurrentConnection.getCurrentThreadConnection();//BlockingTransaction.getCurrentThreadConnection();
//			if ((conn != null) && (conn.DBConfig.PoolName.equals(poolName))) {
//				return conn;
//			}
//		}
//		ConnectionPool pool = PoolMap.get(poolName);
//		if (pool == null) {
//			synchronized (mutex) {
//				pool = PoolMap.get(poolName);
//				if (pool == null) {
//					if (Config.getValue("Database." + poolName + "Type") != null) {
//						pool = new ConnectionPool(poolName);
//						PoolMap.put(poolName, pool);
//					} else {
//						throw new RuntimeException("DB Connection Pool not found:" + poolName);
//					}
//				}
//			}
//		}
//
//		return pool.getConnection(bLongTimeFlag);
//	}
//	//[end]
//
//	//[start] 私有辅助函数
//	private static String convertPoolName(String poolName) {
//
//		if ((poolName == null) || (poolName.equals(""))) {
//			return "Default.";
//		}
//
//		return poolName + ".";
//	}
//	
//	private static Mapx<String, ConnectionPool> getPoolMap() {
//		return PoolMap;
//	}
//	//[end]
//
//	//[start] 获取连接配置
//	public static ConnectionConfig getDBConnConfig() {
//		return getDBConnConfig(null);
//	}
//	
//	public static ConnectionConfig getDBConnConfig(String poolName) {
//
//		poolName = convertPoolName(poolName);
//
//		ConnectionPool pool = PoolMap.get(poolName);
//
//		if (pool == null) {
//			synchronized (mutex) {
//
//				pool = PoolMap.get(poolName);
//				
//				if (pool == null) {
//					
//					if (Config.getValue("Database." + poolName + "Type") == null) {
//						throw new RuntimeException("DB Connection Pool not found:" + poolName);
//					}
//					
//					pool = new ConnectionPool(poolName);
//					PoolMap.put(poolName, pool);
//				}
//			}
//		}
//		return pool.getDBConnConfig();
//	}// [end]
//	
//	//[start] 连接池管理
//	private static void initTimer() {
//		
//		if (timer != null) {
//			return;
//		}
//		
//		timer = new Timer("DBConnPool KeepAlive Timer", true);
//		timer.scheduleAtFixedRate(new TimerTask() {
//			public void run() {
//				if (ConnectionPoolManager.PoolMap == null) {
//					return;
//				}
//				for (String name : ConnectionPoolManager.PoolMap.keyArray()) {
//					ConnectionPool pool = PoolMap.get(name);
//					pool.keepAlive();
//				}
//			}
//		}, 0L, 60 * 1000L);
//	}
//
//	/**
//	 * 获取指定连接池
//	 * 
//	 * @author Darkness
//	 * @date 2012-10-1 上午10:43:29
//	 * @version V1.0
//	 */
//	public static ConnectionPool getPool(String poolName) {
//		return getPoolMap().get(poolName);
//	}
//
//	/**
//	 * 检测连接池是否存在
//	 * 
//	 * @author Darkness
//	 * @date 2012-10-1 上午10:43:04
//	 * @version V1.0
//	 */
//	public static boolean isPoolExist(String poolName) {
//		return getPool(poolName) != null;
//	}
//
//	/**
//	 * 删除指定连接池
//	 * 
//	 * @author Darkness
//	 * @date 2012-10-1 上午10:47:55
//	 * @version V1.0
//	 */
//	public static void removePool(String poolName) {
//		getPoolMap().remove(poolName);
//	}
//
//	/**
//	 * 添加连接池
//	 * 
//	 * @author Darkness
//	 * @date 2012-10-1 上午10:49:12
//	 * @version V1.0
//	 */
//	public static void setPool(String poolName, ConnectionPool pool) {
//		getPoolMap().put(poolName, pool);
//	}
//	//[end]
//	
//}