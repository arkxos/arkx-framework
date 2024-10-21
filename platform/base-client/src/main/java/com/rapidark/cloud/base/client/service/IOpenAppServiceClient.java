package com.rapidark.cloud.base.client.service;

import com.rapidark.cloud.base.client.model.entity.OpenApp;
import com.rapidark.cloud.gateway.manage.service.dto.GatewayAppRouteRegServer;
import com.rapidark.framework.commons.model.ResultBody;
import com.rapidark.framework.commons.security.OpenClientDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 *
 * @author darkness
 * @date 2022/5/25 15:24
 * @version 1.0
 */
public interface IOpenAppServiceClient {

    /**
     * 获取应用基础信息
     *
     * @param appId 应用Id
     * @return
     */
    @GetMapping("/app/{appId}/info")
    ResultBody<OpenApp> getApp(@PathVariable("appId") String appId);

    /**
     * 根据ip查找数据
     * @author darkness
     * @date 2022/6/6 16:42
     * @version 1.0
     * @param ip
     * @return com.rapidark.framework.commons.model.ResultBody<com.rapidark.cloud.base.client.model.OpenClient>
     */
    @GetMapping("/openClient/queryOpenClientByIp")
    ResultBody<OpenApp> queryAppByIp(@RequestParam("ip") String ip);

    /**
     * 获取应用开发配置信息
     *
     * @param appId
     * @return
     */
    @GetMapping("/app/client/{appId}/info")
    ResultBody<OpenClientDetails> getAppClientInfo(@PathVariable("appId") String appId);

    /**
     * 查询客户端注册的所有应用
     * @author darkness
     * @date 2022/6/6 13:43
     * @version 1.0
     * @param appId
     * @return com.rapidark.framework.commons.model.ResultBody<java.util.List < com.rapidark.cloud.gateway.manage.service.dto.GatewayAppRouteRegServer>>
     */
    @GetMapping(value = "/openClient/queryClientRegisterAppsByAppId")
    ResultBody<List<GatewayAppRouteRegServer>> queryClientRegisterAppsByAppId(@RequestParam("appId") String appId);


}
