package com.rapidark.cloud.base.server;

import com.rapidark.boot.RapidArkApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.io.PrintWriter;
import java.net.ProxySelector;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        DriverManager.setLogWriter(new PrintWriter(System.out) {
            @Override
            public void println(String x) {
                // 给日志附加时间戳
                x = new SimpleDateFormat("--- yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " " + x;
                super.println(x);
                System.out.println("---------------------------");
            }
        });

        RapidArkApplication.run(BaseApplication.class, args);

        System.out.println("==========================================");
    }
}
