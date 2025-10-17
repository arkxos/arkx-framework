package io.arkx.framework.data.db.dbtype;

import java.sql.SQLException;

import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.data.db.command.DropTableCommand;
import io.arkx.framework.data.db.command.RenameTableCommand;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionConfig;
import com.sun.star.uno.RuntimeException;

/**
 * 达梦数据库，从7.1开始支持
 * @author Darkness
 * @date 2011-12-10 下午04:57:55
 * @version V1.0
 */
public class Dm extends AbstractDBType {

	public final static String ID = "DM";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "DaMeng";
	}

	@Override
	public boolean isFullSupport() {
		return true;
	}

	@Override
	public String getJdbcUrl(ConnectionConfig dcc) {
		StringBuilder sb = new StringBuilder();
		sb.append("jdbc:dm://");
		sb.append(dcc.DBServerAddress);
		sb.append(":");
		sb.append(dcc.DBPort);
		sb.append("/");
		sb.append(dcc.DBName);

		// 添加schema参数，默认使用数据库名作为schema
		sb.append("?schema="+dcc.DBName);

		return sb.toString();
	}

	@Override
	public int getDefaultPort() {
		return 5236;
	}

	public String getTableDropSQL(String tableCode) {
		return "drop table " + tableCode + " cascade";
	}

	@Override
	public String toSQLType(DataTypes columnType, int length, int precision) {
		String type = null;
		if (columnType == DataTypes.BIGDECIMAL) {
			type = "decimal";
		} else if (columnType == DataTypes.BLOB) {
			type = "blob";
		} else if (columnType == DataTypes.DATETIME) {
			type = "datetime";
		} else if (columnType == DataTypes.DECIMAL) {
			type = "decimal";
		} else if (columnType == DataTypes.DOUBLE) {
			type = "double";
		} else if (columnType == DataTypes.FLOAT) {
			type = "float";
		} else if (columnType == DataTypes.INTEGER) {
			type = "integer";
		} else if (columnType == DataTypes.LONG) {
			type = "bigint";
		} else if (columnType == DataTypes.SMALLINT) {
			type = "short";
		} else if (columnType == DataTypes.STRING) {
			type = "varchar";
		} else if (columnType == DataTypes.CLOB) {
			type = "text";
		}
		if (ObjectUtil.empty(type)) {
			throw new RuntimeException("Unknown DBType " + getExtendItemID() + " or DataType" + columnType);
		}
		if (length == 0 && columnType == DataTypes.STRING) {
			throw new RuntimeException("varchar's length can't be empty!");
		}
		return type + getFieldExtDesc(length, precision);
	}

	@Override
	public String getPagedSQL(String sql, int pageSize, int pageIndex, int connectionId) {
		if (!sql.substring(0, 7).equalsIgnoreCase("select ")) {
			return sql;
		}
		StringBuilder q = new StringBuilder("select top ");
		q.append(sql.substring(7));
		int start = pageIndex * pageSize;
		int end = (pageIndex + 1) * pageSize;
		q.append(start + "," + end);
		return q.toString();
	}

	@Override
	public String getDriverClass() {
		return "dm.jdbc.driver.DmDriver";
	}

	@Override
	public String[] toSQLArray(DropTableCommand c) {
		String sql = "drop table " + c.Table + " cascade";
		return new String[] { sql };
	}

	@Override
	public String[] toSQLArray(RenameTableCommand c) {
		return new String[] { "rename " + c.Table + " to " + c.NewTable };
	}

	@Override
	public String getPKNameFragment(String table) {
		return "primary key";
	}

	@Override
	public String getSQLSperator() {
		return ";\n";
	}

	@Override
	public String getForUpdate() {
		return " for update";
	}

	@Override
	public void afterConnectionCreate(Connection conn) throws SQLException {
	}

	@Override
	public String maskColumnName(String columnName) {
		if ("versions".equalsIgnoreCase(columnName)) {
			columnName = "\"" + columnName + "\"";
		}
		return columnName;
	}
	
	@Override
	public boolean isTableExist(String databaseName, String tableName) {
		throw new RuntimeException("该方法未实现！");
	}
}
