package com.rapidark.cloud.platform.gateway;

import com.rapidark.cloud.platform.common.feign.annotation.EnableArkFeignClients;
import com.rapidark.cloud.platform.common.security.annotation.EnableArkResourceServer;
import com.rapidark.cloud.platform.gateway.manage.task.MonitorTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.Resource;

/**
 * @Description 动态路由配置管理
 * @Author JL
 * @Date 2020/05/27
 * @Version V1.0
 */
@Slf4j
@EnableAsync
@EnableArkFeignClients
@EnableArkResourceServer
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.rapidark")
@EnableDiscoveryClient
public class ArkGatewayManageApplication implements ApplicationListener<ApplicationReadyEvent> {

    /**
     本服务单独部署，对外提供网关配置与管理服务；
     访问方式：http://网关IP_xxxxx:端口_8770/index.html
     如：http://127.0.0.1:8770/index.html

     启用了阿里sentinel限流组件，需在启JVM的动命令中指定注册目标服务IP端口和服务名
     java -Dcsp.sentinel.dashboard.server=127.0.0.1:8080 -Dproject.name=dynamic-manage -jar dynamic-manage.jar
     */

    @Resource
    private MonitorTaskService monitorTaskService;

    public static void main(String[] args) {
        SpringApplication.run(ArkGatewayManageApplication.class, args);
        System.out.println("==============start finished============================");
    }

    /**
     * 执行监控程序
     */
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		log.info("运行网关路由监控任务...");
		monitorTaskService.executeMonitorTask();
	}
}
