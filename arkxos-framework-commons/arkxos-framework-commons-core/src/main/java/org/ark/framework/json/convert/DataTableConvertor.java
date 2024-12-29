package org.ark.framework.json.convert;

import org.ark.framework.json.JSONArray;
import org.ark.framework.json.JSONObject;

import com.arkxos.framework.commons.collection.DataColumn;
import com.arkxos.framework.commons.collection.DataTable;

/**
 * 
 * 
 * @author Darkness
 * @date 2013-3-30 下午03:56:36 
 * @version V1.0
 */
public class DataTableConvertor implements IJSONConvertor {
	
	public String getTypeID() {
		return "DataTable";
	}

	@Override
	public String getExtendItemID() {
		return getTypeID();
	}

	@Override
	public String getExtendItemName() {
		return getExtendItemID();
	}

	public boolean match(Object obj) {
		return obj instanceof DataTable;
	}

	public static String convert(Object obj) {
		return new DataTableConvertor().toJSON(obj).toJSONString();
	}

	public JSONObject toJSON(Object obj) {
		JSONObject jo = new JSONObject();
		DataTable dt = (DataTable) obj;
		jo.put("Columns", dt.getDataColumns());
		Object[][] vs = new Object[dt.getRowCount()][dt.getColumnCount()];
		for (int i = 0; i < dt.getColumnCount(); i++) {
			Object[] columns = dt.getColumnValues(i);
			for (int j = 0; j < dt.getRowCount(); j++) {
				vs[j][i] = columns[j];
			}
		}
		jo.put("Values", vs);
		jo.put("@type", "DataTable");
		return jo;
	}

	public static DataTable reverse(JSONObject map) {
		return (DataTable) new DataTableConvertor().fromJSON(map);
	}

	public Object fromJSON(JSONObject map) {
		JSONArray columns = (JSONArray) map.get("Columns");
		JSONArray values = (JSONArray) map.get("Values");
		DataColumn[] dcs = new DataColumn[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			dcs[i] = new DataColumn();
			JSONObject col = (JSONObject) columns.get(i);
			dcs[i].setColumnName((String) col.get("Name"));
			dcs[i].setColumnType(((Integer) col.get("Type")).intValue());
		}

		Object[][] vs = (Object[][]) null;
		if (values.size() > 0) {
			vs = new Object[values.size()][dcs.length];
			for (int i = 0; i < values.size(); i++) {
				vs[i] = ((JSONArray) values.get(i)).toArray();
			}
		}
		return new DataTable(dcs, vs);
	}
}