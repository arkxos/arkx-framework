package com.rapidark.cloud.platform.gateway.example.feign;

import feign.Feign;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @Description 自定义LoadBalanced负载均衡配置与RestTemplate客户端工具类集成配置
 * @Author JL
 * @Date 2021/08/04
 * @Version V1.0
 */
@Configuration
public class CustomLoadBalancerConfig {

    @LoadBalanced
    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }

    /**
     * 给 RestTemplate 实例添加 @LoadBalanced 注解，开启 @LoadBalanced 与 Ribbon 的集成
     * @return
     */
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * WebClient是Spring Web Flux中提供的类，通过使用WebClient可以通过响应式编程的方式异步访问服务端接口
     * @return
     */
    @LoadBalanced
    @Bean
    public WebClient.Builder builder() {
        return WebClient.builder();
    }
}