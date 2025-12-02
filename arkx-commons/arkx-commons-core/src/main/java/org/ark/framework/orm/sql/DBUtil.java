package org.ark.framework.orm.sql;

import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionConfig;
import io.arkx.framework.data.db.connection.ConnectionPool;
import io.arkx.framework.data.db.connection.ConnectionPoolManager;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.data.jdbc.ResultDataTable;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;

/**
 * @class org.ark.framework.orm.sql.DBUtil
 * @author Darkness
 * @date 2013-1-31 上午11:45:04
 * @version V1.0
 */
public class DBUtil {

	public static DataTable getTableInfo() {
		return getTableInfo(ConnectionPoolManager.getDBConnConfig());
	}

	public static DataTable getTableInfo(ConnectionConfig dcc) {
		Connection conn = null;
		try {
			conn = ConnectionPool.createConnection(dcc, false);
			DatabaseMetaData dbm = conn.getMetaData();
			String currentCatalog = conn.getCatalog();
			ResultSet rs = dbm.getTables(currentCatalog, null, null, null);
			DataTable dt = new ResultDataTable(rs);
			for (DataRow dr : dt) {
				if (dr.get(1) != null) {
					dr.set(2, dr.getString(1) + "." + dr.getString(2));
				}
			}
			DataTable localDataTable1 = dt;
			return localDataTable1;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static DataTable getColumnInfo(String tableName) {
		return getColumnInfo(ConnectionPoolManager.getDBConnConfig(), tableName);
	}

	public static DataTable getColumnInfo(ConnectionConfig dcc, String tableName) {
		Connection conn = null;
		try {
			conn = ConnectionPool.createConnection(dcc, false);
			DatabaseMetaData dbm = conn.getMetaData();
			String currentCatalog = conn.getCatalog();
			String schema = null;
			String oldName = tableName;
			int index = tableName.indexOf(".");
			if (index > 0) {
				schema = tableName.substring(0, index);
				tableName = tableName.substring(index + 1);
			}
			ResultSet rs = dbm.getColumns(currentCatalog, schema, tableName, null);
			DataTable dt = new ResultDataTable(rs);

			rs = dbm.getPrimaryKeys(currentCatalog, null, tableName);
			DataTable keyDt = new ResultDataTable(rs);
			Mapx map = keyDt.toMapx("Column_Name", "PK_Name");
			dt.insertColumn("isKey");
			for (int i = 0; i < dt.getRowCount(); i++) {
				DataRow dr = dt.getDataRow(i);
				if (map.containsKey(dr.getString("Column_Name")))
					dr.set("isKey", "Y");
				else {
					dr.set("isKey", "N");
				}
			}
			DataTable data = getSession().createQuery("select * from " + oldName + " where 1=2").executeDataTable();
			for (int i = 0; i < data.getColumnCount(); i++) {
				DataRow dr = dt.getDataRow(i);
				dr.set("Type_Name", Integer.valueOf(data.getDataColumn(i).getColumnType().code()));
			}
			DataTable localDataTable1 = dt;
			return localDataTable1;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static DataTable getSQLTypes() {
		return getSQLTypes(ConnectionPoolManager.getDBConnConfig());
	}

	public static DataTable getSQLTypes(ConnectionConfig dcc) {
		Connection conn = null;
		try {
			conn = ConnectionPool.createConnection(dcc, false);
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet rs = dbm.getTypeInfo();
			DataTable dt = new ResultDataTable(rs);
			DataTable localDataTable1 = dt;
			return localDataTable1;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static int getCount(Query qb) {
		return getCount(qb, null);
	}

	public static int getCount(Query qb, String poolName) {
		int i = qb.getCount();
		return i;
	}

	public static Timestamp getOracleTimestamp(Object value) {
		try {
			Class clz = value.getClass();
			Method m = clz.getMethod("timestampValue", null);
			return (Timestamp) m.invoke(value, null);
		}
		catch (Exception e) {
		}
		return null;
	}

	public static String sqlBitAndFunction(String columnName, String n) {
		return sqlBitAndFunction(columnName, n, null);
	}

	public static String sqlBitAndFunction(String columnName, int n) {
		return sqlBitAndFunction(columnName, n + "", null);
	}

	public static String sqlBitAndFunction(String columnName, String n, String poolName) {
		ConnectionConfig dcc = ConnectionPoolManager.getDBConnConfig(poolName);
		if (dcc.isSQLServer() || dcc.isMysql())
			return columnName + "&" + n;
		if (dcc.isOracle() || dcc.isDB2()) {
			return "bitand(" + columnName + "," + n + ")";
		}
		return columnName + "&" + n;
	}

	public static Session getSession() {
		return SessionFactory.currentSession();
	}

}
