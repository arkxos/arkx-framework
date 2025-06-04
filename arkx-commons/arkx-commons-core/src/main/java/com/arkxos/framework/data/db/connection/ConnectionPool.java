package com.arkxos.framework.data.db.connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.arkxos.framework.Config;
import io.arkx.framework.commons.collection.ConcurrentMapx;
import io.arkx.framework.commons.util.LogUtil;
import com.arkxos.framework.data.db.dbtype.DBTypeService;
import com.arkxos.framework.data.db.dbtype.IDBType;
import com.arkxos.framework.data.db.exception.DatabaseException;
import com.arkxos.framework.data.jdbc.JdbcTemplate;

/**
 * 数据库连接池
 * 
 */
public class ConnectionPool {
	private static ConcurrentMapx<String, String> jndiMap = new ConcurrentMapx<>(1000);
	private ConnectionConfig dcc;
	private ReentrantLock lock = new ReentrantLock();
	protected Connection[] conns;

	/**
	 * 构造器
	 * 
	 * @param dcc 连接池配置信息
	 */
	public ConnectionPool(ConnectionConfig dcc) {
		this.dcc = dcc;
		if (ConnectionPoolManager.getPool(dcc.PoolName) != null) {
			throw new DatabaseException("DB Connection Pool is exist:" + dcc.PoolName);
		}
		fillInitConn();
	}

	/**
	 * 按InitConnCount指定的数量创建连接
	 */
	private void fillInitConn() {
		if (!dcc.isJNDIPool) {
			conns = new Connection[dcc.MaxConnCount];
			try {
				for (int i = 0; i < dcc.InitConnCount; i++) {
					conns[i] = createConnection(dcc, false);
					conns[i].isUsing = false;
				}
				dcc.ConnCount = dcc.InitConnCount;
				LogUtil.info("----" + dcc.PoolName + " init " + dcc.InitConnCount + " connection");
			} catch (Exception e) {
				LogUtil.warn("----" + dcc.PoolName + "init Connections failed");
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return 所有连接
	 */
	public Connection[] getDBConns() {
		return conns;
	}

	/**
	 * 关闭连接池中的所有连接并清空
	 */
	public void clear() {
		if (conns == null) {
			return;
		}
		for (int i = 0; i < conns.length; i++) {
			if (conns[i] != null) {
				try {
					conns[i].conn.close();
					conns[i] = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		dcc.ConnCount = 0;
	}

	/**
	 * @return 连接池对应的数据库类型
	 */
	public String getDBType() {
		return dcc.DBType;
	}

	/**
	 * @return 连接池配置信息
	 */
	public ConnectionConfig getConfig() {
		return dcc;
	}

	/**
	 * @param isLongTimeOperation 是否将连接标记为长时间占用
	 * @return 返回连接池中的一个连接，如果连接池中的连接都被占用，则新创建一个连接。
	 */
	public Connection getConnection(boolean isLongTimeOperation) {
		if (dcc.isJNDIPool) {
			return getJNDIPoolConnection(dcc);
		}
		long now = System.currentTimeMillis();
		Connection conn = null;
		lock.lock();
		try {
			for (int i = 0; i < dcc.ConnCount; i++) {
				conn = conns[i];
				if (conn == null) {
					continue;
				}
				if (conn.isUsing) {
					if (!conn.longTimeFlag) {
						if (now - conn.lastSuccessExecuteTime > dcc.MaxConnUsingTime) {
							LogUtil.error(dcc.PoolName
									+ ":connection timeout,will close connection automatical,there is last sql and invoke stack:");
							LogUtil.error("Last SQL:" + conn.lastSQL);
							LogUtil.warn(getCallerStack(conn));
							JdbcTemplate.log(conn.lastSuccessExecuteTime, "Timeout:" + conn.lastSQL, null);
							final Connection conn2 = conn;
							new Thread() {// 另开线程关闭，以免close()操作阻塞其他线程
								@Override
								public void run() {
									try {
										if (!conn2.conn.getAutoCommit()) {
											conn2.conn.rollback();
										}
									} catch (SQLException e) {
										e.printStackTrace();
									} finally {
										try {
											conn2.conn.close();// 先关闭，再创建新的
										} catch (SQLException e) {
											e.printStackTrace();
										}
									}
								}
							}.start();
							try {
								conn = createConnection(dcc, isLongTimeOperation);
								conns[i] = conn;
								LogUtil.info(dcc.PoolName + ":create a new connection,total is " + dcc.ConnCount + " line 215");
								setCaller(conn);
								conn.lastSuccessExecuteTime = now;
								return conn;
							} catch (Exception e) {
								e.printStackTrace();
								throw new DatabaseException("ConnectionPool," + dcc.PoolName + "create new connection failed:"
										+ e.getMessage());
							}
						}
					} else if (now - conn.lastSuccessExecuteTime > 4 * dcc.MaxConnUsingTime && now - conn.lastWarnTime > 300000) {
						LogUtil.warn(dcc.PoolName + ":connection used " + (now - conn.lastSuccessExecuteTime)
								+ " ms,there is invoke stack:");
						LogUtil.warn(getCallerStack(conn));
						conn.lastWarnTime = now;
					}
				} else if (!conn.isBlockingTransactionStarted) {// 阻塞型不可用
					conn.longTimeFlag = isLongTimeOperation;
					conn.isUsing = true;
					setCaller(conn);
					// 检查连接是否己失效，若己失效则重新连接
					keepAlive(conn);
					return conn;
				}
			}
			if (dcc.ConnCount < dcc.MaxConnCount) {
				try {
					conn = createConnection(dcc, isLongTimeOperation);
//					System.out.println("调试连接池创建泄露");
					LogUtil.warn(getCallerStack(conn));
					conns[dcc.ConnCount] = conn;
					dcc.ConnCount++;
					LogUtil.info(dcc.PoolName + ":create a new connection,total is " + dcc.ConnCount + " line 246");
					setCaller(conn);
					conn.lastSuccessExecuteTime = now;
					return conn;
				} catch (Exception e) {
					e.printStackTrace();
					throw new DatabaseException("ConnectionPool," + dcc.PoolName + ":create new connection failed:" + e.getMessage());
				}
			} else {
				throw new DatabaseException("ConnectionPool," + dcc.PoolName + ":all connection is using!");
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 保持一个连接存活，避免长时间不操作被数据库服务器自动关闭
	 * 
	 * @param conn
	 */
	private void keepAlive(Connection conn) {
		if (conn == null) {
			return;
		}
		if (System.currentTimeMillis() - conn.getLastSuccessExecuteTime() > dcc.KeepAliveInterval) {
			PreparedStatement stmt = null;
			String sql = "select 1 from " + dcc.TestTable + " where 1=2";
			ResultSet rs = null;
			try {
				stmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				rs = stmt.executeQuery();
			} catch (SQLException e) {
				try {
					conn.conn.close();
				} catch (SQLException e1) {
				}
				try {
					conn.conn = createConnection(dcc, false).conn;
				} catch (Exception e1) {
					LogUtil.error(dcc.PoolName + ":Reconnection is failed:" + e1.getMessage());
				}
			} finally {
				try {
					if (rs != null) {
						rs.close();
						rs = null;
					}
					if (stmt != null) {
						stmt.close();
						stmt = null;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 保持池中所有连接存活，避免长时间不操作被数据库服务器自动关闭
	 */
	public void keepAlive() {
		lock.lock();
		try {
			int count = 0;
			if (conns == null) {
				return;
			}
			for (int i = 0; i < conns.length; i++) {
				Connection conn = conns[i];
				if (conn == null || conn.isUsing) {
					continue;
				}
				if (count < dcc.InitConnCount) {
					count++;
					keepAlive(conn);
					conn.lastSuccessExecuteTime = System.currentTimeMillis();
				} else {// 如果空闲的连接数超过InitConnCount(默认是5个)，则关掉
					conns[i] = null;
					dcc.ConnCount--;
					try {
						conn.conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @param dcc 连接池配置信息
	 * @return 根据连接池配置信息返回一个JNDI连接
	 */
	private static Connection getJNDIPoolConnection(ConnectionConfig dcc) {
		int connID = Connection.getConnID();
		try {
			Context ctx = new InitialContext();
			java.sql.Connection conn = null;
			if (Config.isTomcat()) {
				ctx = (Context) ctx.lookup("java:comp/env");
				DataSource ds = (DataSource) ctx.lookup(dcc.JNDIName);
				conn = ds.getConnection();
			} else if (Config.isJboss()) {
				Hashtable<String, String> environment = new Hashtable<String, String>();
				environment.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
				environment.put(Context.URL_PKG_PREFIXES, "org.jboss.naming.client ");
				environment.put(Context.PROVIDER_URL, "jnp://127.0.0.1:1099");
				ctx = new InitialContext(environment);
				DataSource ds = (DataSource) ctx.lookup("java:" + dcc.JNDIName);
				conn = ds.getConnection();
			} else {
				DataSource ds = (DataSource) ctx.lookup(dcc.JNDIName);
				conn = ds.getConnection();
			}
			IDBType t = DBTypeService.getInstance().get(dcc.DBType);
			Connection dbconn = new Connection();
			dbconn.conn = conn;
			dbconn.setDbConfig(dcc);
			dbconn.connID = connID;
			if (!jndiMap.containsKey(conn.toString())) {
				t.afterConnectionCreate(dbconn);
				jndiMap.put(conn.toString(), "");
			}
			return dbconn;
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.warn("Find JNDI connection pool failed:" + e.getMessage());
			Connection.removeConnID(connID);
		}
		return null;
	}

	/**
	 * @param dcc 连接池配置信息
	 * @param isLongTimeOperation 是否是长时间操作
	 * @return 创建好的JDBC连接
	 * @throws Exception
	 */
	public static Connection createConnection(ConnectionConfig dcc, boolean isLongTimeOperation) throws Exception {
		java.sql.Connection conn = null;
		if (dcc.isJNDIPool) {
			return getJNDIPoolConnection(dcc);
		} else {
			IDBType t = DBTypeService.getInstance().get(dcc.DBType);
			if (t == null) {
				LogUtil.error("Database type is not supported:" + dcc.DBType);
			}
			conn = t.createConnection(dcc);
		}
		Connection dbconn = new Connection();
		dbconn.conn = conn;
		dbconn.isUsing = true;
		dbconn.longTimeFlag = isLongTimeOperation;
		dbconn.setDbConfig(dcc);
		dbconn.connID = Connection.getConnID();
		return dbconn;
	}

	/**
	 * 设置连接的调用堆栈
	 */
	private void setCaller(Connection conn) {
		conn.callerStackTrace = new Throwable().getStackTrace();
	}

	/**
	 * 获取连接的调用堆栈信息
	 */
	private String getCallerStack(Connection conn) {
		StackTraceElement[] trace = conn.callerStackTrace;
		StringBuilder sb = new StringBuilder();
		if (trace != null) {
			for (StackTraceElement element : trace) {
				sb.append("\tat " + element);
			}
		}
		return sb.toString();
	}

	/**
	 * 请使用DBConnPoolManager.getConnection()替代
	 */
	@Deprecated
	public static Connection getConnection() {
		return ConnectionPoolManager.getConnection();
	}

	/**
	 * 请使用DBConnPoolManager.getConnection(poolName)替代
	 */
	@Deprecated
	public static Connection getConnection(String poolName) {
		return ConnectionPoolManager.getConnection(poolName);
	}

	/**
	 * 请使用DBConnPoolManager.getConnection(poolName,isLongTimeOperation,isCurrentThreadConnection)替代
	 */
	@Deprecated
	public static Connection getConnection(String poolName, boolean isLongTimeOperation, boolean isCurrentThreadConnection) {
		return ConnectionPoolManager.getConnection(poolName, isLongTimeOperation, isCurrentThreadConnection);
	}

	/**
	 * 请使用DBConnPoolManager.getDBConnConfig()替代
	 */
	@Deprecated
	public static ConnectionConfig getDBConnConfig() {
		return ConnectionPoolManager.getDBConnConfig();
	}

	/**
	 * 请使用DBConnPoolManager.getDBConnConfig(poolName)替代
	 */
	@Deprecated
	public static ConnectionConfig getDBConnConfig(String poolName) {
		return ConnectionPoolManager.getDBConnConfig(poolName);
	}

}
