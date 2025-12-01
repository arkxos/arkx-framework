package io.arkx.framework.performance.monitor.interceptor;

import io.arkx.framework.performance.monitor.SystemMonitor;
import io.arkx.framework.performance.monitor.web.RequestLifecycleManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * spring controller监控
 *
 * @author Darkness
 * @date 2013-6-20 下午08:52:15
 * @version V1.0
 */
@Slf4j
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

	/**
	 * 在业务处理器处理请求之前被调用 如果返回false 从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链
	 *
	 * 如果返回true 执行下一个拦截器,直到所有的拦截器都执行完毕 再执行被拦截的Controller 然后进入拦截器链,
	 * 从最后一个拦截器往回执行所有的postHandle() 接着再从最后一个拦截器往回执行所有的afterCompletion()
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//		String name = "";
//		if(handler instanceof HandlerMethod) {
//			HandlerMethod handlerMethod = (HandlerMethod)handler;
//			String clazzName = handlerMethod.getMethod().getDeclaringClass().getName();
//			String methodName = handlerMethod.getMethod().getName();
//			name = clazzName + "." + methodName;
//		} else if(handler instanceof ResourceHttpRequestHandler) {
//			//ResourceHttpRequestHandler handlerMethod = (ResourceHttpRequestHandler)handler;
//			name = request.getRequestURI();
//		} else {
//			name = handler.toString();
//		}
//		log.debug("RequestMonitoringInterceptor preHandle: {}", name);

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