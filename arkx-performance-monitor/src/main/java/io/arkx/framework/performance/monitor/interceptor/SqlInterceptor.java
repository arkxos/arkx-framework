package io.arkx.framework.performance.monitor.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nobody
 * @date 2025-06-06 0:40
 * @since 1.0
 */
/* ====================== SQL代理系统 ====================== */
@Slf4j
@Configuration
public class SqlInterceptor {

    @Bean
    public static DataSourcePostProcessor dataSourcePostProcessor() {
        return new DataSourcePostProcessor();
    }

    @Bean
    public DataSourceProxyFactory dataSourceProxyFactory() {
        return new DataSourceProxyFactory();
    }

}
