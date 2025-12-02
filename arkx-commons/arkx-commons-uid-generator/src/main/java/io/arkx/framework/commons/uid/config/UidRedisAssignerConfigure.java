package io.arkx.framework.commons.uid.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import io.arkx.framework.commons.uid.constant.Mode;
import io.arkx.framework.commons.uid.worker.RedisWorkerIdAssigner;
import io.arkx.framework.commons.uid.worker.WorkerIdAssigner;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DengJun 2021/5/10
 */
@Slf4j
@Configuration
@AutoConfigureAfter({RedisAutoConfiguration.class})
@ConditionalOnBean({RedisConnectionFactory.class})
@ConditionalOnProperty(value = "arkx.uid.assigner-mode", havingValue = Mode.REDIS)
public class UidRedisAssignerConfigure {
    /**
     * 基于Redis生成节点ID
     */
    @Bean
    @ConditionalOnBean({RedisTemplate.class})
    @ConditionalOnMissingBean(RedisWorkerIdAssigner.class)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public WorkerIdAssigner redisWorkerIdAssigner(RedisTemplate redisTemplate) {
        log.info("WorkerIdAssigner turn on redis");
        return new RedisWorkerIdAssigner(redisTemplate);
    }
}
