package io.arkx.framework.performance.monitor.config;

import io.arkx.framework.performance.monitor.SystemInfoService;
import io.arkx.framework.performance.monitor.TraceRecorder;
import io.arkx.framework.performance.monitor.repository.TraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * @author Nobody
 * @date 2025-06-06 18:42
 * @since 1.0
 */
@Configuration
public class PerformanceMonitorConfiguration {


	// 注册到Spring上下文
//	@Bean
//	public static BeanDefinitionRegistryPostProcessor aopSafety() {
//		return registry -> {
//			for (String name : registry.getBeanDefinitionNames()) {
//				BeanDefinition bd = registry.getBeanDefinition(name);
//				if (bd.getBeanClassName() != null) {
//					// 自动检测并设置 final 类
//					if (Modifier.isFinal(Objects.requireNonNull(bd.getSource()).getClass().getModifiers())) {
//						bd.setAttribute(AUTO_PROXY_SKIP_ATTRIBUTE, Boolean.TRUE);
//					}
//				}
//			}
//		};
//	}

//	@Bean
//	public static BeanFactoryPostProcessor registerSkipFinalProcessor() {
//		return beanFactory -> {
//			if (beanFactory instanceof ConfigurableListableBeanFactory) {
//				// 注册自定义代理创建器
//				BeanDefinition beanDef = BeanDefinitionBuilder
//						.rootBeanDefinition(FinalClassSkippingAutoProxyCreator.class)
//						.setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
//						.getBeanDefinition();
//
//				((ConfigurableListableBeanFactory) beanFactory)
//						.registerBeanDefinition("skipFinalClassAutoProxyCreator", beanDef);
//			}
//		};
//	}

	@Bean
//	@ConfigurationProperties(prefix = "arkx.performance.monitor")
	public MonitorConfig monitorConfig() {
		return new MonitorConfig();
	}

//	@Bean
//	@DependsOn("monitorConfig")
//	public TraceContext traceContext(MonitorConfig config) {
//		return new TraceContext(config);
//	}

	@Bean
	public SystemInfoService systemInfoService() {
		return new SystemInfoService();
	}

	@Bean
	@DependsOn("monitorConfig")
	public TraceRecorder traceRecorder(MonitorConfig config, TraceRepository repository) {
		return new TraceRecorder(config, repository);
	}

//	@Bean
////	@DependsOn("monitorConfig")
//	public MethodMonitorAspect methodMonitorAspect(
////			MonitorConfig config, MonitorConfigService configService, TraceRecorder recorder
//	) {
//		return new MethodMonitorAspect();
//	}

//    @Bean
//	public FinalClassSkippingAutoProxyCreator finalClassSkippingAutoProxyCreator() {
//		return new FinalClassSkippingAutoProxyCreator();
//	}
}
