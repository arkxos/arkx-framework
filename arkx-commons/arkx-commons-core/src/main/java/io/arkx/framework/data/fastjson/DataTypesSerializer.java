package io.arkx.framework.data.fastjson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import io.arkx.framework.commons.collection.DataTypes;

import java.io.IOException;
import java.lang.reflect.Type;

public class DataTypesSerializer implements ObjectSerializer {

	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
			throws IOException {
		SerializeWriter out = serializer.getWriter();
		if (object == null) {
			serializer.getWriter().writeNull();
			return;
		}
		DataTypes dataTypes = (DataTypes) object;
		out.write(dataTypes.code());
	}
}