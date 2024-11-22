package com.rapidark.cloud.platform.gateway.manage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * @Description
 * @Author JL
 * @Date 2023/03/11
 * @Version V1.0
 */
@Configuration
public class CustomWebFluxConfigurer implements WebFluxConfigurer {

    /**
     * 加载本地静态资源目录
     * WebFlux默认是classpath:/META-INF/resources/,classpath:/resources/,classpath:/static/,classpath:/public/
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/ffgateway/**").addResourceLocations("classpath:/static/");
    }

}
