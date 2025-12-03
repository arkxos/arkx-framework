package io.arkx.framework.performance.monitor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.arkx.framework.performance.monitor.interceptor.RequestMonitoringInterceptor;

/**
 * @author Nobody
 * @date 2025-06-08 0:05
 * @since 1.0
 */
// 配置类2: 监控拦截器配置
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE) // 可指定顺序
public class MonitorWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private RequestMonitoringInterceptor requestMonitoringInterceptor;

    public MonitorWebMvcConfigurer() {
        System.out.println("----------------");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestMonitoringInterceptor).addPathPatterns("/**") // 所有路径
                .order(Ordered.HIGHEST_PRECEDENCE); // 设置最高优先级
    }

}
