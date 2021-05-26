package com.opencloud.common.filter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.opencloud.common.utils.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;

/**
 * @author liuyadu
 */
public class XssStringJsonSerializer extends JsonSerializer<String> {
    @Override
    public Class<String> handledType() {
        return String.class;
    }

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value != null) {
            String encodedValue = StringEscapeUtils.escapeHtml4(value);
            encodedValue = StringUtils.stripXss(encodedValue).trim();
            jsonGenerator.writeString(encodedValue);
        }
    }
}