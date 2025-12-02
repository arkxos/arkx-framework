package io.arkx.framework.performance.monitor.interceptor;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nobody
 * @date 2025-06-06 21:24
 * @since 1.0
 */
@Slf4j
public class DataSourcePostProcessor implements BeanPostProcessor {

	@Autowired
	private DataSourceProxyFactory proxyFactory;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		if (bean instanceof DataSource) {
			log.debug("Creating SQL monitoring proxy for datasource " + beanName);
			// 根据配置决定是否代理
			if (shouldProxy(beanName)) {
				return proxyFactory.createMonitoredDataSource((DataSource) bean);
			}
		}
		return bean;
	}

	private boolean shouldProxy(String beanName) {
		// 从配置或环境变量判断
		return "dataSource".equals(beanName) || "primaryDataSource".equals(beanName);
	}

}
