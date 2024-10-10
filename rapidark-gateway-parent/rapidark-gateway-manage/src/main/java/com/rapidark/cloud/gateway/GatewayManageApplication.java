//package com.rapidark.cloud.gateway;
//
//import com.rapidark.boot.RapidArkApplication;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.EnableScheduling;
//
//import com.rapidark.cloud.gateway.manage.task.MonitorTaskService;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//
///**
// * @Description 动态路由配置管理
// * @Author jianglong
// * @Date 2020/05/27
// * @Version V1.0
// */
//@Slf4j
//@EnableAsync
//@EnableScheduling
//@SpringBootApplication(scanBasePackages = {
//        "com.rapidark.cloud",
//        "com.rapidark",
//        "com.xdreamaker",
//        "com.flying"
//})
//@EnableDiscoveryClient
//public class GatewayManageApplication {
//
//    @Resource
//    private MonitorTaskService monitorTaskService;
//
//    public static void main(String[] args) {
//        RapidArkApplication.run(GatewayManageApplication.class, args);
//    }
//
//    /**
//     * 执行监控程序
//     */
//    @PostConstruct
//    public void runMonitor(){
//        log.info("运行网关路由监控任务...");
//        monitorTaskService.executeMonitorTask();
//    }
//}
