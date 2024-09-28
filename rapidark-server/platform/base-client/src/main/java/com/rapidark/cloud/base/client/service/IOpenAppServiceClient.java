package com.rapidark.cloud.base.client.service;

import com.rapidark.cloud.base.client.model.entity.OpenApp;
import com.rapidark.common.model.ResultBody;
import com.rapidark.common.security.OpenClientDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
     * 获取应用开发配置信息
     *
     * @param clientId
     * @return
     */
    @GetMapping("/app/client/{clientId}/info")
    ResultBody<OpenClientDetails> getAppClientInfo(@PathVariable("clientId") String clientId);
}
