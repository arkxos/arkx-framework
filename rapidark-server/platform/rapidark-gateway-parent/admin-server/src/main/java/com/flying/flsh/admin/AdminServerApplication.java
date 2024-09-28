package com.flying.flsh.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Description 用于监控基于SpringBoot的应用,如:内存，在线状态，心跳，日志级别，线程，Environment等管理功能，提供简洁的可视化WEB UI。
 * @Author jianglong
 * @Date 2020/06/02
 * @Version V1.0
 */
@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
public class AdminServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminServerApplication.class, args);
    }

}
