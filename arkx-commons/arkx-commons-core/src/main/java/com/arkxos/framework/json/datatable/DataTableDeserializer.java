package com.arkxos.framework.json.datatable;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.DataTypes;

/**
 * 
 * @author darkness
 * @date 2018-10-09 22:41:04
 * @version 1.0
 * @since 4.0
 */
public class DataTableDeserializer implements ObjectDeserializer {
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		// String testEnumString = parser.parseObject(String.class);
		// return (T)TestEnum.fromName(testEnumString);
		
		JSONObject map = parser.parseObject();
		
		JSONArray columns = map.getJSONArray("Columns");
		JSONArray values = map.getJSONArray("Values");
		DataColumn[] dcs = new DataColumn[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			dcs[i] = new DataColumn();
			JSONObject col = columns.getJSONObject(i);
			dcs[i].setColumnName(col.getString("Name"));
			dcs[i].setColumnType(DataTypes.valueOf(col.getIntValue("Type")));
		}

		Object[][] vs = new Object[0][0];
		if (values.size() > 0) {
			vs = new Object[values.size()][dcs.length];
			for (int i = 0; i < values.size(); i++) {
				vs[i] = values.getJSONArray(i).toArray();
			}
		}
		DataTable dataTable = new DataTable(dcs, vs);
		return (T)dataTable;
	}

	@Override
	public int getFastMatchToken() {
		return 0;
	}
}
