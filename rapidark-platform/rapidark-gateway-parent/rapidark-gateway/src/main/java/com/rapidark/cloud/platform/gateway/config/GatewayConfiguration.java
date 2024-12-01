package com.rapidark.cloud.platform.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidark.cloud.platform.gateway.filter.ArkRequestGlobalFilter;
import com.rapidark.cloud.platform.gateway.handler.GlobalExceptionHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 网关配置
 *
 * @author L.cm
 */
@Configuration(proxyBeanMethods = false)
public class GatewayConfiguration {

	/**
	 * 创建PigRequest全局过滤器
	 * @return PigRequest全局过滤器
	 */
	@Bean
	public ArkRequestGlobalFilter pigRequestGlobalFilter() {
		return new ArkRequestGlobalFilter();
	}

	/**
	 * 创建全局异常处理程序
	 * @param objectMapper 对象映射器
	 * @return 全局异常处理程序
	 */
	@Bean
	public GlobalExceptionHandler globalExceptionHandler(ObjectMapper objectMapper) {
		return new GlobalExceptionHandler(objectMapper);
	}

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedHeader("*");
		config.setMaxAge(18000L);
		config.addAllowedMethod("*");
		config.addAllowedOriginPattern("*");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/npm/cdn/**", config);
		source.registerCorsConfiguration("/auth/oauth2/token", config);
		return new CorsFilter(source);
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
//	@ConditionalOnProperty(value = "security.micro", matchIfMissing = true)
	public SecurityWebFilterChain authorizationServerSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) throws Exception {

		serverHttpSecurity.authorizeExchange((authorizeExchangeSpec -> {
			authorizeExchangeSpec.pathMatchers(
					"/npm/cdn/**",
					"/auth/captcha/image",
					"/auth/oauth2/token",
					"/**"
			).permitAll();
		}))
		.csrf(ServerHttpSecurity.CsrfSpec::disable);

		return serverHttpSecurity.build();
	}

}
