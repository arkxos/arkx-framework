package com.rapidark.cloud.generator.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 平台基础服务
 *  提供系统用户、权限分配、资源、客户端管理
 * @author darkness
 * @date 2021/5/21 11:20
 * @version 1.0
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GeneratorApplication {
    public static void main(String[] args) {
		SpringApplication.run(GeneratorApplication.class, args);
    }
}
