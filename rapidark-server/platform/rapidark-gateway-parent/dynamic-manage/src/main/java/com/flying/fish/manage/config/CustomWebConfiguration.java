package com.flying.fish.manage.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @Description 自定义WEB配置
 * @Author JL
 * @Date 2021/08/10
 * @Version V1.0
 */
@Configuration
public class CustomWebConfiguration {

    /**
     * 给 RestTemplate 实例添加 @LoadBalanced 注解，开启负载轮询
     * @return
     */
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(5000);
        httpRequestFactory.setConnectTimeout(3000);
        httpRequestFactory.setReadTimeout(3000);
        return new RestTemplate(httpRequestFactory);
    }
}
