package io.arkx.framework.data.common.config;

/**
 * @author Nobody
 * @date 2025-07-26 15:39
 * @since 1.0
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

@Configuration
public class AuditorConfig {

	@Bean
	public AuditorAware<String> auditorAware() {
		return () -> {
			// 从 Spring Security 上下文获取当前用户（示例）
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.isAuthenticated()) {
				return Optional.of(authentication.getName());
			}
			return Optional.empty(); // 未认证时返回空（可根据需求调整）
		};
	}

}
