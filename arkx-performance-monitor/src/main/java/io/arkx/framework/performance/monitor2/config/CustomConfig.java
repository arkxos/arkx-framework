package io.arkx.framework.performance.monitor2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Nobody
 * @date 2025-06-10 22:22
 * @since 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "ark-performance-monitor")
public class CustomConfig {

	private String pointcut;

}
