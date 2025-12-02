package io.arkx.framework.enums.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.arkx.framework.enums.core.mvc.JsonEnumConverter;
import io.arkx.framework.enums.core.mvc.MvcConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author zhuCan
 * @description
 * @since 2022-11-09 10:06
 **/
@AutoConfiguration
public class EnumsCoreAutoConfiguration {

	@Bean
	@ConditionalOnBean(ObjectMapper.class)
	public JsonEnumConverter jsonEnumConverter(MappingJackson2HttpMessageConverter httpMessageConverter,
			ObjectMapper objectMapper) {
		return new JsonEnumConverter(httpMessageConverter, objectMapper);
	}

	@Bean
	@ConditionalOnMissingBean(MappingJackson2HttpMessageConverter.class)
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		return new MappingJackson2HttpMessageConverter();
	}

	@Bean
	@ConditionalOnClass(WebMvcConfigurer.class)
	public MvcConfiguration mvcConfiguration() {
		return new MvcConfiguration();
	}

}
