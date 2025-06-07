package io.arkx.framework.performance.monitor.interceptor;

import org.springframework.aop.TargetSource;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Modifier;

/**
 * @author Nobody
 * @date 2025-06-06 20:26
 * @since 1.0
 */

/**
 * 自定义自动代理创建器，用于跳过 final 类的代理生成
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
//@Component
public class FinalSafeAnnotationAwareAspectJAutoProxyCreator extends AnnotationAwareAspectJAutoProxyCreator {

	public FinalSafeAnnotationAwareAspectJAutoProxyCreator() {
		System.out.println("---------");
	}

	/**
	 * 返回适用于当前 Bean 的所有切面或拦截器
	 */
	@Override
	protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, TargetSource customTargetSource) throws BeansException {
		// 如果是 final 类或特定包下的类，则不应用任何切面
		if (Modifier.isFinal(beanClass.getModifiers())) {
			return DO_NOT_PROXY; // 表示不要代理该 Bean
		}

		// 否则返回 null，表示使用 Spring 默认的切面匹配机制
		return null;
	}

	@Override
	protected boolean shouldSkip(Class<?> beanClass, String beanName) {
		boolean skip = super.shouldSkip(beanClass, beanName);
		if (skip) {
			return skip;
		}
		return Modifier.isFinal(beanClass.getModifiers());
	}
}
