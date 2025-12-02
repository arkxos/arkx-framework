package io.arkx.framework.data.jdbc.connection;
// package org.ark.framework.persistence.jdbc.connection;
//
// import java.sql.Array;
// import java.sql.Blob;
// import java.sql.CallableStatement;
// import java.sql.Clob;
// import java.sql.DatabaseMetaData;
// import java.sql.NClob;
// import java.sql.PreparedStatement;
// import java.sql.SQLClientInfoException;
// import java.sql.SQLException;
// import java.sql.SQLWarning;
// import java.sql.SQLXML;
// import java.sql.Savepoint;
// import java.sql.Statement;
// import java.sql.Struct;
// import java.util.Map;
// import java.util.Properties;
// import java.util.concurrent.Executor;
//
// import org.apache.commons.lang.ArrayUtils;
// import org.apache.log4j.Logger;
// import org.ark.framework.Config;
// import org.ark.framework.orm.db.DatabaseService;
// import org.ark.framework.orm.db.IDatabaseType;
// import org.ark.framework.orm.sql.CurrentConnection;
//
// import nativejdbc.db.io.arkx.framework.data.CommonsDbcpNativeJdbcExtractor;
// import nativejdbc.db.io.arkx.framework.data.JBossNativeJdbcExtractor;
// import nativejdbc.db.io.arkx.framework.data.WebLogicNativeJdbcExtractor;
// import nativejdbc.db.io.arkx.framework.data.WebSphereNativeJdbcExtractor;
// import io.arkx.framework.framework.collection.Mapx;
//
//
/// **
// * @class org.ark.framework.orm.connection.XConnection
//
// */
// public class Connection implements java.sql.Connection {
//
// private static Logger logger = Logger.getLogger(Connection.class);
//
// protected boolean LongTimeFlag = true;//false;
// protected java.sql.Connection connection;
// protected long LastApplyTime;
// protected long LastWarnTime;
// protected long LastSuccessExecuteTime = System.currentTimeMillis();
// protected boolean isUsing = false;
// public boolean isBlockingTransactionStarted;
// protected String CallerString = null;
//
// protected String LastSQL = null;
//
// public int ConnID = 0;
//
// protected ConnectionConfig DBConfig;
// private static Mapx<Integer, String> IDMap = new Mapx<Integer, String>();
//
// private static Object mutex = new Object();
//
// /**
// * 获取服务器自身本地连接
// */
// public java.sql.Connection getPhysicalConnection() {
// if (this.DBConfig.isJNDIPool) {
// try {
// // ServerTypes serverType = DBConfig.getServerType();
// // if (serverType == ServerTypes.Tomcat) {
// // return
// // CommonsDbcpNativeJdbcExtractor.doGetNativeConnection(this.connection);
// // } else if (serverType == ServerTypes.Weblogic) {
// // return
// // WebLogicNativeJdbcExtractor.doGetNativeConnection(this.connection);
// // } else if (serverType == ServerTypes.WebSphere) {
// // return
// // WebSphereNativeJdbcExtractor.doGetNativeConnection(this.connection);
// // } else {
// // return
// // JBossNativeJdbcExtractor.doGetNativeConnection(this.connection);
// // }
// if (Config.isTomcat())
// return CommonsDbcpNativeJdbcExtractor.doGetNativeConnection(this.connection);
// if (Config.isWeblogic())
// return WebLogicNativeJdbcExtractor.doGetNativeConnection(this.connection);
// if (Config.isWebSphere()) {
// return WebSphereNativeJdbcExtractor.doGetNativeConnection(this.connection);
// }
// return JBossNativeJdbcExtractor.doGetNativeConnection(this.connection);
// } catch (SQLException e) {
// e.printStackTrace();
// }
// }
// return this.connection;
// }
//
// public int getHoldability() throws SQLException {
// return this.connection.getHoldability();
// }
//
// public int getTransactionIsolation() throws SQLException {
// return this.connection.getTransactionIsolation();
// }
//
// public void clearWarnings() throws SQLException {
// this.connection.clearWarnings();
// }
//
// /**
// * 设置连接为非使用状态
// */
// public void close() throws SQLException {
// if(this == CurrentConnection.getCurrentThreadConnection()) {
// return;
// }
// this.isUsing = false;
// this.LastApplyTime = 0L;
// setAutoCommit(true);
// if (this.DBConfig.isJNDIPool)
// this.connection.close();
// }
//
// public void closeCurrentConnection() throws SQLException {
// this.isUsing = false;
// this.LastApplyTime = 0L;
// setAutoCommit(true);
// if (this.DBConfig.isJNDIPool)
// this.connection.close();
// }
//
// /**
// * 关闭连接，并从连接池中删除
// *
// * @throws SQLException
// */
// public void closeReally() throws SQLException {
// ConnectionPool pool = (ConnectionPool)
// ConnectionPoolManager.PoolMap.get(this.DBConfig.getDatabaseName() + ".");
// removeConnID(this.ConnID);
// if (pool != null) {
// Connection[] arr = (Connection[]) ArrayUtils.removeElement(pool.conns, this);
// Connection[] arr2 = new Connection[arr.length + 1];
// for (int i = 0; i < arr.length; i++) {
// arr2[i] = arr[i];
// }
// pool.conns = arr2;
// pool.getDBConnConfig().ConnCount -= 1;
// // ArrayUtils.removeElement(pool.conns, this);
// }
// this.connection.close();
// }
//
// // [start]
// //
// ////////////////////////////////////////////////////////////////////////////////
// // ///////////// Proxy the inner Connection object connection
// // ////////////////////////////////
// //
// //////////////////////////////////////////////////////////////////////////////
//
// public void commit() throws SQLException {
// if (!this.connection.getAutoCommit())
// this.connection.commit();
// }
//
// public void rollback() throws SQLException {
// if (connection != null) {
// this.connection.rollback();
// }
// }
//
// public boolean getAutoCommit() throws SQLException {
// if(connection == null) {
// return true;
// }
// return this.connection.getAutoCommit();
// }
//
// public boolean isClosed() throws SQLException {
// if (connection == null) {
// return true;
// }
// return this.connection.isClosed();
// }
//
// public boolean isReadOnly() throws SQLException {
// return this.connection.isReadOnly();
// }
//
// public void setHoldability(int holdability) throws SQLException {
// this.connection.setHoldability(holdability);
// }
//
// public void setTransactionIsolation(int level) throws SQLException {
// this.connection.setTransactionIsolation(level);
// }
//
// public void setAutoCommit(boolean autoCommit) throws SQLException {
// if (this.connection.getAutoCommit() != autoCommit)
// this.connection.setAutoCommit(autoCommit);
// }
//
// public void setReadOnly(boolean readOnly) throws SQLException {
// this.connection.setReadOnly(readOnly);
// }
//
// public String getCatalog() throws SQLException {
// return this.connection.getCatalog();
// }
//
// public void setCatalog(String catalog) throws SQLException {
// this.connection.setCatalog(catalog);
// }
//
// public DatabaseMetaData getMetaData() throws SQLException {
// return this.connection.getMetaData();
// }
//
// public SQLWarning getWarnings() throws SQLException {
// return this.connection.getWarnings();
// }
//
// public Savepoint setSavepoint() throws SQLException {
// return this.connection.setSavepoint();
// }
//
// public void releaseSavepoint(Savepoint savepoint) throws SQLException {
// this.connection.releaseSavepoint(savepoint);
// }
//
// public void rollback(Savepoint savepoint) throws SQLException {
// this.connection.rollback(savepoint);
// }
//
// public Statement createStatement() throws SQLException {
// return this.connection.createStatement();
// }
//
// public Statement createStatement(int resultSetType, int resultSetConcurrency)
// throws SQLException {
// return this.connection.createStatement(resultSetType, resultSetConcurrency);
// }
//
// public Statement createStatement(int resultSetType, int resultSetConcurrency,
// int resultSetHoldability) throws SQLException {
// return this.connection.createStatement(resultSetType, resultSetConcurrency,
// resultSetHoldability);
// }
//
// public Map<String, Class<?>> getTypeMap() throws SQLException {
// return this.connection.getTypeMap();
// }
//
// public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
// this.connection.setTypeMap(map);
// }
//
// public String nativeSQL(String sql) throws SQLException {
// return this.connection.nativeSQL(sql);
// }
//
// public CallableStatement prepareCall(String sql) throws SQLException {
// return this.connection.prepareCall(sql);
// }
//
// public CallableStatement prepareCall(String sql, int resultSetType, int
// resultSetConcurrency) throws SQLException {
// return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency);
// }
//
// public CallableStatement prepareCall(String sql, int resultSetType, int
// resultSetConcurrency, int resultSetHoldability) throws SQLException {
// return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency,
// resultSetHoldability);
// }
//
// public PreparedStatement prepareStatement(String sql) throws SQLException {
// return this.connection.prepareStatement(sql);
// }
//
// public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
// throws SQLException {
// return this.connection.prepareStatement(sql, autoGeneratedKeys);
// }
//
// public PreparedStatement prepareStatement(String sql, int resultSetType, int
// resultSetConcurrency) throws SQLException {
// this.LastSQL = sql;
// return this.connection.prepareStatement(sql, resultSetType,
// resultSetConcurrency);
// }
//
// public PreparedStatement prepareStatement(String sql, int resultSetType, int
// resultSetConcurrency, int resultSetHoldability) throws SQLException {
// return this.connection.prepareStatement(sql, resultSetType,
// resultSetConcurrency, resultSetHoldability);
// }
//
// public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
// throws SQLException {
// return this.connection.prepareStatement(sql, columnIndexes);
// }
//
// public Savepoint setSavepoint(String name) throws SQLException {
// return this.connection.setSavepoint(name);
// }
//
// public PreparedStatement prepareStatement(String sql, String[] columnNames)
// throws SQLException {
// return this.connection.prepareStatement(sql, columnNames);
// }
//
// public long getLastSuccessExecuteTime() {
// return this.LastSuccessExecuteTime;
// }
//
// public void setLastSuccessExecuteTime(long lastSuccessExecuteTime) {
// this.LastSuccessExecuteTime = lastSuccessExecuteTime;
// }
//
// public ConnectionConfig getDBConfig() {
// return this.DBConfig;
// }
//
// public void setPoolName(ConnectionConfig dbcc) {
// this.DBConfig = dbcc;
// }
//
// /** @deprecated */
// public IDatabaseType getDBType() {
// return DatabaseService.valueOf(this.DBConfig.getDatabaseType());
// }
//
// // /** @deprecated */
// // public String getDBType() {
// // return this.DBConfig.getDatabaseType().getCode();
// // }
//
// public static int getConnID() {
// synchronized (mutex) {
// for (int i = 1; i <= 2000; i++) {
// if (!IDMap.containsKey(i)) {
// IDMap.put(i, "1");
// return i;
// }
// }
// }
// return 0;
// }
//
// public static void removeConnID(int id) {
// synchronized (mutex) {
// IDMap.remove(id);
// }
// }
//
// public boolean isExcuteTooLong() {
// if (LongTimeFlag) {
// return false;
// }
// long now = System.currentTimeMillis();
// if (now - LastSuccessExecuteTime <= this.DBConfig.MaxConnUsingTime) {
// return false;
// }
//
// logger.error(this.DBConfig.getPoolName() + ":检测到连接使用超时" + (now -
// LastSuccessExecuteTime) + "，程序可能存在连接池泄漏，将自动关闭连接。以下是最后执行的SQL及调用堆栈:");
// logger.error("LastSQL:" + LastSQL);
// logger.error(CallerString);
// return true;
// }
//
// public void commitAndClose() {
// final Connection conn = this;
// new Runnable() {
//
// public void run() {
// try {
// if (!conn.connection.getAutoCommit())
// conn.connection.rollback();
// } catch (SQLException e) {
// e.printStackTrace();
// try {
// conn.connection.close();
// } catch (SQLException ex) {
// ex.printStackTrace();
// }
// } finally {
// try {
// conn.connection.close();
// } catch (SQLException e) {
// e.printStackTrace();
// }
// }
// }
// }.run();
// }
//
// public <T> T unwrap(Class<T> iface) throws SQLException {
// return connection.unwrap(iface);
// }
//
// public boolean isWrapperFor(Class<?> iface) throws SQLException {
// return connection.isWrapperFor(iface);
// }
//
// public Clob createClob() throws SQLException {
// return connection.createClob();
// }
//
// public Blob createBlob() throws SQLException {
// return connection.createBlob();
// }
//
// public NClob createNClob() throws SQLException {
// return connection.createNClob();
// }
//
// public SQLXML createSQLXML() throws SQLException {
// return connection.createSQLXML();
// }
//
// public boolean isValid(int timeout) throws SQLException {
// return connection.isValid(timeout);
// }
//
// public void setClientInfo(String name, String value) throws
// SQLClientInfoException {
// connection.setClientInfo(name, value);
// }
//
// public void setClientInfo(Properties properties) throws
// SQLClientInfoException {
// connection.setClientInfo(properties);
// }
//
// public String getClientInfo(String name) throws SQLException {
// return connection.getClientInfo(name);
// }
//
// public Properties getClientInfo() throws SQLException {
// return connection.getClientInfo();
// }
//
// public Array createArrayOf(String typeName, Object[] elements) throws
// SQLException {
// return connection.createArrayOf(typeName, elements);
// }
//
// public Struct createStruct(String typeName, Object[] attributes) throws
// SQLException {
// return connection.createStruct(typeName, attributes);
// }
//
// @Override
// public void setSchema(String schema) throws SQLException {
// connection.setSchema(schema);
// }
//
// @Override
// public String getSchema() throws SQLException {
// return connection.getSchema();
// }
//
// @Override
// public void abort(Executor executor) throws SQLException {
// connection.abort(executor);
// }
//
// @Override
// public void setNetworkTimeout(Executor executor, int milliseconds) throws
// SQLException {
// connection.setNetworkTimeout(executor, milliseconds);
// }
//
// @Override
// public int getNetworkTimeout() throws SQLException {
// return connection.getNetworkTimeout();
// }
// }
