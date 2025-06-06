package io.arkx.framework.performance.monitor.interceptor;

import io.arkx.framework.performance.monitor.TraceRecorder;
import io.arkx.framework.performance.monitor.config.MonitorConfig;
import io.arkx.framework.performance.monitor.sql.handler.DataSourceProxyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
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
@Component
public class SqlInterceptor {

	private final DataSource dataSource;
	private final MonitorConfig config;
	private final TraceRecorder recorder;

	public SqlInterceptor(@Lazy DataSource dataSource, MonitorConfig config,
						  TraceRecorder recorder) {
		this.dataSource = dataSource;
		this.config = config;
		this.recorder = recorder;
	}

	// 创建代理数据源
	@Bean
	public DataSource monitoredDataSource() {
		return (DataSource) Proxy.newProxyInstance(
				DataSource.class.getClassLoader(),
				new Class[]{DataSource.class},
				new DataSourceProxyHandler(dataSource, config, recorder)
		);
	}
}