package io.arkx.framework.data.db.dbtype;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionConfig;

/**
 * Derby嵌入式数据库
 * 
 */
public class DerbyEmbedded extends AbstractDBType {

	public final static String ID = "DerbyEmbedded";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "Derby Embedded";
	}

	@Override
	public boolean isFullSupport() {
		return true;
	}

	@Override
	public String getJdbcUrl(ConnectionConfig dcc) {
		return "jdbc:derby:" + dcc.DBName.replace('\\', '/') + ";create=true";
	}

	@Override
	public void afterConnectionCreate(Connection conn) {
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.execute("set schema app");// 将当前schema设为app
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getDefaultPort() {
		return 1527;
	}

	@Override
	public String getPKNameFragment(String table) {
		return "primary key";
	}

	@Override
	public String toSQLType(DataTypes columnType, int length, int precision) {
		String type = null;
		if (columnType == DataTypes.BIGDECIMAL) {
			type = "numeric";
		} else if (columnType == DataTypes.BLOB) {
			type = "blob";
		} else if (columnType == DataTypes.DATETIME) {
			type = "timestamp";
		} else if (columnType == DataTypes.DECIMAL) {
			type = "decimal";
		} else if (columnType == DataTypes.DOUBLE) {
			type = "double";
		} else if (columnType == DataTypes.FLOAT) {
			type = "double";
		} else if (columnType == DataTypes.INTEGER) {
			type = "integer";
		} else if (columnType == DataTypes.LONG) {
			type = "bigint";
		} else if (columnType == DataTypes.SMALLINT) {
			type = "smallint";
		} else if (columnType == DataTypes.STRING) {
			type = "varchar";
		} else if (columnType == DataTypes.CLOB) {
			type = "clob";
		}
		if (ObjectUtil.empty(type)) {
			throw new RuntimeException("Unknown DBType " + getExtendItemID() + " or DataType" + columnType);
		}
		if (length == 0 && columnType == DataTypes.STRING) {
			throw new RuntimeException("varchar's length can't be empty!");
		}

		String FieldExtDesc = "";
		if (length != 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append(length);
			if (precision != 0) {
				sb.append(",");
				sb.append(precision);
			}
			sb.append(") ");
			FieldExtDesc = sb.toString();
		}

		return type + FieldExtDesc;
	}

	@Override
	public String getPagedSQL(String sql, int pageSize, int pageIndex, int connectionId) {
		int start = pageIndex * pageSize;
		return sql + " offset "+start+" rows fetch next "+pageSize+" rows only";
	}

	@Override
	public String getDriverClass() {
		return "org.apache.derby.jdbc.EmbeddedDriver";
	}

	@Override
	public String getSQLSperator() {
		return ";\n";
	}

	@Override
	public String getForUpdate() {
		return "";
	}
	
	@Override
	public boolean isTableExist(String databaseName, String tableName) {
		throw new RuntimeException("该方法未实现！");
	}
}
