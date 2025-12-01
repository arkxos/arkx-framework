package io.arkx.framework.data.fastjson;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import io.arkx.framework.commons.collection.DataTypes;

import java.lang.reflect.Type;

public class DataTypesDeserializer implements ObjectDeserializer {

	@SuppressWarnings("unchecked")
	@Override
    public <T> T deserialze(DefaultJSONParser parser, Type type,
            Object fieldName) {
        JSONLexer lexer = parser.getLexer();
        int value = lexer.intValue();
        return (T) DataTypes.valueOf(value);
    }

	@Override
    public int getFastMatchToken() {
        return 0;
    }
}