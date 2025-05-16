package com.arkxos.framework.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

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
		for (ArkObjectMapperConfig config : configs) {
			config.configure(this);
		}
	}

}
