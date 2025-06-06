package io.arkx.framework.performance.monitor.interceptor;

import io.arkx.framework.performance.monitor.TraceContext;
import io.arkx.framework.performance.monitor.TraceRecorder;
import io.arkx.framework.performance.monitor.config.MonitorConfig;
import io.arkx.framework.performance.monitor.model.TraceNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
public  class MethodMonitorAspect {

	// 执行统计
	private final LongAdder monitoredCount = new LongAdder();
	private final LongAdder skippedCount = new LongAdder();

	private final MonitorConfig config;
	private final TraceContext traceContext;
	private final TraceRecorder recorder;

	public MethodMonitorAspect(MonitorConfig config, TraceContext traceContext,
							   TraceRecorder recorder) {
		this.config = config;
		this.traceContext = traceContext;
		this.recorder = recorder;
	}

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

	@Around("execution(* *(..))")
	public Object monitor(ProceedingJoinPoint pjp) throws Throwable {
		// 检查监控是否启用
		if (!config.isEnabled()) {
			return pjp.proceed();
		}

		// 获取方法信息
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		String className = signature.getDeclaringType().getName();
		String methodName = signature.getName();
		String methodKey = className + "#" + methodName;

		// 是否应该监控此方法
		if (!config.shouldMonitorMethod(className, methodName)) {
			skippedCount.increment();
			return pjp.proceed();
		}

		// 采样决策
		if (!config.shouldSample(methodKey)) {
			skippedCount.increment();
			return pjp.proceed();
		}

		// 创建跟踪节点
		TraceNode node = createTraceNode(pjp, className, methodName);
		TraceNode pushedNode = traceContext.pushNode(node);
		if (pushedNode == null) {
			// 超过最大深度
			skippedCount.increment();
			return pjp.proceed();
		}

		try {
			monitoredCount.increment();
			Object result = pjp.proceed();
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
			traceContext.popNode();

			// 记录慢方法
			if (node.getDuration() > config.getSlowThreshold() * 1_000_000) {
				log.warn("Slow method detected: {}.{} ({}ms)",
						className, methodName,
						TimeUnit.NANOSECONDS.toMillis(node.getDuration()));
			}

			// 记录节点
			recorder.record(node);
		}
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