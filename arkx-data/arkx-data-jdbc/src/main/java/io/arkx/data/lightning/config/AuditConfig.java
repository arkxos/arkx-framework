package io.arkx.data.lightning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-09-06 22:14
 * @since 1.0
 */
@Configuration
@EnableJdbcAuditing // 启用审计
public class AuditConfig {

	@Bean
	public AuditorAware<String> auditorProvider() {
		// 返回当前用户名的逻辑，例如从Spring Security中获取
		return () -> Optional.ofNullable(SecurityContextHolder.getContext())
				.map(SecurityContext::getAuthentication)
				.map(Authentication::getName)
				.or(() -> Optional.of("system")); // 如果无认证用户，使用默认值
	}

}
