package io.arkx.framework.performance.monitor.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author Nobody
 * @date 2025-06-06 0:43
 * @since 1.0
 */
@Slf4j
/* ====================== 上下文辅助类 ====================== */
@Component
public class ApplicationContextHolder
		implements
			ApplicationContextAware,
			ApplicationListener<ContextRefreshedEvent> {

	private static volatile boolean contextReady = false;
	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		context = applicationContext;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// 只在根应用上下文初始化完成时设置标志
		if (event.getApplicationContext().getParent() == null) {
			contextReady = true;
			log.info("Spring root context initialized");
		}
	}

	public static <T> T getBean(Class<T> beanClass) {
		if (context != null) {
			return context.getBean(beanClass);
		}
		throw new IllegalStateException("Application context not initialized");
	}

	public static boolean notReady() {
		return !contextReady;
	}

}
