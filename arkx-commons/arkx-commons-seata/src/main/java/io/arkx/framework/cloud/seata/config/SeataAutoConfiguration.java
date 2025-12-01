package io.arkx.framework.cloud.seata.config;

import io.arkx.framework.core.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Seata 配置类
 *
 * @author lengleng
 * @date 2022/3/29
 */
@PropertySource(value = "classpath:seata-config.yml", factory = YamlPropertySourceFactory.class)
@Configuration(proxyBeanMethods = false)
public class SeataAutoConfiguration {

}
