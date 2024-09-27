package com.rapidark.cloud.base.server;

import com.rapidark.boot.RapidArkApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 平台基础服务
 * 提供系统用户、权限分配、资源、客户端管理
 *
 * @author liuyadu
 */
@EnableCaching
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.rapidark.cloud.base.server.mapper")
public class BaseApplication {
    public static void main(String[] args) {
        RapidArkApplication.run(BaseApplication.class, args);

        System.out.println("==========================================");
    }
}
