//package io.arkx.framework.performance.monitor.interceptor;
//
//import org.springframework.aop.framework.AdvisedSupport;
//import org.springframework.aop.framework.AopProxy;
//import org.springframework.aop.framework.DefaultAopProxyFactory;
//import org.springframework.context.annotation.Bean;
//
//import java.lang.reflect.Modifier;
//
///**
// * @author Nobody
// * @date 2025-06-07 22:12
// * @since 1.0
// */
//// 最安全的自定义代理工厂实现
//public class SafeAopProxyFactory extends DefaultAopProxyFactory {
//	private static final boolean SAFE_MODE = true;
//
//	@Override
//	public AopProxy createAopProxy(AdvisedSupport config) {
//		if (SAFE_MODE) {
//			Class<?> targetClass = config.getTargetClass();
//			if (targetClass != null) {
//				if (Modifier.isFinal(targetClass.getModifiers())) {
//					return new NoOpAopProxy(config); // 无操作代理
//				}
//
//				// 检查已知问题类
//				if (isProhibitedClass(targetClass)) {
//					return new NoOpAopProxy(config);
//				}
//			}
//		}
//		return super.createAopProxy(config);
//	}
//
//	private boolean isProhibitedClass(Class<?> clazz) {
//		// 检查常见的final类库
//		return clazz.getName().startsWith("com.ulisesbocchio.") ||
//				clazz.getName().startsWith("com.hazelcast.");
//	}
//}
//
