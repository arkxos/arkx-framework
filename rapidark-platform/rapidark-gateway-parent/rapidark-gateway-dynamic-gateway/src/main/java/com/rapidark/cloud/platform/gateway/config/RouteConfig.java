package com.rapidark.cloud.platform.gateway.config;


import com.rapidark.cloud.platform.gateway.framework.config.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import jakarta.annotation.Resource;

/**
 * @Description 配置路由规则
 * @Author JL
 * @Date 2020/04/27
 * @Version V1.0
 */
@Slf4j
@Configuration
public class RouteConfig {

    @Resource
    private ApplicationContextProvider applicationContextProvider;

    /**
     * 项目启动时,初始化RouteLocator对象
     * @param builder
     * @return
     */
    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RouteLocator routeLocatorObject(RouteLocatorBuilder builder){
        return builder.routes().build();
    }

    /**
     * 对请求基于ip的访问进行限流
     *
     * @return
     */
    @Primary
    @Deprecated
    @Bean("hostAddrKeyResolver")
    public KeyResolver hostAddrKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

    /**
     * 根据uri限流
     *
     * @return
     */
    @Deprecated
    @Bean("uriKeyResolver")
    public KeyResolver uriKeyResolver() {
        return (exchange) -> {
            return Mono.just(exchange.getRequest().getURI().getPath());
        };
    }

    /**
     * 根据user限流
     *
     * @return
     */
    @Deprecated
    @Bean("requestIdKeyResolver")
    KeyResolver requestIdKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("requestId"));
    }

    /**
     * 限流参数
     * @param defaultReplenishRate 每1秒限制请求数(令牌数)
     * @param defaultBurstCapacity 令牌桶的容量
     * @return
     */
    @Deprecated
    public RedisRateLimiter redisRateLimiter(int defaultReplenishRate, int defaultBurstCapacity){
        RedisRateLimiter redisRateLimiter= new RedisRateLimiter(defaultReplenishRate, defaultBurstCapacity);
        redisRateLimiter.setApplicationContext(applicationContextProvider.getApplicationContext());
        return redisRateLimiter;
    }

}