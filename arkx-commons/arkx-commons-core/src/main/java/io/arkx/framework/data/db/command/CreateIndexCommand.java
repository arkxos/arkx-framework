package io.arkx.framework.data.db.command;

import java.util.HashMap;
import java.util.List;

import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.data.db.dbtype.DBTypeService;
import io.arkx.framework.data.db.dbtype.IDBType;
import io.arkx.framework.json.JSON;
import io.arkx.framework.json.JSONArray;
import io.arkx.framework.json.JSONObject;

/**
 * 创建索引指令
 * 
 */
public class CreateIndexCommand implements IDBCommand {
	/**
	 * 所在数据表
	 */
	public String Table;
	/**
	 * 索引名称
	 */
	public String Name;
	/**
	 * 索引包含的字段
	 */
	public List<String> Columns;

	public static final String Prefix = "CreateIndex:";

	@Override
	public String getPrefix() {
		return Prefix;
	}

	@Override
	public String[] getDefaultSQLArray(String dbType) {
		IDBType db = DBTypeService.getInstance().get(dbType);
		StringBuilder sb = new StringBuilder();
		sb.append("create index ");
		sb.append(Name);
		sb.append(" on ");
		sb.append(Table);
		sb.append(" (");
		boolean first = true;
		for (String column : Columns) {
			if (StringUtil.isEmpty(column)) {
				continue;
			}
			if (!first) {
				sb.append(",");
			}
			sb.append(db.maskColumnName(column));
			first = false;
		}
		sb.append(")");
		return new String[] { sb.toString() };
	}

	@Override
	public void parse(String ddl) {
		ddl = ddl.substring(Prefix.length());
		JSONObject map = (JSONObject) JSON.parse(ddl);
		Table = map.getString("Table");
		Columns = ObjectUtil.toStringList((JSONArray) map.get("Columns"));
		Name = map.getString("Name");
	}

	@Override
	public String toJSON() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("Table", Table);
		map.put("Columns", Columns);
		map.put("Name", Name);
		return Prefix + JSON.toJSONString(map);
	}
}
