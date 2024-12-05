package com.rapidark.cloud.base.server.controller.cmd;

import com.rapidark.platform.system.api.constants.BaseConstants;
import lombok.Data;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/14 15:13
 */
@Data
public class AddRouteCommand {

    /**
     *
     *      * @param path        路径表达式
     *      * @param routeName   描述
     *      * @param serviceId   服务名方转发
     *      * @param url         地址转发
     *      * @param stripPrefix 忽略前缀
     *      * @param retryable   支持重试
     *      * @param status      是否启用
     */
    //    @RequestParam(value = "routeName", required = true, defaultValue = "")
    private String routeName = "";
    //    @RequestParam(value = "routeType", required = false, defaultValue = BaseConstants.ROUTE_TYPE_SERVICE)
    private String routeType = BaseConstants.ROUTE_TYPE_SERVICE;
    //    @RequestParam(value = "routeDesc", required = true, defaultValue = "")
    private String routeDesc = "";
    //    @RequestParam(value = "path")
    private String path;
    //    @RequestParam(value = "serviceId", required = false)
    private String serviceId;
    //    @RequestParam(value = "url", required = false)
    private String url;
    //    @RequestParam(value = "stripPrefix", required = false, defaultValue = "1")
    private Integer stripPrefix = 1;
    //    @RequestParam(value = "retryable", required = false, defaultValue = "0")
    private Integer retryable = 0;
    //    @RequestParam(value = "status", defaultValue = "1")
    private Integer status = 1;

}
