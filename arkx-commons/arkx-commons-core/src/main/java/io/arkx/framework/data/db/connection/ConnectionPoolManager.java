package io.arkx.framework.data.db.connection;

import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

import io.arkx.framework.Config;
import io.arkx.framework.commons.collection.ConcurrentMapx;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.data.db.dbtype.DBTypeService;
import io.arkx.framework.data.db.dbtype.IDBType;
import io.arkx.framework.data.db.exception.DatabaseException;
import io.arkx.framework.data.jdbc.TransactionFactory;

/**
 * 连接池管理类,同时管理多个连接池 Create on May 14, 2010 11:41:46 AM
 *
 * @author Darkness
 * @version 1.0
 */
public class ConnectionPoolManager {

    public static final String DEFAULT_POOLNAME = "Default";

    private static ConcurrentMapx<String, ConnectionPool> poolMap = new ConcurrentMapx<>();

    private static ReentrantLock lock = new ReentrantLock();

    private static ThreadLocal<String> threadCurrentPool;

    public static Connection getConnection() {
        return getConnection(null, false);
    }

    public static Connection getConnection(boolean bLongTimeFlag) {// NO_UCD
        return getConnection(null, bLongTimeFlag);
    }

    public static Connection getConnection(String poolName) {
        return getConnection(poolName, false);
    }

    /**
     * @return 默认连接池的配置信息
     */
    public static ConnectionConfig getDBConnConfig() {
        return getDBConnConfig(null);
    }

    /**
     * @param poolName
     *            连接池名称
     * @return 连接池名称对应的连接池，如果找不到则返回null
     */
    public static ConnectionConfig getDBConnConfig(String poolName) {
        if (StringUtil.isNull(poolName)) {
            if (threadCurrentPool != null) {
                poolName = threadCurrentPool.get();
            }
            if (StringUtil.isNull(poolName)) {
                poolName = DEFAULT_POOLNAME;
            }
        }
        ConnectionPool pool = poolMap.get(poolName);
        if (pool == null) {
            lock.lock();
            try {
                pool = poolMap.get(poolName);
                if (pool == null) {
                    if (Config.getValue("Database." + poolName + ".Type") != null) {
                        pool = new ConnectionPool(createConnConfig(poolName, Config.getMapx()));
                        poolMap.put(poolName, pool);
                    } else {
                        throw new RuntimeException("DB Connection Pool not found:" + poolName);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        return pool.getConfig();
    }

    /**
     * @param poolName
     *            连接池名称
     * @param isLongTimeOperation
     *            是否是长时操作
     * @return 指定连接池中的JDBC连接
     */
    public static Connection getConnection(String poolName, boolean isLongTimeOperation) {
        return getConnection(poolName, isLongTimeOperation, true);
    }

    /**
     * @param poolName
     *            连接池名称
     * @param isLongTimeOperation
     *            是否是长时操作
     * @param isCurrentThreadConnection
     *            是否使用当前线程中的连接，如果为true且当前线程中有阻塞形连接，则使用当前线程中的连接
     * @return 指定连接池中的JDBC连接
     */
    public static Connection getConnection(String poolName, boolean isLongTimeOperation,
            boolean isCurrentThreadConnection) {
        if (StringUtil.isNull(poolName)) {
            if (threadCurrentPool != null) {
                poolName = threadCurrentPool.get();
            }
            if (StringUtil.isNull(poolName)) {
                poolName = DEFAULT_POOLNAME;
            }
        }
        if (isCurrentThreadConnection) {
            if (TransactionFactory.getCurrentTransaction() != null) {
                Connection conn = TransactionFactory.getCurrentTransaction().getConnection();
                if (conn != null && conn.getDBConfig().PoolName.equals(poolName)) {
                    return conn;// 如果存在阻塞型事务，并且其中的连接的连接池名和当前申请的连接池名称一致，则直接返回该连接，以保证整个处理过程中能够查询到正确的数据。
                }
            }
        }
        ConnectionPool pool = poolMap.get(poolName);
        if (pool == null) {
            lock.lock();
            try {
                pool = poolMap.get(poolName);
                if (pool == null) {
                    if (Config.getValue("Database." + poolName + ".Type") != null) {
                        pool = new ConnectionPool(createConnConfig(poolName, Config.getMapx()));
                        poolMap.put(poolName, pool);
                    } else {
                        throw new RuntimeException("DB Connection Pool not found:" + poolName);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        return pool.getConnection(isLongTimeOperation);
    }

    /**
     * @param poolName
     *            连接池名称
     * @return 从map读取连接池配置信息
     */
    public static ConnectionConfig createConnConfig(String poolName, Mapx<String, String> map) {
        ConnectionConfig dcc = new ConnectionConfig();
        dcc.PoolName = poolName;
        dcc.DBType = map.get("Database." + dcc.PoolName + ".Type");
        dcc.JNDIName = map.get("Database." + dcc.PoolName + ".JNDIName");
        dcc.isLatin1Charset = "true".equalsIgnoreCase(map.get("Database." + dcc.PoolName + ".isLatin1Charset"));
        if (StringUtil.isNotEmpty(dcc.JNDIName)) {
            dcc.isJNDIPool = true;
        } else {
            dcc.DBServerAddress = map.get("Database." + dcc.PoolName + ".ServerAddress");
            dcc.ConnectionURL = map.get("Database." + dcc.PoolName + ".ConnectionURL");
            dcc.DriverClass = map.get("Database." + dcc.PoolName + ".DriverClass");
            dcc.DBName = map.get("Database." + dcc.PoolName + ".Name");
            dcc.DBUserName = map.get("Database." + dcc.PoolName + ".UserName");
            dcc.DBPassword = map.get("Database." + dcc.PoolName + ".Password");
            dcc.TestTable = map.get("Database." + dcc.PoolName + ".TestTable");
            if (StringUtil.isEmpty(dcc.DBType)) {
                throw new DatabaseException("DB.Type not found");
            }
            if (StringUtil.isEmpty(dcc.ConnectionURL)) {
                if (StringUtil.isEmpty(dcc.DBServerAddress)) {
                    throw new DatabaseException("DB.ServerAddress not found");
                }
                if (StringUtil.isEmpty(dcc.DBName)) {
                    throw new DatabaseException("DB.Name not found");
                }
                if (StringUtil.isEmpty(dcc.DBUserName)) {
                    throw new DatabaseException("DB.UserName not found");
                }
                if (dcc.DBPassword == null) {// 可能为空
                    throw new DatabaseException("DB.Password not found");
                }
            } else {
                dcc.ConnectionURL = dcc.ConnectionURL.trim();
            }
            String s = map.get("Database." + dcc.PoolName + ".InitConnCount");
            try {
                dcc.InitConnCount = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                dcc.InitConnCount = 0;
                LogUtil.warn(s + " is invalid DB.InitConnCount value,will use 0");
            }
            s = map.get("Database." + dcc.PoolName + ".MaxConnCount");
            try {
                dcc.MaxConnCount = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                dcc.MaxConnCount = 20;
                LogUtil.warn(s + " is invalid DB.MaxConnCount value,will use 20");
            }
            s = map.get("Database." + dcc.PoolName + ".Port");
            if (StringUtil.isNotEmpty(s)) {
                try {
                    dcc.DBPort = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    IDBType t = DBTypeService.getInstance().get(dcc.DBType);
                    if (t == null) {
                        throw new DatabaseException("Unknow DB Type:" + dcc.DBType);
                    }
                    dcc.DBPort = t.getDefaultPort();
                    LogUtil.warn(s + " is invalid DB.Port value,will use default value");
                }
            }
        }
        if (dcc.InitConnCount < 1) {
            dcc.InitConnCount = 1;
        }
        return dcc;
    }

    /**
     * 添加一个数据库连接池
     *
     * @param dcc
     *            数据库连接池配置
     */
    public static void addPool(ConnectionConfig dcc) {
        poolMap.put(dcc.PoolName, new ConnectionPool(dcc));
    }

    /**
     * @param poolName
     *            连接池名称
     * @return 对应的连接池实例，如果未找到则返回null
     */
    public static ConnectionPool getPool(String poolName) {
        return poolMap.get(poolName);
    }

    public static Collection<ConnectionPool> pools() {
        return poolMap.values();
    }

    public static boolean isEmpty() {
        return poolMap == null;
    }

    /**
     * @param poolName
     *            连接池名称
     * @return 删除掉的连接池实例，如果没有对应的连接池，则返回null
     */
    public static ConnectionPool removePool(String poolName) {
        return poolMap.remove(poolName);
    }

    /**
     * 将当前线程中的所有的数据库操作的目标设置到指定的连接池
     *
     * @param poolName
     *            连接池名称
     */
    public static void setThreadCurrentPool(String poolName) {// NO_UCD
        if (poolName == null) {
            return;
        }
        if (threadCurrentPool == null) {
            lock.lock();
            try {
                if (threadCurrentPool == null) {
                    threadCurrentPool = new ThreadLocal<>();
                }
            } finally {
                lock.unlock();
            }
        }
        threadCurrentPool.set(poolName);
    }

    /**
     * 销毁所有连接池，关闭所有连接。
     */
    public static void destory() {
        for (ConnectionPool pool : poolMap.values()) {
            pool.clear();
        }
        poolMap.clear();
        poolMap = null;
    }

}
