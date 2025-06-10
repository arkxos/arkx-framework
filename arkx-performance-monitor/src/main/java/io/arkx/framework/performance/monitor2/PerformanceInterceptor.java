package io.arkx.framework.performance.monitor2;

import io.arkx.framework.performance.monitor2.annotation.IngorePerformanceLog;
import io.arkx.framework.performance.monitor2.domain.PerformanceMonitor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Darkness
 * @date 2013-7-22 下午04:46:39
 * @version V1.0
 */
@Slf4j
public class PerformanceInterceptor implements MethodInterceptor {
	
	@Resource
	private PerformanceMonitor performanceMonitor;

	public PerformanceInterceptor() {
		log.debug("PerformanceInterceptor init");
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		IngorePerformanceLog classIngorePerformanceLog = invocation.getMethod().getDeclaringClass().getAnnotation(IngorePerformanceLog.class);
		if(classIngorePerformanceLog != null) {
			return invocation.proceed();
		}
		IngorePerformanceLog ingorePerformanceLog = invocation.getMethod().getAnnotation(IngorePerformanceLog.class);
		if(ingorePerformanceLog != null) {
			return invocation.proceed();
		}
		
		if (performanceMonitor.isSwitchOn()) {
			String name = extractLogName(invocation);
			try { // 记录开始时间
				performanceMonitor.start(name);
				return invocation.proceed();
			} finally { // 记录方法结束时间
				performanceMonitor.stop();
			}
		} else {
			return invocation.proceed();
		}
	}
	
	public String extractLogName(MethodInvocation invocation) {
		return invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
	}

}
