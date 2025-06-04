package io.arkx.framework.data.db.dbtype;

import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionConfig;

/**
 * HSqlDB支持
 * @author Darkness
 * @date 2012-10-6 下午2:25:21
 * @version V1.0
 */
public class HsqlDb extends AbstractDBType {

	public final static String ID = "HSQLDB";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return ID;
	}

	@Override
	public boolean isFullSupport() {
		return true;
	}

	@Override
	public String getJdbcUrl(ConnectionConfig dcc) {
		StringBuilder sb = new StringBuilder(128);
		if ("file".equalsIgnoreCase(dcc.DBServerAddress)) {// 文件数据库
			sb.append("jdbc:hsqldb:res:");
			sb.append(dcc.DBName);
		} else if ("memory".equalsIgnoreCase(dcc.DBServerAddress)) {// 内存
			sb.append("jdbc:hsqldb:mem:.");
		} else {
			sb.append("jdbc:hsqldb:hsql://");
			sb.append(dcc.DBServerAddress);
			sb.append(":");
			sb.append(dcc.DBPort);
			sb.append("/");
			sb.append(dcc.DBName);
		}
		return sb.toString();
	}

	@Override
	public void afterConnectionCreate(Connection conn) {
	}

	@Override
	public int getDefaultPort() {
		return 9001;
	}

	@Override
	public String getPKNameFragment(String table) {
		return "primary key";
	}

	@Override
	public String toSQLType(DataTypes columnType, int length, int precision) {
		String type = null;
		if (columnType == DataTypes.BIGDECIMAL) {
			type = "double";
		} else if (columnType == DataTypes.BLOB) {
			type = "binary varying(MAX)";
		} else if (columnType == DataTypes.DATETIME) {
			type = "datetime";
		} else if (columnType == DataTypes.DECIMAL) {
			type = "decimal";
		} else if (columnType == DataTypes.DOUBLE) {
			type = "double";
		} else if (columnType == DataTypes.FLOAT) {
			type = "float";
		} else if (columnType == DataTypes.INTEGER) {
			type = "int";
		} else if (columnType == DataTypes.LONG) {
			type = "bigint";
		} else if (columnType == DataTypes.SMALLINT) {
			type = "int";
		} else if (columnType == DataTypes.STRING) {
			type = "varchar";
		} else if (columnType == DataTypes.CLOB) {
			type = "longvarchar";
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
		
		String result = "select limit "+start+" "+pageSize+" * from(";
		result += sql;
		result += ")";
		return result;
	}

	@Override
	public String getDriverClass() {
		return "org.hsqldb.jdbcDriver";
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
