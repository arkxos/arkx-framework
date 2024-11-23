//package com.rapidark.cloud.platform.gateway.framework.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
///**
// * @Description 配置redis
// * @Author JL
// * @Date 2022/11/19
// * @Version V2.0
// */
//@Configuration
//public class RedisConfig {
//
//    @Bean(name = {"redisTemplate", "stringRedisTemplate"})
//    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
//        return new StringRedisTemplate(factory);
//    }
//}
