package com.rapidark.framework.data.db.dbtype;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.rapidark.framework.commons.collection.DataTypes;
import com.rapidark.framework.data.db.connection.Connection;
import com.rapidark.framework.data.db.connection.ConnectionConfig;
import com.rapidark.framework.data.db.sql.SelectSQLParser;

/**
 * Sybase ASE数据库
 * @author Darkness
 * @date 2011-12-10 下午04:57:55
 * @version V1.0 
 */
public class Sybase extends MsSql {

	public final static String ID = "SYBASE";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "Sybase";
	}

	@Override
	public boolean isFullSupport() {
		return true;
	}

	@Override
	public String getJdbcUrl(ConnectionConfig dcc) {
		StringBuilder sb = new StringBuilder();
		sb.append("jdbc:sybase:Tds:");
		sb.append(dcc.DBServerAddress);
		sb.append(":");
		sb.append(dcc.DBPort);
		sb.append("/");
		sb.append(dcc.DBName);
		return sb.toString();
	}

	@Override
	public void afterConnectionCreate(Connection conn) {
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.execute("set textsize 20971520");// 防止text字段超32K后，32K之后的部分不能取出来
			stmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public int getDefaultPort() {
		return 5000;
	}

	@Override
	public String getPagedSQL(String sql, int pageSize, int pageIndex, int connectionId) {
		SelectSQLParser sp = new SelectSQLParser(sql);
		try {
			sp.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sp.getSybasePagedSQL(pageSize, pageIndex, Connection.getConnID());
	}

	@Override
	public String getDriverClass() {
		return "com.sybase.jdbc3.jdbc.SybDriver";
	}

	@Override
	public String getForUpdate() {
		return " ";
	}

	@Override
	public Object getValueFromResultSet(ResultSet rs, int columnIndex, DataTypes columnType, boolean latin1Flag) throws SQLException {
		Object v = super.getValueFromResultSet(rs, columnIndex, columnType, latin1Flag);
		if (v != null && (columnType == DataTypes.STRING || columnType == DataTypes.CLOB)) {
			if (v.equals(" ")) {
				v = "";// 这是sybase的一个问题，null或者空字符串会返回一个空格。
			}
		}
		return v;
	}
}
