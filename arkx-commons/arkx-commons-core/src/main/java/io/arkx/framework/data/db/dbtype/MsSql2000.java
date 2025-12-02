package io.arkx.framework.data.db.dbtype;

import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.data.db.command.DropIndexCommand;
import io.arkx.framework.data.db.command.DropTableCommand;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionConfig;

import com.sun.star.uno.RuntimeException;

/**
 * SQLServer 2000数据库
 *
 * @author Darkness
 * @date 2011-12-10 下午04:57:55
 * @version V1.0
 */
public class MsSql2000 extends AbstractDBType {

	public final static String ID = "MSSQL2000";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "SQLServer 2000";
	}

	@Override
	public boolean isFullSupport() {
		return false;
	}

	@Override
	public String getJdbcUrl(ConnectionConfig dcc) {
		StringBuilder sb = new StringBuilder();
		sb.append("jdbc:jtds:sqlserver://");
		sb.append(dcc.DBServerAddress);
		sb.append(":");
		sb.append(dcc.DBPort);
		sb.append(";DatabaseName=");
		sb.append(dcc.DBName);
		sb.append(";useLOBs=false");
		return sb.toString();
	}

	@Override
	public void afterConnectionCreate(Connection conn) {

	}

	@Override
	public int getDefaultPort() {
		return 1433;
	}

	@Override
	public String toSQLType(DataTypes columnType, int length, int precision) {
		String type = null;
		if (columnType == DataTypes.BIGDECIMAL) {
			type = "numeric";
		}
		else if (columnType == DataTypes.BLOB) {
			type = "varbinary(MAX)";
		}
		else if (columnType == DataTypes.DATETIME) {
			type = "datetime";
		}
		else if (columnType == DataTypes.DECIMAL) {
			type = "decimal";
		}
		else if (columnType == DataTypes.DOUBLE) {
			type = "numeric";
		}
		else if (columnType == DataTypes.FLOAT) {
			type = "numeric";
		}
		else if (columnType == DataTypes.INTEGER) {
			type = "int";
		}
		else if (columnType == DataTypes.LONG) {
			type = "bigint";
		}
		else if (columnType == DataTypes.SMALLINT) {
			type = "int";
		}
		else if (columnType == DataTypes.STRING) {
			type = "varchar";
		}
		else if (columnType == DataTypes.CLOB) {
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
		return sql;
	}

	@Override
	public String[] toSQLArray(DropIndexCommand c) {
		return new String[] { "drop index " + c.Name + " on " + c.Table };
	}

	@Override
	public String[] toSQLArray(DropTableCommand c) {
		String sql = "if exists (select 1 from  sysobjects where id = object_id('" + c.Table
				+ "') and type='U') drop table " + c.Table;
		return new String[] { sql };
	}

	@Override
	public String getPKNameFragment(String table) {
		return "constraint PK_" + table + " primary key nonclustered";
	}

	@Override
	public String getDriverClass() {
		return "net.sourceforge.jtds.jdbc.Driver";
	}

	@Override
	public String getSQLSperator() {
		return "\ngo\n";
	}

	@Override
	public String getForUpdate() {
		return " with(updlock, readpast)";
	}

	@Override
	public boolean isTableExist(String databaseName, String tableName) {
		throw new RuntimeException("该方法未实现！");
	}

}
