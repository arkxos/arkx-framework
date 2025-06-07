package io.arkx.framework.performance.monitor.config;

/**
 * @author Nobody
 * @date 2025-06-07 22:42
 * @since 1.0
 */
import io.arkx.framework.performance.monitor.interceptor.FinalSafeAnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Configuration;

/**
 * @author Nobody
 * @date 2025-06-07 22:53
 * @since 1.0
 */
@Configuration
public class AopProxyCreatorOverride implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		// 正确方式：直接查找所有类型为 AnnotationAwareAspectJAutoProxyCreator 的 Bean 定义
		String[] beanNames = registry.getBeanDefinitionNames();
		for (String beanName : beanNames) {
			AbstractBeanDefinition beanDef = (AbstractBeanDefinition)registry.getBeanDefinition(beanName);
			if (beanDef.getBeanClassName() != null &&
					beanDef.getBeanClassName().equals(AnnotationAwareAspectJAutoProxyCreator.class.getName())) {

				// 替换为自定义的安全代理创建器
				RootBeanDefinition safeDefinition = new RootBeanDefinition(FinalSafeAnnotationAwareAspectJAutoProxyCreator.class);
				safeDefinition.setRole(beanDef.getRole());
				safeDefinition.setSource(beanDef.getSource());
				safeDefinition.copyQualifiersFrom(beanDef);
				safeDefinition.setLazyInit(beanDef.isLazyInit());
				safeDefinition.setScope(beanDef.getScope());

				// 注册替换后的定义
				registry.removeBeanDefinition(beanName);
				registry.registerBeanDefinition(beanName, safeDefinition);
				return; // 找到并替换后退出
			}
		}

		// 如果没有找到，确保注册一个自定义的代理创建器
		if (!registry.containsBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator")) {
			RootBeanDefinition safeDefinition = new RootBeanDefinition(FinalSafeAnnotationAwareAspectJAutoProxyCreator.class);
			safeDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			registry.registerBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator", safeDefinition);
		}
	}

}