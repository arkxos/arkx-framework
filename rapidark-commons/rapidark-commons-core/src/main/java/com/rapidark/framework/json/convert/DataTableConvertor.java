package com.rapidark.framework.json.convert;

import java.math.BigDecimal;
import java.util.Date;

import com.rapidark.framework.commons.collection.DataColumn;
import com.rapidark.framework.commons.collection.DataRow;
import com.rapidark.framework.commons.collection.DataTable;
import com.rapidark.framework.commons.collection.DataTypes;
import com.rapidark.framework.commons.util.DateUtil;
import com.rapidark.framework.commons.util.StringUtil;
import com.rapidark.framework.json.JSONArray;
import com.rapidark.framework.json.JSONObject;

/**
 * DataTable的JSON转换器
 * 
 */
public class DataTableConvertor implements IJSONConvertor {

	@Override
	public String getExtendItemID() {
		return "DataTable";
	}

	@Override
	public String getExtendItemName() {
		return getExtendItemID();
	}

	@Override
	public boolean match(Object obj) {
		return obj instanceof DataTable;
	}

	@Override
	public JSONObject toJSON(Object obj) {
		JSONObject jo = new JSONObject();
		DataTable dt = (DataTable) obj;
		jo.put("Columns", dt.getDataColumns());
		Object[][] vs = new Object[dt.getRowCount()][dt.getColumnCount()];
		for (int j = 0; j < dt.getRowCount(); j++) {
			DataRow dr = dt.getDataRow(j);
			vs[j] = dr.getDataValues();
			if (j == 0) {
				for (int i = 0; i < dr.getColumnCount(); i++) {
					if ((dr.get(i) instanceof Date)) {
						dr.getDataColumn(i).setColumnType(DataTypes.DATETIME);
					}
				}
			}
		}
		for (int i = 0; i < dt.getColumnCount(); i++) {
			DataColumn dc = dt.getDataColumn(i);
			if (dc.getColumnType() == DataTypes.DATETIME) {// 日期字段应该自动格式化
				for (int j = 0; j < dt.getRowCount(); j++) {
					Object v = vs[j][i];
					if (v instanceof Date) {
						if (StringUtil.isNotEmpty(dc.getDateFormat())) {
							v = DateUtil.toString((Date) v, dc.getDateFormat());
						} else {
							v = DateUtil.toDateTimeString((Date) v);
						}
					}
					vs[j][i] = v;
				}
			} else if (dc.getColumnType() == DataTypes.BIGDECIMAL) {// 日期字段应该自动格式化
				for (int j = 0; j < dt.getRowCount(); j++) {
					Object v = vs[j][i];
					if (v instanceof BigDecimal) {
						v = ((BigDecimal)v).toString();
					}
					vs[j][i] = v;
				}
			}
		}

		jo.put("Values", vs);
		jo.put("@type", "DataTable");
		return jo;
	}

	@Override
	public Object fromJSON(JSONObject map) {
		JSONArray columns = map.getJSONArray("Columns");
		JSONArray values = map.getJSONArray("Values");
		DataColumn[] dcs = new DataColumn[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			dcs[i] = new DataColumn();
			JSONObject col = columns.getJSONObject(i);
			dcs[i].setColumnName(col.getString("Name"));
			dcs[i].setColumnType(DataTypes.valueOf(col.getInt("Type")));
		}

		Object[][] vs = null;
		if (values.size() > 0) {
			vs = new Object[values.size()][dcs.length];
			for (int i = 0; i < values.size(); i++) {
				vs[i] = values.getJSONArray(i).toArray();
			}
		}
		return new DataTable(dcs, vs);
	}

}
