package io.arkx.framework.boot.config.redis;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.arkx.commons.utils.StringUtils;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.lang.Assert;

/**
 * 重写序列化器
 *
 * @author /
 */
class StringRedisSerializer implements RedisSerializer<Object> {

	private final Charset charset;

	StringRedisSerializer() {
		this(StandardCharsets.UTF_8);
	}

	private StringRedisSerializer(Charset charset) {
		Assert.notNull(charset, "Charset must not be null!");
		this.charset = charset;
	}

	@Override
	public String deserialize(byte[] bytes) {
		return (bytes == null ? null : new String(bytes, charset));
	}

	@Override
	public byte[] serialize(Object object) {
		String string = JSON.toJSONString(object);
		if (StringUtils.isBlank(string)) {
			return null;
		}
		string = string.replace("\"", "");
		return string.getBytes(charset);
	}
}
