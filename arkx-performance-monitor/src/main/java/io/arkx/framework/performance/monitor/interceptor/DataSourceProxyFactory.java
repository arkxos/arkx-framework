package io.arkx.framework.performance.monitor.interceptor;

import java.lang.reflect.Proxy;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import io.arkx.framework.performance.monitor.TraceRecorder;
import io.arkx.framework.performance.monitor.config.MonitorConfigService;
import io.arkx.framework.performance.monitor.sql.handler.DataSourceProxyHandler;

/**
 * @author Nobody
 * @date 2025-06-06 21:15
 * @since 1.0
 */
public class DataSourceProxyFactory {

    private MonitorConfigService monitorConfigService;
    private TraceRecorder traceRecorder;

    @Autowired
    public void setDependencies(@Lazy MonitorConfigService monitorConfigService, @Lazy TraceRecorder traceRecorder) {
        this.monitorConfigService = monitorConfigService;
        this.traceRecorder = traceRecorder;
    }

    public DataSource createMonitoredDataSource(DataSource realDataSource) {
        return (DataSource) Proxy.newProxyInstance(DataSource.class.getClassLoader(), new Class[]{DataSource.class},
                new DataSourceProxyHandler(realDataSource, monitorConfigService, traceRecorder));
    }
}
