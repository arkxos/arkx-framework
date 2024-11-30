package com.rapidark.framework.boot.spring;

import java.util.Collection;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring bean holder
 * @author Darkness
 * @date 2020年11月3日 下午4:18:39
 * @version V1.0
 */
@Component
public class IocBeanRegister implements ApplicationContextAware {

	private static IocBeanRegister instance = new IocBeanRegister();
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String id) {
		return (T)instance.applicationContext.getBean(id);
	}
	
	public static <T> T getBean(Class<T> clazz) {
		return instance.applicationContext.getBean(clazz);
	}
	
	public static <T> Collection<T> getBeansOfType(Class<T> clazz) {
		return instance.applicationContext.getBeansOfType(clazz).values();
	}
	
	private ApplicationContext applicationContext;
	
	private IocBeanRegister() {
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
