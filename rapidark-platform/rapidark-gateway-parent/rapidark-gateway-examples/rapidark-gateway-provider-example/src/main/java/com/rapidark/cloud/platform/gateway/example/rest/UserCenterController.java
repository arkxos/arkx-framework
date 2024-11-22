package com.rapidark.cloud.platform.gateway.example.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @Description 演示示例，模拟《用户管理系统》接口
 * @Author JL
 * @Date 2020/12/30
 * @Version V1.0
 */
@Slf4j
@RestController
@RequestMapping("/userCenter")
public class UserCenterController {

    @Value("${server.port}")
    private String port;

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/addUser")
    public String addUser() {
        return "server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/getUser")
    public String getUser() {
        System.out.println("============/getUser=============");
        return "server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/delUser")
    public String delUser() {
        return "server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/regUser")
    public String regUser() {
        return "server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/getToken")
    public String getToken() {
        String uuid = UUID.randomUUID().toString();
        log.info("getToken api server port: " + port + ",ok:" + System.currentTimeMillis() + ", uuid:" + uuid);
        return uuid;
    }

}
