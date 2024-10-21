package com.rapidark.framework.data.db.dbtype;

import java.util.ArrayList;
import java.util.List;

import com.rapidark.framework.commons.collection.DataTypes;
import com.rapidark.framework.commons.util.ObjectUtil;
import com.rapidark.framework.commons.util.StringFormat;
import com.rapidark.framework.data.db.command.AdvanceChangeColumnCommand;
import com.rapidark.framework.data.db.command.ChangeColumnLengthCommand;
import com.rapidark.framework.data.db.command.ChangeColumnMandatoryCommand;
import com.rapidark.framework.data.db.command.RenameColumnCommand;
import com.rapidark.framework.data.db.command.RenameTableCommand;
import com.rapidark.framework.data.db.connection.Connection;
import com.rapidark.framework.data.db.connection.ConnectionConfig;
import com.sun.star.uno.RuntimeException;

/**
 * IBM DB2 UDB数据库
 * 
 */
public class Db2 extends AbstractDBType {

	public final static String ID = "DB2";

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
		StringBuilder sb = new StringBuilder();
		sb.append("jdbc:db2://");
		sb.append(dcc.DBServerAddress);
		sb.append(":");
		sb.append(dcc.DBPort);
		sb.append("/");
		sb.append(dcc.DBName);
		return sb.toString();
	}

	@Override
	public void afterConnectionCreate(Connection conn) {
	}

	@Override
	public int getDefaultPort() {
		return 50000;
	}

	@Override
	public String getPKNameFragment(String table) {
		String pkName = table;
		if (pkName.length() > 15) {
			pkName = pkName.substring(0, 15);
		}
		return "constraint PK_" + pkName + " primary key";
	}

	@Override
	public String toSQLType(DataTypes columnType, int length, int precision) {
		String type = null;
		if (columnType == DataTypes.BIGDECIMAL) {
			type = "DOUBLE PRECISION";
		} else if (columnType == DataTypes.BLOB) {
			type = "BLOB";
		} else if (columnType == DataTypes.DATETIME) {
			type = "TIMESTAMP";
		} else if (columnType == DataTypes.DECIMAL) {
			type = "DECIMAL";
		} else if (columnType == DataTypes.DOUBLE) {
			type = "NUMERIC";
		} else if (columnType == DataTypes.FLOAT) {
			type = "NUMERIC";
		} else if (columnType == DataTypes.INTEGER) {
			type = "INTEGER";
		} else if (columnType == DataTypes.LONG) {
			type = "BIGINT";
		} else if (columnType == DataTypes.SMALLINT) {
			type = "INTEGER";
		} else if (columnType == DataTypes.STRING) {
			type = "VARCHAR";
		} else if (columnType == DataTypes.CLOB) {
			type = "CLOB";
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
		StringBuffer sb = new StringBuffer();
		int start = pageIndex * pageSize;
		int end = (pageIndex + 1) * pageSize;
		sb.append("select * from (select rs.*,rownumber() OVER () rnm from (");
		sb.append(sql);
		sb.append(") rs) rss WHERE rnm BETWEEN " + (start + 1) + " and " + end);
		return sb.toString();
	}

	@Override
	public String getDriverClass() {
		return "com.ibm.db2.jcc.DB2Driver";
	}

	@Override
	public String[] toSQLArray(AdvanceChangeColumnCommand c) {
		String[] arr = c.getDefaultSQLArray(ID);
		List<String> list = new ArrayList<String>();
		for (String sql : arr) {
			list.add(sql);
			if (sql.startsWith("alter table")) {
				list.add("reorg table " + c.Table);// DB2下修改了表结构需要reorg
			}
		}
		arr = new String[list.size()];
		return list.toArray(arr);
	}

	@Override
	public String[] toSQLArray(RenameColumnCommand c) {
		AdvanceChangeColumnCommand acc = new AdvanceChangeColumnCommand();
		acc.Table = c.Table;
		acc.Column = c.NewColumn;
		acc.DataType = c.DataType;
		acc.OldDataType = c.DataType;
		acc.Length = c.Length;
		acc.OldLength = c.Length;
		acc.Precision = c.Precision;
		acc.OldPrecision = c.Precision;
		acc.Mandatory = c.Mandatory;
		acc.OldMandatory = c.Mandatory;
		return toSQLArray(acc);
	}

	@Override
	public String[] toSQLArray(ChangeColumnLengthCommand c) {
		String sql = StringFormat.format("alter table ? alter ? set data type ?", c.Table, c.Column,
				toSQLType(DataTypes.valueOf((byte)c.DataType), c.Length, c.Precision));
		String reorg = "reorg table " + c.Table;
		return new String[] { sql, reorg };
	}

	@Override
	public String[] toSQLArray(RenameTableCommand c) {
		return new String[] { "rename table " + c.Table + " to " + c.NewTable };
	}

	@Override
	public String getSQLSperator() {
		return ";\n";
	}

	@Override
	public String[] toSQLArray(ChangeColumnMandatoryCommand c) {
		String sql = null;
		if (c.Mandatory) {
			sql = StringFormat.format("alter table ? alter ? set not null", c.Table, c.Column);
		} else {
			sql = StringFormat.format("alter table ? alter ? drop not null", c.Table, c.Column);
		}
		String reorg = "reorg table " + c.Table;
		return new String[] { sql, reorg };
	}

	@Override
	public String getForUpdate() {
		return " for update with rs";
	}
	
	@Override
	public boolean isTableExist(String databaseName, String tableName) {
		throw new RuntimeException("该方法未实现！");
	}
}
