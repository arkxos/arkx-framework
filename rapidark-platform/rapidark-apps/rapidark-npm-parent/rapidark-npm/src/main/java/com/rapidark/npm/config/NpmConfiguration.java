package com.rapidark.npm.config;

import com.rapidark.npm.JNPMService;
import com.rapidark.npm.JNPMSettings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.nio.file.Paths;

@AutoConfiguration
@EnableConfigurationProperties(NpmConfigProperties.class)
public class NpmConfiguration {

    @Bean
    @ConditionalOnProperty(value = "npm.enabled", havingValue = "true")
    public JNPMService jnpmService(NpmConfigProperties configProperties) {
        if(!JNPMService.isConfigured()) {
            JNPMSettings.JNPMSettingsBuilder builder = JNPMSettings.builder();

            String registryUrl = configProperties.getRegistryUrl();
            if(!StringUtils.isEmpty(registryUrl)) builder.registryUrl(registryUrl);

            String homeDirectory = configProperties.getHomeDirectory();
            if(!StringUtils.isEmpty(homeDirectory)) builder.homeDirectory(Paths.get(homeDirectory));

            String downloadDirectory = configProperties.getDownloadDirectory();
            if(!StringUtils.isEmpty(downloadDirectory)) builder.downloadDirectory(Paths.get(downloadDirectory));

            String installDirectory = configProperties.getInstallDirectory();
            if(!StringUtils.isEmpty(installDirectory)) builder.installDirectory(Paths.get(installDirectory));

            builder.username(configProperties.getUsername()).password(configProperties.getPassword());
            builder.useCache(Boolean.valueOf(configProperties.isUseCache()));

            JNPMService.configure(builder.build());
        }
        return JNPMService.instance();
    }

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
//	@ConditionalOnProperty(value = "security.micro", matchIfMissing = true)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

		AntPathRequestMatcher[] requestMatchers = new AntPathRequestMatcher[] {
			AntPathRequestMatcher.antMatcher("/cdn/**")
		};

		http.authorizeHttpRequests(authorizeRequests -> {
			// 自定义接口、端点暴露
			authorizeRequests.requestMatchers(requestMatchers).permitAll();
			authorizeRequests.anyRequest().authenticated();
		});

		DefaultSecurityFilterChain securityFilterChain = http.build();

		return securityFilterChain;
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
		source.registerCorsConfiguration("/cdn/**", config);
		return new CorsFilter(source);
	}

}
