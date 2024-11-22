//package com.flying.fish.manage.config;
//
//import com.alibaba.fastjson.JSONObject;
//import util.com.rapidark.cloud.platform.gateway.framework.ApiResult;
//import util.com.rapidark.cloud.platform.gateway.framework.Constants;
//import util.com.rapidark.cloud.platform.gateway.framework.HttpResponseUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
//import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
//import reactor.core.publisher.Mono;
//
///**
// * @Description 增加Security服务访问鉴权配置
// * @Author JL
// * @Date 2023/01/10
// * @Version V1.0
// */
//@Slf4j
//@Configuration
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
//public class SecurityConfigurer {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(CsrfConfigurer::disable)
//                .cors(CorsConfigurer::disable)
//                .authorizeHttpRequests(authorizeRequests -> {
//                    authorizeRequests.requestMatchers("/**").permitAll()
//                            .requestMatchers(
//                            "/index.html", "/ffgateway/**",
//                            "/toLogin", "/login", "/logout", "/assets/**",
//                                    "/actuator","/actuator/**", "/*.html", "/*.js", "/*.css", "/*.ico", "/*.woff", "/*.ttf", "/*.svg",
//                                    "/*.eot", "/static/**", "/webjars/**", "/css/**", "/js/**", "/images/**", "/favicon.ico")
//
//                            .permitAll();
////                            .anyRequest(HttpMethod.OPTIONS)
////                            .permitAll();
//                })
//
//
////                .permitAll()//特殊请求过滤
//
////                .anyExchange()
////                .authenticated()
////                .and()
////                .formLogin()
////                .formLogin(formLogin -> {
////                    formLogin.loginPage("/login");
//////                    formLogin.successHandler(successHandler);
////                })
////                .requiresAuthenticationMatcher(
////                        //默认情况下/login在Security中强制为method=post模式，此处改为支持get和post
////                        ServerWebExchangeMatchers.matchers(
////                                ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/login"),
////                                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/login"))
////                )
//                //认证成功时返回的响应内容
////                .authenticationSuccessHandler((exchange, authentication) -> {
////                    ServerHttpResponse response = exchange.getExchange().getResponse();
////                    if (authentication.isAuthenticated()) {
////                        return HttpResponseUtils.write(response, HttpStatus.OK, JSONObject.toJSONString(new ApiResult(Constants.SUCCESS)));
////                    }
////                    return response.writeWith(Mono.empty());
////                })
//                //认证失败时返回的响应内容
////                .authenticationFailureHandler((exchange, exception) -> {
////                    log.error("user auth failed ,there is not login! error: {}", exception.getMessage());
////                    ServerHttpResponse response = exchange.getExchange().getResponse();
////                    return HttpResponseUtils.write(response, HttpStatus.UNAUTHORIZED, JSONObject.toJSONString(new ApiResult(Constants.NOT_LOGIN)));
////                })
////                .and()
////                .logout()
//                //默认情况下/logout在Security中强制为method=post模式，此处改为支持get和post
////                .requiresLogout(ServerWebExchangeMatchers.matchers(
////                        ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/logout"),
////                        ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/logout"))
////                )
//                //退出成功时返回的响应内容
////                .logoutSuccessHandler((exchange, authentication) -> {
////                    ServerHttpResponse response = exchange.getExchange().getResponse();
////                    if (authentication.isAuthenticated()) {
////                        return HttpResponseUtils.write(response, HttpStatus.OK, JSONObject.toJSONString(new ApiResult(Constants.SUCCESS)));
////                    }
////                    return response.writeWith(Mono.empty());
////                })
////                .and()
////                .exceptionHandling()
//                //无权限访问时返回的响应内容
////                .accessDeniedHandler((exchange, denied) -> {
////                    denied.printStackTrace();
////                    ServerHttpResponse response = exchange.getResponse();
////                    ApiResult result = new ApiResult(Constants.NOT_LOGIN, "无权限访问");
////                    return HttpResponseUtils.write(response, HttpStatus.UNAUTHORIZED, JSONObject.toJSONString(result));
////                })
//        ;
//        // 全部放行
////        http.csrf()
////                .disable()
////                .cors()
////                .disable()
////                .authorizeExchange()
////                .pathMatchers(HttpMethod.OPTIONS)
////                .permitAll()//特殊请求过滤
////                .pathMatchers("/**")
////                .permitAll();
//        return http.build();
//    }
//}
