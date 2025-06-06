package io.arkx.framework.performance.monitor.sql.handler;

import io.arkx.framework.performance.monitor.TraceRecorder;
import io.arkx.framework.performance.monitor.config.MonitorConfig;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @author Nobody
 * @date 2025-06-06 0:41
 * @since 1.0
 */
// 数据源代理处理器
public class DataSourceProxyHandler implements InvocationHandler {

	private final DataSource realDataSource;
	private final MonitorConfig config;
	private final TraceRecorder recorder;

	public DataSourceProxyHandler(DataSource realDataSource, MonitorConfig config,
								  TraceRecorder recorder) {
		this.realDataSource = realDataSource;
		this.config = config;
		this.recorder = recorder;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if ("getConnection".equals(method.getName())) {
			// 拦截获取连接操作
			Connection realConn = (Connection) method.invoke(realDataSource, args);
			return Proxy.newProxyInstance(
					Connection.class.getClassLoader(),
					new Class[]{Connection.class},
					new ConnectionProxyHandler(realConn, config, recorder)
			);
		}
		return method.invoke(realDataSource, args);
	}
}
