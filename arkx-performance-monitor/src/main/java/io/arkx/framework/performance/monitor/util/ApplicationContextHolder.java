package io.arkx.framework.performance.monitor.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Nobody
 * @date 2025-06-06 0:43
 * @since 1.0
 */
/* ====================== 上下文辅助类 ====================== */
@Component
public  class ApplicationContextHolder {

	private static ApplicationContext context;

	@Autowired
	public ApplicationContextHolder(ApplicationContext context) {
		ApplicationContextHolder.context = context;
	}

	public static <T> T getBean(Class<T> clazz) {
		return context.getBean(clazz);
	}

}
