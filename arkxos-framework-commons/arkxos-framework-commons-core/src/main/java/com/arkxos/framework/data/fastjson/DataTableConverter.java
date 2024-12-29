package com.arkxos.framework.data.fastjson;

import java.io.IOException;
import java.lang.reflect.Type;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.json.JSON;

/**
 * @author Darkness
 * @date 2019-11-17 16:03:14
 * @version V1.0
 */
public class DataTableConverter implements ObjectSerializer {

	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
			throws IOException {
		SerializeWriter out = serializer.getWriter();
		if (object == null) {
			serializer.getWriter().writeNull();
			return;
		}
		DataTable dataTypes = (DataTable) object;
		out.write(JSON.toJSONString(dataTypes));
	}
	
}

