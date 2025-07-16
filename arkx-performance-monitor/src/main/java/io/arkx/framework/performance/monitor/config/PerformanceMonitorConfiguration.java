package io.arkx.framework.performance.monitor.config;

import io.arkx.framework.performance.monitor.SystemInfoService;
import io.arkx.framework.performance.monitor.TraceRecorder;
import io.arkx.framework.performance.monitor.interceptor.PerformanceInterceptor;
import io.arkx.framework.performance.monitor.repository.TraceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;

/**
 * @author Nobody
 * @date 2025-06-06 18:42
 * @since 1.0
 */
@Slf4j
@Configuration
public class PerformanceMonitorConfiguration {

	private String pointcut = "execution(* *(..))";

	@Bean
	@Lazy
	@DependsOn("applicationContextHolder")
	public AspectJExpressionPointcutAdvisor configurabledvisor(MonitorConfig monitorConfig) {
		log.info("arkx=>init PerformanceInterceptor");
		AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();

		// 1. 重新组织表达式结构
		StringBuilder expressionBuilder = new StringBuilder();


		// 5. 添加用户自定义点切（如果有）
		if (!StringUtils.isEmpty(monitorConfig.getPointcut())) {
			expressionBuilder.append(monitorConfig.getPointcut() + " && ");
		} else {
//			expressionBuilder.append(pointcut);
		}

		// 2. 包排除部分（必须满足）
		expressionBuilder.append(
				"!within(io.arkx.framework.performance.monitor..*) " +
				" && !within(org.springframework.beans.factory.InitializingBean+)" +
				" && !within(org.springframework.beans.factory.config.BeanPostProcessor+)" +
				" && !within(org.springframework.context.ApplicationContextAware+)"


//						"&& " +
//						"!within(org.springframework.context..*) && " +
//						"!within(org.springframework.boot..*) && " +
//						"!within(org.springframework.beans..*)"
		);

		// 3. 方法排除部分（必须满足）
		expressionBuilder.append(
				" && !execution(* get*(..))" +
				" && !execution(* set*(..))" +
				" && !execution(* put*(..))" +
				" && !execution(* is*(..))" +
				" && !execution(* toString())" +
				" && !execution(* hashCode())" +
				" && !execution(* equals(*))" +
				" && !execution(public void jakarta.annotation.PostConstruct.*(..))" +
				" && !execution(public void jakarta.annotation.PreDestroy.*(..))"
//				" && !execution(public void javax.annotation.PostConstruct.*(..))" +
//				" && !execution(public void javax.annotation.PreDestroy.*(..))"
		);

		// 4. 目标范围（或关系）
		expressionBuilder.append(
				" && (" +
						"   @within(org.springframework.stereotype.Controller) || " +
						"   @within(org.springframework.web.bind.annotation.RestController) || " +
						"   @within(org.springframework.stereotype.Component) || " +
						"   @within(org.springframework.stereotype.Service) || " +
						"   @within(org.springframework.stereotype.Repository) || " +
						"   target(org.springframework.data.repository.Repository) || " +
						"   within(org.springframework.data.jpa.repository.support..*)" +
						")"
		);

		String expression = expressionBuilder.toString();
		log.debug("Final PerformanceInterceptor pointcut: {}", expression);

		advisor.setExpression(expression);
		advisor.setAdvice(new PerformanceInterceptor());
		return advisor;
	}
}
