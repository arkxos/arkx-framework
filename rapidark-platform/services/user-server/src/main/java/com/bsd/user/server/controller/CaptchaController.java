package com.bsd.user.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.bsd.user.server.model.dto.CaptchaInitDTO;
import com.bsd.user.server.model.dto.CaptchaInitResultDTO;
import com.bsd.user.server.model.dto.CaptchaValidateDTO;
import com.bsd.user.server.model.dto.CaptchaValidateResultDTO;
import com.bsd.user.server.service.CaptchaService;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.utils.RedisUtils;
import com.rapidark.framework.common.utils.StringUtils;
import com.rapidark.framework.common.utils.WebUtils;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @Author: linrongxin
 * @Date: 2019/9/4 15:02
 */
@Slf4j
@Schema(title = "行为验证")
@RestController
@RequestMapping("/user/captcha")
public class CaptchaController {
    /**
     * 初始化用户redis key 前缀
     */
    private static final String CAPTCHA_INIT_USER_PREFIX = "captcha:init:user:";

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CaptchaService captchaService;

    /**
     * 行为验证初始化
     *
     * @param userId     用户ID
     * @param clientType 客户端类型
     * @param request    request
     * @return
     */
    @Schema(title = "初始化", name = "行为验证初始化")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId", required = true, value = "用户ID", paramType = "form"),
//            @ApiImplicitParam(name = "clientType", required = true, value = "客户端类型web(pc浏览器),h5(手机浏览器,包括webview),native(原生app),unknown(未知)", paramType = "form"),
//    })
    @PostMapping("/init")
    public ResponseResult init(@RequestParam(value = "userId") Long userId,
                               @RequestParam(value = "clientType") String clientType,
                               HttpServletRequest request) {
        //业务参数
        CaptchaInitDTO captchaInitDTO = new CaptchaInitDTO();
        captchaInitDTO.setIp(WebUtils.getRemoteAddress(request));
        captchaInitDTO.setUserId(CAPTCHA_INIT_USER_PREFIX + MD5Utils.md5Hex(userId+"", "UTF-8"));//用户ID MD5加密一下,避免泄露
        captchaInitDTO.setClientType(clientType);
        log.info("init:{}", captchaInitDTO);
        //调用初始化接口
        CaptchaInitResultDTO captchaInitResultDTO = captchaService.init(captchaInitDTO);
        //初始化数据存到redis中
        captchaInitDTO.setGtServerStatus(captchaInitResultDTO.getGtServerStatus());
        redisUtils.set(captchaInitDTO.getUserId(), JSON.toJSONString(captchaInitDTO), 60 * 60);
        return ResponseResult.ok(captchaInitResultDTO);
    }

    /**
     * 行为验证二次验证
     *
     * @param chllenge
     * @param validate
     * @param seccode
     * @param request
     * @return
     */
    @Schema(title = "二次验证", name = "行为验证二次验证")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId", required = true, value = "用户ID", paramType = "form"),
//            @ApiImplicitParam(name = "chllenge", required = true, value = "极验验证二次验证表单数据 chllenge", paramType = "form"),
//            @ApiImplicitParam(name = "validate", required = true, value = "极验验证二次验证表单数据 validate", paramType = "form"),
//            @ApiImplicitParam(name = "seccode", required = true, value = "极验验证二次验证表单数据 seccode", paramType = "form"),
//    })
    @PostMapping("/validate")
    public ResponseResult validate(@RequestParam(value = "userId") Long userId,
                                   @RequestParam(value = "chllenge") String chllenge,
                                   @RequestParam(value = "validate") String validate,
                                   @RequestParam(value = "seccode") String seccode,
                                   HttpServletRequest request) {
        //获取session中的数据
        String initStr = (String) redisUtils.get(CAPTCHA_INIT_USER_PREFIX + MD5Utils.md5Hex(userId+"", "UTF-8"));
        if (StringUtils.isEmpty(initStr)) {
            return ResponseResult.failed("二次验证之前未调用初始化接口");
        }
        CaptchaInitDTO captchaInitDTO = JSON.parseObject(initStr, CaptchaInitDTO.class);
        //业务数据
        CaptchaValidateDTO captchaValidateDTO = new CaptchaValidateDTO();
        captchaValidateDTO.setChllenge(chllenge);
        captchaValidateDTO.setValidate(validate);
        captchaValidateDTO.setSeccode(seccode);
        captchaValidateDTO.setIp(WebUtils.getRemoteAddress(request));
        captchaValidateDTO.setUserId(captchaInitDTO.getUserId());
        captchaValidateDTO.setClientType(captchaInitDTO.getClientType());
        captchaValidateDTO.setGtServerStatus(captchaInitDTO.getGtServerStatus());
        log.info("validate:{}", captchaValidateDTO);
        //二次验证请求
        CaptchaValidateResultDTO captchaValidateResultDTO = captchaService.validate(captchaValidateDTO);
        if ("success".equals(captchaValidateResultDTO.getStatus())) {
            return ResponseResult.ok(captchaValidateResultDTO);
        }
        return ResponseResult.failed(captchaValidateResultDTO);
    }

}
