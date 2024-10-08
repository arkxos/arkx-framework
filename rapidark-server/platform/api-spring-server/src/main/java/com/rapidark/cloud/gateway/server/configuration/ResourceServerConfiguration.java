package com.rapidark.cloud.gateway.server.configuration;

import com.rapidark.cloud.gateway.server.exception.JsonAccessDeniedHandler;
import com.rapidark.cloud.gateway.server.exception.JsonAuthenticationEntryPoint;
import com.rapidark.cloud.gateway.server.exception.JsonSignatureDeniedHandler;
import com.rapidark.cloud.gateway.server.filter.*;
import com.rapidark.cloud.gateway.server.locator.ResourceLocator;
import com.rapidark.cloud.gateway.server.oauth2.RedisAuthenticationManager;
import com.rapidark.cloud.gateway.server.service.AccessLogService;
import com.rapidark.cloud.gateway.server.service.feign.OpenAppServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.SecurityContextServerWebExchange;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * oauth2资源服务器配置
 * @author darkness
 * @date 2022/5/14 17:25
 * @version 1.0
 */
@Configuration
public class ResourceServerConfiguration {

    private static final String MAX_AGE = "18000L";

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private ResourceLocator resourceLocator;
    @Autowired
    private ApiProperties apiProperties;
    @Autowired
    private AccessLogService accessLogService;
    @Autowired
    private OpenAppServiceClient openAppServiceClient;

    /**
     * 跨域配置
     *
     * @return
     */
    public WebFilter corsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            ServerHttpRequest request = ctx.getRequest();
            if (CorsUtils.isCorsRequest(request)) {
                HttpHeaders requestHeaders = request.getHeaders();
                ServerHttpResponse response = ctx.getResponse();
                HttpMethod requestMethod = requestHeaders.getAccessControlRequestMethod();
                HttpHeaders headers = response.getHeaders();
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestHeaders.getOrigin());
                headers.addAll(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders.getAccessControlRequestHeaders());
                if (requestMethod != null) {
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethod.name());
                }
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
                headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return chain.filter(ctx);
        };
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        // 自定义oauth2 认证, 使用redis读取token,而非jwt方式
        JsonAuthenticationEntryPoint entryPoint = new JsonAuthenticationEntryPoint(accessLogService);
        JsonAccessDeniedHandler accessDeniedHandler = new JsonAccessDeniedHandler(accessLogService);
        AccessManager accessManager = new AccessManager(resourceLocator, openAppServiceClient, apiProperties);
        AuthenticationWebFilter oauth2 = new AuthenticationWebFilter(new RedisAuthenticationManager(new RedisTokenStore(redisConnectionFactory)));
        oauth2.setServerAuthenticationConverter(new ServerBearerTokenAuthenticationConverter());
        oauth2.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(entryPoint));
        oauth2.setAuthenticationSuccessHandler(new ServerAuthenticationSuccessHandler() {
            @Override
            public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
                ServerWebExchange exchange = webFilterExchange.getExchange();
                SecurityContextServerWebExchange securityContextServerWebExchange =
                        new SecurityContextServerWebExchange(exchange,
                                ReactiveSecurityContextHolder.getContext().subscriberContext(
                                    ReactiveSecurityContextHolder.withAuthentication(authentication)
                ));
                return webFilterExchange.getChain().filter(securityContextServerWebExchange);
            }
        });
        serverHttpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/", "/*/druid/**").permitAll()
                // 动态权限验证
                .anyExchange().access(accessManager)
                .and().exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(entryPoint).and()
                // 日志前置过滤器
                .addFilterAt(new PreRequestFilter(), SecurityWebFiltersOrder.FIRST)
                // 跨域过滤器
                .addFilterAt(corsFilter(), SecurityWebFiltersOrder.CORS)
                // 签名验证过滤器
                .addFilterAt(new PreSignatureFilter(openAppServiceClient, apiProperties, new JsonSignatureDeniedHandler(accessLogService)), SecurityWebFiltersOrder.CSRF)
                // 访问验证前置过滤器
                .addFilterAt(new PreCheckFilter(accessManager, accessDeniedHandler), SecurityWebFiltersOrder.CSRF)
                // oauth2认证过滤器
                .addFilterAt(oauth2, SecurityWebFiltersOrder.AUTHENTICATION)
                // 日志过滤器
                .addFilterAt(new PreResponseFilter(accessLogService, apiProperties), SecurityWebFiltersOrder.SECURITY_CONTEXT_SERVER_WEB_EXCHANGE);
        return serverHttpSecurity.build();
    }
}
