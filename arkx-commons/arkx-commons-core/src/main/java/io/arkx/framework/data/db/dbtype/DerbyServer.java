package io.arkx.framework.data.db.dbtype;

import io.arkx.framework.data.db.connection.ConnectionConfig;

/**
 * Derby服务器模式数据库
 * 
 */
public class DerbyServer extends DerbyEmbedded {

	public final static String ID = "DerbyServer";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "Derby Server";
	}

	@Override
	public String getJdbcUrl(ConnectionConfig dcc) {
		StringBuilder sb = new StringBuilder();
		sb.append("jdbc:derby://");
		sb.append(dcc.DBServerAddress);
		sb.append(":");
		sb.append(dcc.DBPort);
		sb.append("/");
		sb.append(dcc.DBName);
		sb.append(";create=true");
		return sb.toString();
	}

	@Override
	public String getDriverClass() {
		return "org.apache.derby.jdbc.ClientDriver";
	}

}
