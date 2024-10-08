package com.rapidark.cloud.gateway.server.configuration;

import com.rapidark.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/7 14:26
 */
@Slf4j
@Configuration
public class RedisConfig {

    @Bean(name = {"redisTemplate", "stringRedisTemplate"})
    public StringRedisTemplate stringRedisTemplate(@Qualifier("j2CahceRedisConnectionFactory") RedisConnectionFactory factory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(RedisUtils.class)
    @ConditionalOnBean(StringRedisTemplate.class)
    public RedisUtils redisUtils(StringRedisTemplate stringRedisTemplate) {
        RedisUtils redisUtils = new RedisUtils(stringRedisTemplate);
        log.info("RedisUtils [{}]", redisUtils);
        return redisUtils;
    }

}
