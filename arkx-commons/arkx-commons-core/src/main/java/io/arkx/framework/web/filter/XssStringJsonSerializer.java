package io.arkx.framework.web.filter;

import java.io.IOException;

import org.apache.commons.text.StringEscapeUtils;

import io.arkx.framework.commons.utils2.StringUtil;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author liuyadu
 */
public class XssStringJsonSerializer extends JsonSerializer<String> {

	@Override
	public Class<String> handledType() {
		return String.class;
	}

	@Override
	public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		if (value != null) {
			String encodedValue = StringEscapeUtils.escapeHtml4(value);
			encodedValue = StringUtil.stripXss(encodedValue).trim();
			jsonGenerator.writeString(encodedValue);
		}
	}

}
