package io.arkx.framework.performance.monitor.interceptor;

import io.arkx.framework.performance.monitor.SystemMonitor;
import io.arkx.framework.performance.monitor.web.RequestLifecycleManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author Nobody
 * @date 2025-06-06 1:04
 * @since 1.0
 */
@Component
public class RequestMonitoringInterceptor implements HandlerInterceptor {

	private static final String REQUEST_RECEIVED_TIME = "monitor.requestReceivedTime";
	private static final String START_PROCESSING_TIME = "monitor.startProcessingTime";

	private final RequestLifecycleManager lifecycleManager;
	private final SystemMonitor systemMonitor;

	@Autowired
	public RequestMonitoringInterceptor(RequestLifecycleManager lifecycleManager,
										SystemMonitor systemMonitor) {
		this.lifecycleManager = lifecycleManager;
		this.systemMonitor = systemMonitor;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		// 记录请求进入系统的时间
		long requestReceivedTime = System.currentTimeMillis();
		request.setAttribute(REQUEST_RECEIVED_TIME, requestReceivedTime);

		lifecycleManager.startRequest(request);

		// 记录开始处理时间
		long startProcessingTime = System.currentTimeMillis();
		request.setAttribute(START_PROCESSING_TIME, startProcessingTime);

		systemMonitor.onRequestStart();
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
								Object handler, Exception ex) {
		lifecycleManager.endRequest(request);

		// 获取时间戳
		Long startProcessingTime = (Long) request.getAttribute(START_PROCESSING_TIME);
		Long requestReceivedTime = (Long) request.getAttribute(REQUEST_RECEIVED_TIME);

		if (startProcessingTime != null) {
			long endTime = System.currentTimeMillis();
			long processingTime = endTime - startProcessingTime;

			// 计算延迟（从接收到开始处理的时间）
			long latency = requestReceivedTime != null ?
					startProcessingTime - requestReceivedTime : 0;

			systemMonitor.onRequestEnd(latency, processingTime);
		}
	}
}