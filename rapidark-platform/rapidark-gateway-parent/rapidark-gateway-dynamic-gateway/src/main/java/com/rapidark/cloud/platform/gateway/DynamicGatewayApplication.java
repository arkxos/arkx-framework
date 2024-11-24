package com.rapidark.cloud.platform.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description
 * @Author JL
 * @Date 2020/05/19
 * @Version V1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
//@PropertySource("classpath:application.yml")
public class DynamicGatewayApplication {

    /**
     Spring Cloud Gateway 是 Spring Cloud 的一个全新项目，该项目是基于 Spring 5.0;Spring Boot 2.0 和 Project Reactor 等技术开发的网关，旨在为微服务架构提供一种简单有效的统一的 API 路由管理方式
     注意版本组合：
     Spring-Cloud               Spring-Boot
     =================          ==============
     2021.0.4                   2.6.11
     == == (以下cloud版本官方不再提供维护) == ==
     Hoxton.SR10                2.3.9.RELEASE
     Hoxton.SR4                 2.2.6.RELEASE
     Hoxton.SR1                 2.2.1.RELEASE
     Greenwich.SR3              2.1.1.RELEASE
     Finchley.RELEASE           2.0.3.RELEASE

     查看路由信息：http://127.0.0.1:8771/actuator/gateway/routes
     查看过滤器工厂列表：http://127.0.0.1:8771/actuator/gateway/routefilters
     查看日志等级：http://127.0.0.1:8771/actuator/loggers

     本服务单独部署，对外提供网关转发服务；
     访问方式：http://网关IP_xxxxx:端口_8771/xxxx
     如：http://127.0.0.1:8771/route/userCenter/getToken

     升级注意事项（Spring官方已移除Netflix OSS组件）：
     1.从springCloud.2021.0.x版本开始移除Hystrix的支持（替代方案：集成alibaba的sentinel【面向云原生微服务的高可用流控防护组件】）
     2.从springCloud.2021.0.x版本开始移除Ribbon的支持（替代方案：使用自带的LoadBalance模块）
     详情参见：https://cloud.tencent.com/developer/article/1973602

     启用了阿里sentinel限流组件，需在启JVM的动命令中指定注册目标服务IP端口和服务名
     java -Dcsp.sentinel.dashboard.server=127.0.0.1:8080 -Dproject.name=dynamic-gateway -jar dynamic-gateway.jar
     */

    /**
     * 启动入口
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DynamicGatewayApplication.class);
        application.run(args);
    }

}
