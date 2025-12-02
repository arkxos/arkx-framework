package io.arkx.framework.jackson;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Nobody
 * @date 2025-05-16 20:28
 * @since 1.0
 */
@Component
@Primary
public class ArkObjectMapper extends ObjectMapper implements InitializingBean {

	private final List<ArkObjectMapperConfig> configs;

	public ArkObjectMapper(List<ArkObjectMapperConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void afterPropertiesSet() {
		// this.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
		// ObjectMapper.DefaultTyping.NON_FINAL);
		// 启用自动检测序列化器等设置
		// this.enable(SerializationFeature.FAIL_ON_EMPTY_BEANS.USE_EQUALITY_FOR_NULLS);
		for (ArkObjectMapperConfig config : configs) {
			config.configure(this);
		}
	}

}
