package io.arkx.framework.performance.monitor.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Nobody
 * @date 2025-06-06 0:43
 * @since 1.0
 */
/* ====================== 上下文辅助类 ====================== */
@Component
public  class ApplicationContextHolder implements ApplicationContextAware {

	private static final AtomicReference<ApplicationContext> CONTEXT_REF =
			new AtomicReference<>();

	@Override
	public void setApplicationContext(ApplicationContext context) {
		CONTEXT_REF.compareAndSet(null, context);
	}

	public static <T> T getBean(Class<T> beanClass) {
		ApplicationContext context = CONTEXT_REF.get();
		if (context != null) {
			return context.getBean(beanClass);
		}
		throw new IllegalStateException("Application context not initialized");
	}

	public static boolean isReady() {
		return CONTEXT_REF.get() != null;
	}

}
