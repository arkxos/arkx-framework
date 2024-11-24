package com.rapidark.cloud.platform.common.core.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @Author: linrongxin
 * @Date: 2019/9/19 17:49
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.data.redisson", name = "enabled", havingValue = "true")
public class RedissonConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private String port;

	@Value("${spring.data.redis.password}")
	private String password;

	private int timeout = 3000;

	private int connectionPoolSize = 64;

	private int connectionMinimumIdleSize = 10;

	/**
	 * 单机配置
	 *
	 * @return
	 */
	@Bean
	RedissonClient redissonSingle() {
		Config config = new Config();
		SingleServerConfig serverConfig = config.useSingleServer()
				.setAddress("redis://" + host + ":" + port)
				.setTimeout(timeout)
				.setConnectionPoolSize(connectionPoolSize)
				.setConnectionMinimumIdleSize(connectionMinimumIdleSize);
		//密码设置
		if (StringUtils.hasText(password)) {
			serverConfig.setPassword(password);
		}
		return Redisson.create(config);
	}
}
