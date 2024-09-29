package com.rapidark.cloud.base.server.controller.cmd;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 添加应用信息参数
 * @author darkness
 * @version 1.0
 * @date 2021/5/16 22:06
 */
@Data
public class CreateAppCommand {
    /**
     *
     *      * @param appName     应用名称
     *      * @param appNameEn   应用英文名称
     *      * @param appOs       手机应用操作系统:ios-苹果 android-安卓
     *      * @param appType     应用类型:server-应用服务 app-手机应用 pc-PC网页应用 wap-手机网页应用
     *      * @param appIcon     应用图标
     *      * @param appDesc     应用说明
     *      * @param status      状态
     *      * @param website     官网地址
     *      * @param developerId 开发者
     */
    //    @RequestParam(value = "appName")
    private String appName;
    //    @RequestParam(value = "appNameEn")
    private String appNameEn;
    //    @RequestParam(value = "appType")
    private String
            appType;
    //    @RequestParam(value = "appIcon", required = false)
    private String appIcon;
    //    @RequestParam(value = "appOs", required = false)
    private String appOs;
    //    @RequestParam(value = "appDesc", required = false)
    private String appDesc;
    //    @RequestParam(value = "status", defaultValue = "1")
    private Integer status;
    //    @RequestParam(value = "website", required = false)
    private String website;
    //    @RequestParam(value = "developerId", required = false)
    private Long developerId;
    //    @RequestParam(value = "isSign", required = false, defaultValue = "0")
    private Integer isSign;
    //    @RequestParam(value = "isEncrypt", required = false, defaultValue = "0")
    private Integer isEncrypt;
    //    @RequestParam(value = "encryptType", required = false, defaultValue = "")
    private String encryptType;
    //    @RequestParam(value = "publicKey", required = false, defaultValue = "")
    private String publicKey;
    private String groupCode;
    private String ip;
}
