package io.arkx.framework.performance.monitor.interceptor;

import io.arkx.framework.performance.monitor.TraceContext;
import io.arkx.framework.performance.monitor.TraceRecorder;
import io.arkx.framework.performance.monitor.config.MonitorConfig;
import io.arkx.framework.performance.monitor.config.MonitorConfigService;
import io.arkx.framework.performance.monitor.config.PerformanceMonitorConfiguration;
import io.arkx.framework.performance.monitor.model.TraceNode;
import io.arkx.framework.performance.monitor.util.ApplicationContextHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * 方法监控切面
 * @author Nobody
 * @date 2025-06-06 0:42
 * @since 1.0
 */
@Slf4j
@Aspect
@Component
public class MethodMonitorAspect {

	// 执行统计
	private final LongAdder monitoredCount = new LongAdder();
	private final LongAdder skippedCount = new LongAdder();

	// 添加排除列表，不监控配置类
//	private static final List<Class<?>> EXCLUDED_CLASSES = Arrays.asList(
//			MonitorConfig.class,
//			PerformanceMonitorConfiguration.class
//	);

//	private MonitorConfig config;

//	public MethodMonitorAspect(MonitorConfig config, MonitorConfigService configService, TraceRecorder recorder) {
//		this.config = config;
//	}

	private MonitorConfig config() {
//		if (config != null) {
//			return config;
//		}

//		this.config = ApplicationContextHolder.getBean(MonitorConfig.class);
//		return config;

		return  ApplicationContextHolder.getBean(MonitorConfig.class);
	}

	private MonitorConfigService monitorConfigService() {
		return ApplicationContextHolder.getBean(MonitorConfigService.class);
	}

	private TraceContext traceContext() {
		return ApplicationContextHolder.getBean(TraceContext.class);
	}

	private TraceRecorder recorder() {
		return ApplicationContextHolder.getBean(TraceRecorder.class);
	}

	public MethodMonitorAspect() {}

	// 每5分钟输出执行统计
	@Scheduled(fixedRate = 5 * 60_000)
	public void logStats() {
		long monitored = monitoredCount.sum();
		long skipped = skippedCount.sum();
		long total = monitored + skipped;
		double ratio = total > 0 ? (monitored * 100.0 / total) : 0;

		log.info("Method monitoring stats: Monitored={} ({}%), Skipped={}",
				monitored, String.format("%.1f", ratio), skipped);

		// 重置计数器
		monitoredCount.reset();
		skippedCount.reset();
	}

//	@Around("execution(* *(..))")
//    @Around("execution(* *.*(..)) && " +
//		"!within(org.springframework..*) && " +
//		"!within(javax..*)")
	@Around("execution(public !static * *(..)) && " +
			"!within(io.arkx.framework.performance.monitor..*) && " +
			"!target(org.springframework.beans.factory.config.BeanPostProcessor) && " +

			"!within(org.springframework..*) && " +
			"!within(javax..*) && " +
			"!within(java..*) && " +
			"!within(com.hazelcast..*)")
	public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable {
		// 检查是否在排除列表中
//		if (EXCLUDED_CLASSES.contains(joinPoint.getTarget().getClass())) {
//			return joinPoint.proceed();
//		}

//		if (FactoryBean.class.isAssignableFrom(joinPoint.getTarget().getClass())) {
//			return joinPoint.proceed();
//		}

		// 先检查类类型
		if (isFinalClass(joinPoint.getTarget().getClass())) {
			return joinPoint.proceed();
		}

		// 关键点1：仅在上下文就绪时执行
		if (!ApplicationContextHolder.isReady()) {
			return joinPoint.proceed();
		}

		// 检查监控是否启用
		if (!config().isEnabled()) {
			return joinPoint.proceed();
		}

		// 获取方法信息
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		String className = signature.getDeclaringType().getName();
		String methodName = signature.getName();
		String methodKey = className + "#" + methodName;

		// 是否应该监控此方法
		if (!monitorConfigService().shouldMonitorMethod(className, methodName)) {
			skippedCount.increment();
			return joinPoint.proceed();
		}

		// 采样决策
		if (!monitorConfigService().shouldSample(methodKey)) {
			skippedCount.increment();
			return joinPoint.proceed();
		}

		// 创建跟踪节点
		TraceNode node = createTraceNode(joinPoint, className, methodName);
		TraceNode pushedNode = traceContext().pushNode(node);
		if (pushedNode == null) {
			// 超过最大深度
			skippedCount.increment();
			return joinPoint.proceed();
		}

		try {
			monitoredCount.increment();
			Object result = joinPoint.proceed();
			node.setSuccess(true);
			return result;
		} catch (Throwable t) {
			node.setSuccess(false);
			node.setErrorMessage(t.getMessage());
			throw t;
		} finally {
			// 完成节点
			node.setEndTime(System.nanoTime());
			node.complete();
			traceContext().popNode();

			// 记录慢方法
			if (node.getDuration() > config().getSlowThreshold() * 1_000_000) {
				log.warn("Slow method detected: {}.{} ({}ms)",
						className, methodName,
						TimeUnit.NANOSECONDS.toMillis(node.getDuration()));
			}

			// 记录节点
			recorder().record(node);
		}
	}

	// 检查最终类逻辑
	private boolean isFinalClass(Class<?> clazz) {
		// 1. 类本身是否声明为 final
		if (Modifier.isFinal(clazz.getModifiers())) {
			return true;
		}

		return false;
	}

	// 创建方法跟踪节点
	private TraceNode createTraceNode(ProceedingJoinPoint pjp, String className, String methodName) {
		MethodSignature signature = (MethodSignature) pjp.getSignature();

		TraceNode node = new TraceNode();
		node.setType("METHOD");
		node.setClassName(className);
		node.setMethodName(methodName);
		node.setSignature(signature.toString());
		node.setStartTime(System.nanoTime());

		// 尝试获取HTTP请求信息
		if (RequestContextHolder.getRequestAttributes() != null) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			node.setSessionId(request.getSession(false) != null ?
					request.getSession().getId() : null);
		}

		return node;
	}
}