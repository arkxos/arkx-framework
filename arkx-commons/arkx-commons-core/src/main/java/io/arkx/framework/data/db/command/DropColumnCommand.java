package io.arkx.framework.data.db.command;

import java.util.HashMap;

import io.arkx.framework.data.db.dbtype.DBTypeService;
import io.arkx.framework.data.db.dbtype.IDBType;
import io.arkx.framework.json.JSON;
import io.arkx.framework.json.JSONObject;

/**
 * 删除字段指令
 *
 */
public class DropColumnCommand implements IDBCommand {

	/**
	 * 要删除的字段
	 */
	public String Column;

	/**
	 * 所在数据表
	 */
	public String Table;

	public static final String Prefix = "DropColumn:";

	@Override
	public String getPrefix() {
		return Prefix;
	}

	@Override
	public void parse(String ddl) {
		ddl = ddl.substring(Prefix.length());
		JSONObject map = (JSONObject) JSON.parse(ddl);
		Table = map.getString("Table");
		Column = map.getString("Column");
	}

	@Override
	public String toJSON() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("Table", Table);
		map.put("Column", Column);
		return Prefix + JSON.toJSONString(map);
	}

	@Override
	public String[] getDefaultSQLArray(String dbType) {
		IDBType db = DBTypeService.getInstance().get(dbType);
		String sql = "alter table " + Table + " drop column " + db.maskColumnName(Column);
		return new String[] { sql };
	}

}
