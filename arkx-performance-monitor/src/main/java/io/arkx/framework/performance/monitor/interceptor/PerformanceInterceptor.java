package io.arkx.framework.performance.monitor.interceptor;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import io.arkx.framework.performance.monitor.TraceContext;
import io.arkx.framework.performance.monitor.TraceRecorder;
import io.arkx.framework.performance.monitor.annotation.IngorePerformanceLog;
import io.arkx.framework.performance.monitor.config.MonitorConfig;
import io.arkx.framework.performance.monitor.config.MonitorConfigService;
import io.arkx.framework.performance.monitor.model.TraceNode;
import io.arkx.framework.performance.monitor.util.ApplicationContextHolder;
import io.arkx.framework.performance.monitor.util.SignatureBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Darkness
 * @date 2013-7-22 下午04:46:39
 * @version V1.0
 */
@Slf4j
// @Component
public class PerformanceInterceptor implements MethodInterceptor {

	// @Resource
	private MonitorConfig monitorConfig() {
		return ApplicationContextHolder.getBean(MonitorConfig.class);
	}

	// @Resource
	private MonitorConfigService monitorConfigService() {
		return ApplicationContextHolder.getBean(MonitorConfigService.class);
	}

	// @Resource
	private TraceContext traceContext() {
		return ApplicationContextHolder.getBean(TraceContext.class);
	}

	// @Resource
	private TraceRecorder traceRecorder() {
		return ApplicationContextHolder.getBean(TraceRecorder.class);
	}

	public PerformanceInterceptor() {
		log.debug("PerformanceInterceptor init");
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (ApplicationContextHolder.notReady()) {
			return invocation.proceed();
		}

		// 获取方法信息
		Method method = invocation.getMethod();
		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		String signature = SignatureBuilder.build(className, methodName);

		log.debug("PerformanceInterceptor.invoke: {}", signature);
		IngorePerformanceLog classIngorePerformanceLog = invocation.getMethod()
			.getDeclaringClass()
			.getAnnotation(IngorePerformanceLog.class);
		if (classIngorePerformanceLog != null) {
			return invocation.proceed();
		}
		IngorePerformanceLog ingorePerformanceLog = invocation.getMethod().getAnnotation(IngorePerformanceLog.class);
		if (ingorePerformanceLog != null) {
			return invocation.proceed();
		}

		// 检查监控是否启用
		if (!monitorConfig().isEnabled()) {
			return invocation.proceed();
		}

		// 是否应该监控此方法
		if (!monitorConfigService().shouldMonitorMethod(className, methodName)) {
			return invocation.proceed();
		}

		// 采样决策
		if (!monitorConfigService().shouldSample(signature)) {
			return invocation.proceed();
		}

		// 创建跟踪节点
		TraceNode node = TraceNode.createMethodNode(method);
		TraceNode pushedNode = traceContext().pushNode(node);
		if (pushedNode == null) {
			return invocation.proceed();
		}

		try {
			Object result = invocation.proceed();
			node.setSuccess(true);
			return result;
		}
		catch (Throwable t) {
			node.setSuccess(false);
			node.setErrorMessage(t.getMessage());
			throw t;
		}
		finally {
			// 完成节点
			node.end();
			traceContext().popNode();

			// 记录慢方法
			if (node.getDuration() > monitorConfig().getSlowThreshold() * 1_000_000) {
				log.warn("Slow method detected: {}.{} ({}ms)", className, methodName,
						TimeUnit.NANOSECONDS.toMillis(node.getDuration()));
			}

			// 记录节点
			traceRecorder().record(node);
		}
	}

}
