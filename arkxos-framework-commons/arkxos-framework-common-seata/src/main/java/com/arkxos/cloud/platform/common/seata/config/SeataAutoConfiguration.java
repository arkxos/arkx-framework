package com.arkxos.cloud.platform.common.seata.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.arkxos.common.core.factory.YamlPropertySourceFactory;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;

/**
 * Seata 配置类
 *
 * @author lengleng
 * @date 2022/3/29
 */
@PropertySource(value = "classpath:seata-config.yml", factory = YamlPropertySourceFactory.class)
@EnableAutoDataSourceProxy(useJdkProxy = true)
@Configuration(proxyBeanMethods = false)
public class SeataAutoConfiguration {

}
