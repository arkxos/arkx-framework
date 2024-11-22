package com.rapidark.cloud.platform.gateway.filter.factory;

import com.rapidark.cloud.platform.gateway.filter.CacheResultGatewayFilter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @Description 自定义response响应结果缓存过滤器工厂
 * @Author JL
 * @Date 2023/10/11
 * @Version V1.0
 */
@Slf4j
@Component
public class CacheResultGatewayFilterFactory extends AbstractGatewayFilterFactory<CacheResultGatewayFilterFactory.Config> {

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    /**
     在yml中配置
     filters:
     # 这种配置方式和spring cloud gateway内置的GatewayFilterFactory一致
     - name=CacheResult
       args:
         ttl: 7
         unit: d
     */
    public CacheResultGatewayFilterFactory() {
        super(Config.class);
        log.info("Loaded GatewayFilterFactory [CacheResult]");
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("ttl");
    }

    /**
     * 核心执行方法
     * @param config
     * @return
     */
    @Override
    public GatewayFilter apply(Config config) {
        return new CacheResultGatewayFilter(config.ttl, redisTemplate);
    }

    @Data
    public static class Config {
        protected long ttl = 0;
    }

}
