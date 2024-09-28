package com.flying.fish.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Description 生产者-示例
 * @Author JL
 * @Date 2020/07/06
 * @Version V1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProviderExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderExampleApplication.class, args);
    }
}
