package com.rapidark.cloud.platform.gateway.filter.factory;

import com.rapidark.cloud.platform.gateway.filter.ClientIdGatewayFilter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @Description 自定义clientId安全验证过滤器工厂
 * @Author JL
 * @Date 2020/05/27
 * @Version V1.0
 */
@Slf4j
@Component
public class SecureClientIdGatewayFilterFactory extends AbstractGatewayFilterFactory<SecureClientIdGatewayFilterFactory.Config> {

    /**
     在yml中配置
     filters:
     # 关键在下面一句，值为true则开启认证，false则不开启
     # 这种配置方式和spring cloud gateway内置的GatewayFilterFactory一致
     # 注意SecureClientId对应SecureClientIdGatewayFilterFactory，GatewayFilterFactory为默认固定后缀,gateway只匹配前缀
     - SecureClientId=true
     */

    public SecureClientIdGatewayFilterFactory() {
        super(Config.class);
        log.info("Loaded GatewayFilterFactory [SecureClientId]");
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("enabled");
    }

    /**
     * 核心执行方法
     * @param config
     * @return
     */
    @Override
    public GatewayFilter apply(Config config) {
        return new ClientIdGatewayFilter(config.isEnabled());
    }

    @Data
    public static class Config{
        // 控制是否开启认证
        private boolean enabled;
    }
}
