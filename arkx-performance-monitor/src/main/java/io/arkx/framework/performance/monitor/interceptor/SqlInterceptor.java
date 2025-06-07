package io.arkx.framework.performance.monitor.interceptor;

import io.arkx.framework.performance.monitor.TraceRecorder;
import io.arkx.framework.performance.monitor.config.MonitorConfig;
import io.arkx.framework.performance.monitor.config.MonitorConfigService;
import io.arkx.framework.performance.monitor.sql.handler.DataSourceProxyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;

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