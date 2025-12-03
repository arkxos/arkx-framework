package io.arkx.framework.performance.monitor.sql.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import io.arkx.framework.performance.monitor.TraceRecorder;
import io.arkx.framework.performance.monitor.config.MonitorConfigService;
import io.arkx.framework.performance.monitor.util.ApplicationContextHolder;

/**
 * @author Nobody
 * @date 2025-06-06 0:41
 * @since 1.0
 */
// 连接代理处理器
public class ConnectionProxyHandler implements InvocationHandler {

    private final Connection realConnection;

    private final MonitorConfigService configService;

    private final TraceRecorder recorder;

    public ConnectionProxyHandler(Connection realConnection, MonitorConfigService configService,
            TraceRecorder recorder) {
        this.realConnection = realConnection;
        this.configService = configService;
        this.recorder = recorder;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 重要：处理 Object 的基础方法
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(realConnection, args);
        }

        if (ApplicationContextHolder.notReady()) {
            return method.invoke(realConnection, args);
        }

        String methodName = method.getName();
        boolean isExecuteMethod = methodName.startsWith("execute") || methodName.startsWith("prepare")
                || methodName.startsWith("create");

        // 拦截语句创建方法
        if ("prepareStatement".equals(method.getName()) && args != null && args.length > 0) {
            PreparedStatement stmt = (PreparedStatement) method.invoke(realConnection, args);
            String sql = (String) args[0];
            return createMonitoredStatement(stmt, sql);
        }
        if ("createStatement".equals(method.getName())) {
            Statement stmt = (Statement) method.invoke(realConnection, args);
            return createMonitoredStatement(stmt, "");
        }

        return method.invoke(realConnection, args);
    }

    // 创建监视语句
    private Object createMonitoredStatement(Statement stmt, String sql) {
        return Proxy.newProxyInstance(Statement.class.getClassLoader(),
                new Class[]{Statement.class, PreparedStatement.class},
                new StatementProxyHandler(stmt, sql, configService, recorder));
    }

}
