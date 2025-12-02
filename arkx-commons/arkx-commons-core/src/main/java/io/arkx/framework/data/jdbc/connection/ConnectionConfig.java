package io.arkx.framework.data.jdbc.connection;

// package org.ark.framework.persistence.jdbc.connection;
//
// import java.sql.Connection;
// import java.sql.SQLException;
//
// import org.apache.log4j.Logger;
// import org.ark.framework.orm.db.DatabaseService;
// import org.ark.framework.utility.ObjectUtil;
// import util.io.arkx.framework.commons.StringUtil;
//
// import io.arkx.framework.framework.utility.LogUtil;
//
//
/// **
// * @class org.ark.framework.orm.connection.XConnectionConfig
// * 数据库连接配置 Create on May 14, 2010 11:29:37 AM
// *
// * @author Darkness
// * @date 2012-4-7 下午9:48:17
// * @version V1.0
// */
// public class ConnectionConfig {
//
// //private static Logger logger = Logger.getLogger(XConnectionConfig.class);
//
// public String JNDIName = null;
//
// public boolean isJNDIPool = false;
//
// public int MaxConnCount = 1000;
//
// public int InitConnCount = 5;
// public int ConnCount;
// public int MaxConnUsingTime = 300000;
//
// public int RefershPeriod = 60 * 1000;
//
// public String ConnectionURL;
// public String PoolName;
//
// private static Logger logger = Logger.getLogger(ConnectionConfig.class);
//
// private String name;
//
// private String host;
// private int port;
// private String databaseName;
// private String userName;
// private String password;
//
// private String databaseType;
// private String testTable;
//
// private String jdbcUrl;
//
// /**
// * 数据库字符集
// */
// private String charset = "UTF-8";
//
// private boolean isLatin1Charset = false;
//
// public ConnectionConfig() {
// }
//
// public ConnectionConfig(String host, int port, String databaseName, String
// userName, String password) {
// this.host = host;
// this.port = port;
// this.databaseName = databaseName;
// this.userName = userName;
// this.password = password;
// }
// public String getName() {
// return name;
// }
//
// public void setName(String name) {
// this.name = name;
// }
//
// public String getHost() {
// return host;
// }
//
// public void setHost(String host) {
// this.host = host;
// }
//
// public int getPort() {
//// if (port == 0) {
//// return databaseType.getDefaultPort();
//// }
// return port;
// }
//
// public void setPort(String port) {
// try {
// this.port = Integer.parseInt(port);
// } catch (NumberFormatException e) {
//// this.port = databaseType.getDefaultPort();
// logger.warn("配置项DB.Port错误," + port + "不是有效的整数，该配置项将采用默认值" + this.port + "!");
// }
// }
//
// public String getDatabaseName() {
// return databaseName;
// }
//
// public void setDatabaseName(String dbName) {
// this.databaseName = dbName;
// }
//
// public String getUserName() {
// return userName;
// }
//
// public void setUserName(String userName) {
// this.userName = userName;
// }
//
// public String getPassword() {
// return password;
// }
//
// public void setPassword(String password) {
// this.password = password;
// }
//
// public String getDatabaseType() {
// return databaseType;
// }
//
//// public void setDatabaseType(IDatabaseType database) {
//// this.databaseType = database;
//// }
//
// public void setDatabaseType(String databaseType) {
// this.databaseType = databaseType;
// }
//
// public String getTestTable() {
// return testTable;
// }
//
// public void setTestTable(String testTable) {
// this.testTable = testTable;
// }
//
// public String getCharset() {
// return charset;
// }
//
// public void setCharset(String charset) {
// this.charset = charset;
// }
//
// public boolean isLatin1Charset() {
// return isLatin1Charset;
// }
//
// public void setLatin1Charset(boolean isLatin1Charset) {
// this.isLatin1Charset = isLatin1Charset;
// }
//
//// public void setJdbcUrl(String jdbcUrl) {
//// this.jdbcUrl = jdbcUrl;
//// if (this.jdbcUrl.indexOf("oracle") != -1) {
//// setDatabaseType(DatabaseService.ORACLE);
//// } else if (this.jdbcUrl.indexOf("mysql") != -1) {
//// setDatabaseType(DatabaseService.MYSQL);
//// }
//// }
//
//
// @Override
// public String toString() {
// return "pool name:" + PoolName + ",jdbc url:" + getJdbcUrl()
// + ",db name;" + getDatabaseName()
// + ",username:" + getUserName() + ",password:" + getPassword();
//
// // "<Type>"+DBType+"</Type>\r\n" +
// // "<ServerAddress>"+DBServerAddress+"</ServerAddress>\r\n" +
// // "<Port>"+DBPort+"</Port>\r\n" +
// // "<Name>"+DBName+"</Name>\r\n" +
// // "<UserName>"+DBUserName+"</UserName>\r\n" +
// // //"<Password>"+Password+"</Password>\r\n" +
// // "<MaxConnCount>"+MaxConnCount+"</MaxConnCount>\r\n" +
// // "<InitConnCount>"+InitConnCount+"</InitConnCount>\r\n" +
// // "<TestTable>"+TestTable+"</TestTable>";
// }
//
// public String getJdbcUrl() {
//
// if (StringUtil.isNotEmpty(ConnectionURL)) {
// return ConnectionURL;
// }
//
// ConnectionConfig databaseConnectionInfo = new ConnectionConfig(
// getHost(), getPort(), getDatabaseName(), getUserName(), getPassword());
//
// return
// DatabaseService.valueOf(getDatabaseType()).getJdbcUrl(databaseConnectionInfo);
// }
//
// public Connection createConnection() {
//
// try {
// return DatabaseService.valueOf(getDatabaseType()).createConnection(this);
// } catch (ClassNotFoundException e) {
// e.printStackTrace();
// } catch (SQLException e) {
// e.printStackTrace();
// }
//
// return null;
// }
//
// /**
// * 验证连接配置
// *
// * @author Darkness
// * @date 2012-10-6 下午5:22:29
// * @version V1.0
// */
// public void validate() {
// if (getDatabaseType() == null) {
// throw new RuntimeException("DB.Type not found");
// }
//
// if (ObjectUtil.empty(ConnectionURL)) {
// if (StringUtil.isEmpty(getHost())) {
// throw new RuntimeException("DB.ServerAddress not found");
// }
// if (StringUtil.isEmpty(getDatabaseName())) {
// throw new RuntimeException("DB.Name not found");
// }
// }
//
// if (StringUtil.isEmpty(getUserName())) {
// throw new RuntimeException("DB.UserName not found");
// }
// if (StringUtil.isEmpty(getPassword())) {
// throw new RuntimeException("DB.Password not found");
// }
// }
//
// public void setInitConnCount(String connCount) {
// try {
// InitConnCount = Integer.parseInt(connCount);
// } catch (NumberFormatException e) {
// InitConnCount = 0;
// LogUtil.warn(connCount + " is invalid DB.InitConnCount value,will use 0");
// }
// }
//
// public void setMaxConnCount(String maxConnCount) {
// try {
// MaxConnCount = Integer.parseInt(maxConnCount);
// } catch (NumberFormatException e) {
// MaxConnCount = 20;
// LogUtil.warn(maxConnCount + " is invalid DB.MaxConnCount value,will use 20");
// }
// }
//
// public String getPoolName() {
// return PoolName;
// }
//
// public void setPoolName(String poolName) {
// PoolName = poolName;
// }
//
// public void connectionCountAddOne() {
// ConnCount += 1;
// }
//
// public void connectionCountMinusOne() {
// ConnCount -= 1;
// }
//
// }
