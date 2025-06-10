package io.arkx.framework.performance.monitor2.config;

import io.arkx.framework.performance.monitor2.PerformanceInterceptor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Nobody
 * @date 2025-06-10 22:21
 * @since 1.0
 */
@Slf4j
@Configuration
public class PerformanceConfig {

	@Resource
	private CustomConfig customConfig;

	private String pointcut = "execution(* *(..))";

	@Bean
	public AspectJExpressionPointcutAdvisor configurabledvisor() {
		log.info("arkx=>init PerformanceInterceptor");
		AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
		String cutRange = customConfig.getPointcut() == null ? pointcut : customConfig.getPointcut();
		cutRange = cutRange + " && !@annotation(io.arkx.framework.performance.monitor2.IngorePerformanceLog)";
		advisor.setExpression(cutRange);
		advisor.setAdvice(new PerformanceInterceptor());
		return advisor;
	}

}
