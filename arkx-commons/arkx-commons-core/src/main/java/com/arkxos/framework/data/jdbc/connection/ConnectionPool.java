package com.arkxos.framework.data.jdbc.connection;
//package org.ark.framework.persistence.jdbc.connection;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.Hashtable;
//
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.sql.DataSource;
//
//import org.apache.log4j.Logger;
//import org.ark.framework.Config;
//import org.ark.framework.orm.db.DatabaseService;
//import org.ark.framework.orm.db.IDatabaseType;
//import org.ark.framework.orm.query.QueryBuilder;
//import org.ark.framework.orm.sql.DataAccess;
//import org.ark.framework.utility.ObjectUtil;
//import util.io.arkx.framework.commons.StringUtil;
//
//import com.arkxos.framework.framework.collection.Mapx;
//import com.arkxos.framework.framework.utility.LogUtil;
//
//
///**
// * @class org.ark.framework.orm.connection.XConnectionPool
// * Create on May 14, 2010 11:54:58 AM
// * 
// * @author Darkness
// * @version 1.0
// */
//public class ConnectionPool {
//
//	private static Logger logger = Logger.getLogger(ConnectionPool.class);
//	
//	private ConnectionConfig dcc;
//	protected Connection[] conns;
//
//	public ConnectionPool(String poolName) {
//		this.dcc = new ConnectionConfig();
//		this.dcc.PoolName = poolName;
////		dcc = DBConfig.getDatabase(poolName);
////		init();
//	}
//
//	/**
//	 * 根据连接配置初始化连接池
//	 * 
//	 * @param config
//	 */
//	public ConnectionPool(ConnectionConfig config) {
//		this.dcc = config;
//		if (ConnectionPoolManager.isPoolExist(this.dcc.PoolName + ".")) {
//			throw new RuntimeException("DB Connection Pool is exist:" + this.dcc.PoolName);
//		}
//		ConnectionPoolManager.setPool(this.dcc.PoolName + ".", this);
//		fillInitConn();
//	}
//
//	// [start] 初始化连接池
//	public synchronized void init() {
//		if (this.dcc.getDatabaseType() != null) {
//			return;
//		}
//		init(this.dcc, Config.getMapx());
//		fillInitConn();
//	}
//
//	private void fillInitConn() {
//
//		if (this.dcc.isJNDIPool) {
//			return;
//		}
//
//		this.conns = new Connection[this.dcc.MaxConnCount];
//		try {
//			for (int i = 0; i < this.dcc.InitConnCount; i++) {
//				this.conns[i] = createConnection(this.dcc, false);
//				this.conns[i].isUsing = false;
//			}
//			this.dcc.ConnCount = this.dcc.InitConnCount;
//			logger.info("----" + this.dcc.PoolName + " init " + this.dcc.InitConnCount + " connection");
//		} catch (Exception e) {
//			LogUtil.warn("----" + this.dcc.PoolName + "init Connections failed");
//			e.printStackTrace();
//		}
//	}// [end]
//
//	public Connection[] getDBConns() {
//		return this.conns;
//	}
//
//	/**
//	 * 清除连接池中的所有连接
//	 */
//	public void clear() {
//		if (this.conns == null) {
//			return;
//		}
//		for (int i = 0; i < this.conns.length; i++) {
//			if (this.conns[i] == null)
//				continue;
//			try {
//				this.conns[i].connection.close();
//				this.conns[i] = null;
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//
//		this.dcc.ConnCount = 0;
//	}
//
//	public IDatabaseType getDBType() {
//		return DatabaseService.valueOf(this.dcc.getDatabaseType());
//	}
//
//	/**
//	 * 获取连接配置
//	 */
//	public ConnectionConfig getDBConnConfig() {
//		if (this.dcc.getDatabaseType() == null) {
//			init();
//		}
//		return this.dcc;
//	}
//
//	// [start] 初始化连接配置
////	public void init() {
////		init(this.dcc, Config.getMapx());
////		fillInitConn();
////	}
//	/**
//	 * 初始化连接配置
//	 * 
//	 * @author Darkness
//	 * @date 2012-10-6 下午5:27:30
//	 * @version V1.0
//	 */
//	public static void init(ConnectionConfig dcc, Mapx<String, String> map) {
//		if (dcc.getDatabaseType() != null) {
//			return;
//		}
//		dcc.setDatabaseType(map.getString("Database." + dcc.PoolName + "Type"));
//		dcc.JNDIName = map.getString("Database." + dcc.PoolName + "JNDIName");
//		dcc.setLatin1Charset("true".equalsIgnoreCase(map.getString("Database." + dcc.PoolName + "isLatin1Charset")));
//		if (StringUtil.isNotEmpty(dcc.JNDIName)) {
//			dcc.isJNDIPool = true;
//		} else {
//			dcc.setHost(map.getString("Database." + dcc.PoolName + "ServerAddress"));
//			dcc.ConnectionURL = map.getString("Database." + dcc.PoolName + "ConnectionURL");
//			dcc.setDatabaseName(map.getString("Database." + dcc.PoolName + "Name"));
//			dcc.setUserName(map.getString("Database." + dcc.PoolName + "UserName"));
//			dcc.setPassword(map.getString("Database." + dcc.PoolName + "Password"));
//			dcc.setTestTable(map.getString("Database." + dcc.PoolName + "TestTable"));
//
//			dcc.validate();
//
//			dcc.setInitConnCount(map.getString("Database." + dcc.PoolName + "InitConnCount"));
//			dcc.setMaxConnCount(map.getString("Database." + dcc.PoolName + "MaxConnCount"));
//			dcc.setPort(map.getString("Database." + dcc.PoolName + "Port"));
//		}
//		if (dcc.InitConnCount < 5) {
//			dcc.InitConnCount = 5;
//		}
//	}// [end]
//
//	// [start] 获取连接
//	/**
//	 * 获取连接，非长连接，默认超时时间为 2分钟
//	 */
//	public Connection getConnection() {
//		return getConnection(false);
//	}
//
//	/**
//	 * 获取连接
//	 * 
//	 * @param bLongTimeFlag
//	 *            是否为长连接
//	 * @return
//	 */
//	public Connection getConnection(boolean bLongTimeFlag) {
//
//		if (this.dcc.getDatabaseType() == null) {
//			init();
//		}
//
//		if (this.dcc.isJNDIPool) {
//			return getJNDIPoolConnection(this.dcc);
//		}
//
//		long now = System.currentTimeMillis();
//		Connection conn = null;
//		synchronized (this) {
//				
//			Connection searchedConn = searchNotUsingConn(bLongTimeFlag);
//			if(searchedConn != null) {
//				return searchedConn;
//			}
//			if (this.dcc.ConnCount < this.dcc.MaxConnCount) {
//				try {
//					conn = createConnection(this.dcc, bLongTimeFlag);
//					this.conns[this.dcc.ConnCount] = conn;
//					this.dcc.ConnCount += 1;
//					LogUtil.info(this.dcc.PoolName + ":create a new connection,total is " + this.dcc.ConnCount);
//					setCaller(conn);
//					conn.LastSuccessExecuteTime = now;
//					return conn;
//				} catch (Exception e) {
//					throw new RuntimeException("DBConnPoolImpl," + this.dcc.PoolName + ":create new connection failed:" + e.getMessage());
//				}
//			}
//			throw new RuntimeException("DBConnPoolImpl," + this.dcc.PoolName + ":all connection is using!");
//		}
//	}// [end]
//	private Connection searchNotUsingConn(boolean bLongTimeFlag) {
//		Connection conn = null;
//		long now = System.currentTimeMillis();
//		for (int i = 0; i < this.dcc.ConnCount; i++) {
//			conn = this.conns[i];
//			if (conn == null) {
//				continue;
//			}
//			try {
//				if(conn.connection == null || conn.isClosed()) {
//					keepAlive(conn);
//				}
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//			if (conn.isUsing) {
//				if (!conn.LongTimeFlag) {
//					if (!conn.isExcuteTooLong())
//						continue;
//					conn.commitAndClose();
//					
//					LogUtil.error(this.dcc.PoolName + ":connection timeout,will close connection automatical,there is last sql and invoke stack:");
//					LogUtil.error("Last SQL:" + conn.LastSQL);
//					LogUtil.error(conn.CallerString);
//					DataAccess.log(conn.LastSuccessExecuteTime, "Timeout:" + conn.LastSQL + "\n" + conn.CallerString, null);
//					
//					try {
//						conn = createConnection(this.dcc, bLongTimeFlag);
//						this.conns[i] = conn;
//						LogUtil.info(this.dcc.PoolName + ":create a new connection,total is " + this.dcc.ConnCount);
//						setCaller(conn);
//						conn.LastSuccessExecuteTime = now;
//						return conn;
//					} catch (Exception e) {
//						e.printStackTrace();
//						throw new RuntimeException("DBConnPoolImpl," + this.dcc.PoolName + "create new connection failed:" + e.getMessage());
//					}
//				}
//				if ((now - conn.LastSuccessExecuteTime > 4 * this.dcc.MaxConnUsingTime) && (now - conn.LastWarnTime > 300000L)) {
//					LogUtil.warn(this.dcc.PoolName + ":connection used " + (now - conn.LastSuccessExecuteTime) + " ms,there is invoke stack:");
//					logger.warn(conn.CallerString);
//					conn.LastWarnTime = now;
//				}
//			} else if (!conn.isBlockingTransactionStarted) {
//				conn.LongTimeFlag = bLongTimeFlag;
//				conn.isUsing = true;
//				conn.LastApplyTime = now;
//				setCaller(conn);
//				keepAlive(conn);
//				conn.LastSuccessExecuteTime = now;
//				return conn;
//			}
//		}
//		return null;
//	}
//	// [start] 保持连接有效
//	private void keepAlive(Connection conn) {
//		if (conn == null) {
//			return;
//		}
//		
//		try {
//			if(conn.isClosed()) {
//				try {
//					conn.close();
//					conn.connection.close();
//				} catch (Exception localSQLException) {
//				}
//				try {
//					conn.connection = createConnection(this.dcc, false).connection;
//				} catch (Exception e1) {
//					LogUtil.error(this.dcc.PoolName + ":Reconnection is failed:" + e1.getMessage());
//				}
//			}
//		} catch (SQLException e2) {
//			e2.printStackTrace();
//		}
//		if (System.currentTimeMillis() - conn.getLastSuccessExecuteTime() > this.dcc.RefershPeriod) {
//			try {
//				QueryBuilder qb = new QueryBuilder("select 1 from " + this.dcc.getTestTable() + " where 1=2", new Object[0]);
//				qb.setConnection(conn);
//				qb.executeOneValue();
//			} catch (Exception e) {
//				try {
//					conn.close();
//					conn.connection.close();
//				} catch (Exception localSQLException) {
//				}
//				try {
//					conn.connection = createConnection(this.dcc, false).connection;
//				} catch (Exception e1) {
//					LogUtil.error(this.dcc.PoolName + ":Reconnection is failed:" + e1.getMessage());
//				}
//			}
//		}
//	}
//
//	public void keepAlive() {
//		synchronized (this) {
//			int count = 0;
//			for (int i = 0; i < this.conns.length; i++) {
//				Connection conn = this.conns[i];
//				if ((conn == null) || (conn.isUsing)) {
//					continue;
//				}
//				if (count < this.dcc.InitConnCount) {
//					count++;
//					keepAlive(conn);
//					conn.LastSuccessExecuteTime = System.currentTimeMillis();
//				} else {
//					this.conns[i] = null;
//					this.dcc.ConnCount -= 1;
//					try {
//						conn.connection.close();
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//	}// [end]
//
//	// [start] 获取JNDI连接
//	public static Connection getJNDIPoolConnection(ConnectionConfig dbcc) {
//		int connID = Connection.getConnID();
//		try {
//			Context ctx = new InitialContext();
//			java.sql.Connection conn = null;
//			if (Config.isTomcat()) {
//				ctx = (Context) ctx.lookup("java:comp/env");
//				DataSource ds = (DataSource) ctx.lookup(dbcc.JNDIName);
//				conn = ds.getConnection();
//			} else if (Config.isJboss()) {
//				Hashtable<String, String> environment = new Hashtable<String, String>();
//				environment.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
//				environment.put("java.naming.factory.url.pkgs", "org.jboss.naming.client ");
//				environment.put("java.naming.provider.url", "jnp://127.0.0.1:1099");
//				ctx = new InitialContext(environment);
//				DataSource ds = (DataSource) ctx.lookup("java:" + dbcc.JNDIName);
//				conn = ds.getConnection();
//			} else {
//				DataSource ds = (DataSource) ctx.lookup(dbcc.JNDIName);
//				conn = ds.getConnection();
//			}
//			
////			dbcc.getDatabaseType().onGetJNDIPoolConnection(conn, connID);
//			if (DatabaseService.isOracle(dbcc.getDatabaseType())) {
//				Statement stmt = conn.createStatement(1005, 1008);
//				stmt.execute("alter session set nls_date_format = 'YYYY-MM-DD HH24:MI:SS'");
//				stmt.close();
//			} else if (DatabaseService.isMysql(dbcc.getDatabaseType())) {
//				Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
//				String charset = dbcc.getCharset();
//				if (ObjectUtil.empty(charset)) {
//					charset = Config.getGlobalCharset();
//				}
//				stmt.execute("SET NAMES '" + charset.replaceAll("\\-", "").toLowerCase() + "'");
//				stmt.close();
//			} else if (DatabaseService.isSybase(dbcc.getDatabaseType())) {
//				Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
//				stmt.execute("set textsize 20971520");
//				stmt.execute("drop table #tmp_z_" + connID);
//				stmt.close();
//			}
//			Connection dbconn = new Connection();
//			dbconn.connection = conn;
//			dbconn.DBConfig = dbcc;
//			dbconn.ConnID = connID;
//			return dbconn;
//		} catch (Exception e) {
//			e.printStackTrace();
//			LogUtil.warn("Find JNDI connection pool failed:" + e.getMessage());
//			Connection.removeConnID(connID);
//		}
//		return null;
//	}// [end]
//
//	// [start] 创建连接
//	public static Connection createConnection(ConnectionConfig dbcc, boolean bLongTimeFlag) throws Exception {
//
//		if (dbcc.isJNDIPool) {
//			return getJNDIPoolConnection(dbcc);
//		}
//
//		Connection dbconn = new Connection();
//		dbconn.connection = dbcc.createConnection();
//		dbconn.isUsing = true;
//		dbconn.LongTimeFlag = true;//bLongTimeFlag;
//		dbconn.LastApplyTime = System.currentTimeMillis();
//		dbconn.DBConfig = dbcc;
//		dbconn.ConnID = Connection.getConnID();
//		return dbconn;
//	}// [end]
//
//	// [start] 设置连接调用堆栈
//	private void setCaller(Connection conn) {
//		StackTraceElement[] stack = new Throwable().getStackTrace();
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < stack.length; i++) {
//			StackTraceElement ste = stack[i];
//			if (ste.getClassName().indexOf("DBConnPoolImpl") == -1) {
//				sb.append("\t");
//				sb.append(ste.getClassName());
//				sb.append(".");
//				sb.append(ste.getMethodName());
//				sb.append("(),行号:");
//				sb.append(ste.getLineNumber());
//				sb.append("\n");
//			}
//		}
//		conn.CallerString = sb.toString();
//	}// [end]
//
//}