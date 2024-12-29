package com.arkxos.framework.data.db.command;

import java.util.HashMap;

import com.arkxos.framework.json.JSON;
import com.arkxos.framework.json.JSONObject;

/**
 * 删除数据表指令
 * 
 */
public class DropTableCommand implements IDBCommand {
	/**
	 * 要删除的数据表
	 */
	public String Table;

	public static final String Prefix = "DropTable:";

	@Override
	public String getPrefix() {
		return Prefix;
	}

	@Override
	public void parse(String ddl) {
		ddl = ddl.substring(Prefix.length());
		JSONObject map = (JSONObject) JSON.parse(ddl);
		Table = map.getString("Table");
	}

	@Override
	public String toJSON() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("Table", Table);
		return Prefix + JSON.toJSONString(map);
	}

	@Override
	public String[] getDefaultSQLArray(String dbType) {
		return new String[] { "drop table " + Table };
	}
}
