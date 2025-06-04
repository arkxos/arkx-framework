package io.arkx.framework.data.db.connection;

import io.arkx.framework.data.db.dbtype.Db2;
import io.arkx.framework.data.db.dbtype.DerbyEmbedded;
import io.arkx.framework.data.db.dbtype.DerbyServer;
import io.arkx.framework.data.db.dbtype.HsqlDb;
import io.arkx.framework.data.db.dbtype.MsSql;
import io.arkx.framework.data.db.dbtype.MsSql2000;
import io.arkx.framework.data.db.dbtype.MySql;
import io.arkx.framework.data.db.dbtype.Oracle;
import io.arkx.framework.data.db.dbtype.Sybase;

/**
 * 数据库连接池配置信息类
 * 
 */
public class ConnectionConfig {

	public static final String ORACLE = Oracle.ID;

	public static final String DB2 = Db2.ID;

	public static final String MYSQL = MySql.ID;

	public static final String MSSQL = MsSql.ID;

	public static final String MSSQL2000 = MsSql2000.ID;

	public static final String SYBASE = Sybase.ID;

	public static final String HSQLDB = HsqlDb.ID;

	public static final String DERBY_EMBEDDED = DerbyEmbedded.ID;

	public static final String DERBY_SERVER = DerbyServer.ID;

	/**
	 * JDBC驱动类名称
	 */
	public String DriverClass;

	/**
	 * JDNI名称
	 */
	public String JNDIName = null;

	/**
	 * 是否是一个JNDI池
	 */
	public boolean isJNDIPool = false;

	/**
	 * 最大连接数量，默认为1000
	 */
	public int MaxConnCount = 1000;

	/**
	 * 初始化时创建的连接数量，默认为5
	 */
	public int InitConnCount = 5;

	/**
	 * 己创建的连接数量
	 */
	public int ConnCount;

	/**
	 * 连接最长使用时间（单位为毫秒），如果连接不是长时连接，则超过此时间会抛出异常
	 */
	public int MaxConnUsingTime = 5 * 60 * 1000;// 以毫秒为单位

	/**
	 * 保持活动间隔（单位为毫秒），如果连接池中的连接超过此间隔，则会发一次测试语句给数据库以免数据库服务器将此连接自动关闭。
	 */
	//RefershPeriod
	public int KeepAliveInterval = 30_000;// 一分钟检查一次连接是否己失效（数据库重启等原因造成）

	/**
	 * 数据库类型
	 */
	public String DBType;//databaseType

	/**
	 * 数据库服务器地址
	 */
	public String DBServerAddress;//host

	/**
	 * 数据库服务器端口
	 */
	public int DBPort;//port

	/**
	 * JDBC URL，特殊情况（例如Oracle RAC）下需要使用这个
	 */
	public String ConnectionURL; // 指定连接串，可以用于oracleRAC

	/**
	 * 数据库名称
	 */
	public String DBName;//databaseName

	/**
	 * 连接时使用的用户名
	 */
	public String DBUserName;//userName

	/**
	 * 连接时使用的用户密码
	 */
	public String DBPassword;//password

	/**
	 * 测试表名
	 */
	public String TestTable;//testTable

	/**
	 * 连接池名称
	 */
	public String PoolName;//name

	/**
	 * 数据库字符集
	 */
	public String Charset;

	/**
	 * 是否是latin1字符集，如果在Oracle下是此字符集，则SQL及返回结果会自动转码
	 */
	public boolean isLatin1Charset;// 是否是latin1字符集，如果在Oracle下是此字符集，则SQL及返回结果必须转码

	public ConnectionConfig() {
	}
	
	public ConnectionConfig(String host, int port, String databaseName, String userName, String password) {
		this.DBServerAddress = host;
		this.DBPort = port;
		this.DBName = databaseName;
		this.DBUserName = userName;
		this.DBPassword = password;
	}
	
	public String getPoolName() {
		return PoolName;
	}
	
	public String getDatabaseType() {
		return DBType;
	}
	
	public String getHost() {
		return DBServerAddress;
	}
	
	public int getPort() {
		return DBPort;
	}
	
	public String getDatabaseName() {
		return DBName;
	}
	
	public String getUserName() {
		return DBUserName;
	}
	
	public String getPassword() {
		return DBPassword;
	}
	
	public boolean isLatin1Charset() {
		return isLatin1Charset;
	}
	
	public void setDatabaseName(String databaseName) {
		this.DBName = databaseName;
	}
	
	public void setDatabaseType(String databaseType) {
		this.DBType = databaseType;
	}
	
	public void setHost(String host) {
		this.DBServerAddress = host;
	}
	
	public void setPassword(String password) {
		this.DBPassword = password;
	}
	
	public void setPoolName(String poolName) {
		this.PoolName = poolName;
	}
	
	public void setPort(String port) {
		this.DBPort = Integer.parseInt(port);
	}
	
	public void setUserName(String username) {
		this.DBUserName = username;
	}
	
	/**
	 * @return 数据库服务器是否是Oracle
	 */
	public boolean isOracle() {
		return DBType.equalsIgnoreCase(ORACLE);
	}

	/**
	 * @return 数据库服务器是否是DB2
	 */
	public boolean isDB2() {
		return DBType.equalsIgnoreCase(DB2);
	}

	/**
	 * @return 数据库服务器是否是Mysql
	 */
	public boolean isMysql() {
		return DBType.equalsIgnoreCase(MYSQL);
	}

	/**
	 * @return 数据库服务器是否是SQLServer(2005以上)
	 */
	public boolean isSQLServer() {
		return DBType.equalsIgnoreCase(MSSQL);
	}

	/**
	 * @return 数据库服务器是否是SQLServer2000
	 */
	public boolean isSQLServer2000() {
		return DBType.equalsIgnoreCase(MSSQL2000);
	}

	/**
	 * @return 数据库服务器是否是Sybase ASE
	 */
	public boolean isSybase() {
		return DBType.equalsIgnoreCase(SYBASE);
	}

	/**
	 * @return 数据库服务器是否是HSQLDB
	 */
	public boolean isHSQLDB() {
		return DBType.equalsIgnoreCase(HSQLDB);
	}

	/**
	 * @return 数据库服务器是否是嵌入式的Derby
	 */
	public boolean isDerbyEmbedded() {
		return DBType.equalsIgnoreCase(DERBY_EMBEDDED);
	}

	/**
	 * @return 数据库服务器是否是服务器模式的Derby
	 */
	public boolean isDerbyServer() {
		return DBType.equalsIgnoreCase(DERBY_SERVER);
	}

}
