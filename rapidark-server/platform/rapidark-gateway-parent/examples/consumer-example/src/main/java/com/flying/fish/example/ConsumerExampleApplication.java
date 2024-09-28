package com.flying.fish.example;

import com.flying.fish.example.feign.CustomLoadBalancerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Description 消费者-示例
 * @Author JL
 * @Date 2021/08/04
 * @Version V1.0
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
//@LoadBalancerClient(name = "provider-examples", configuration = CustomLoadBalancerConfig.class)
public class ConsumerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerExampleApplication.class, args);
    }
}
