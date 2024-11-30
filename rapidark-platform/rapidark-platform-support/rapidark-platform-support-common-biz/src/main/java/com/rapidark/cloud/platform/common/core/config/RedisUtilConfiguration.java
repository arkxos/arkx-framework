package com.rapidark.cloud.platform.common.core.config;

import com.rapidark.framework.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@AutoConfiguration
public class RedisUtilConfiguration {

	public RedisUtilConfiguration() {
		System.out.println("---------------");
	}

	@Bean
	@ConditionalOnMissingBean(RedisUtils.class)
	@ConditionalOnBean(RedisTemplate.class)
	public RedisUtils<Object> redisUtils(RedisTemplate<String, Object> redisTemplate) {
		RedisUtils<Object> redisUtils = new RedisUtils<>(redisTemplate);
		log.info("RedisUtils [{}]", redisUtils);
		return redisUtils;
	}

}
