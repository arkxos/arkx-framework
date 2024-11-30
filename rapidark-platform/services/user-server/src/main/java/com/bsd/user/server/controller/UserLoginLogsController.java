package com.bsd.user.server.controller;


import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户-登录日志 前端控制器
 *
 * @author lisongmao
 * @date 2019-06-26
 */
@RestController
@RequestMapping("/user")
@Schema(title = "登录记录查询")
public class UserLoginLogsController {
    @Schema(title = "登录日志信息查询", name = "登录日志信息查询")
    @GetMapping("/login/log")
    public String getLoginLogInfo() {
        return "hello world";
    }
}
