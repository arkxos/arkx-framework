package io.arkx.framework.performance.monitor2;

import io.arkx.framework.performance.monitor2.domain.PerformanceMonitor;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

/**
 * spring controller监控
 * 
 * @author Darkness
 * @date 2013-6-20 下午08:52:15
 * @version V1.0
 */
@Slf4j
@Component
public class ControllerMonitorInterceptor implements HandlerInterceptor {

	@Resource
	private PerformanceMonitor performanceMonitor;

	/**
	 * 在业务处理器处理请求之前被调用 如果返回false 从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链
	 * 
	 * 如果返回true 执行下一个拦截器,直到所有的拦截器都执行完毕 再执行被拦截的Controller 然后进入拦截器链,
	 * 从最后一个拦截器往回执行所有的postHandle() 接着再从最后一个拦截器往回执行所有的afterCompletion()
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//log.debug("==============执行顺序: 1、preHandle================");

		if (performanceMonitor.isSwitchOn()) {
			String name = "";
			if(handler instanceof HandlerMethod) {
				HandlerMethod handlerMethod = (HandlerMethod)handler;
				String clazzName = handlerMethod.getMethod().getDeclaringClass().getName();
				String methodName = handlerMethod.getMethod().getName();
				name = clazzName + "." + methodName;
			} else if(handler instanceof ResourceHttpRequestHandler) {
				//ResourceHttpRequestHandler handlerMethod = (ResourceHttpRequestHandler)handler;
				name = request.getRequestURI();
			} else {
				name = handler.toString();
			}
			performanceMonitor.start(name);
		}

		return true;
	}

	// 在业务处理器处理请求执行完成后,生成视图之前执行的动作
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		//log.debug("==============执行顺序: 2、postHandle================");
	}

	/**
	 * 在DispatcherServlet完全处理完请求后被调用
	 * 
	 * 当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion()
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		//log.debug("==============执行顺序: 3、afterCompletion================");
		if (performanceMonitor.isSwitchOn()) {
			performanceMonitor.stop();
		}
	}
	
}
