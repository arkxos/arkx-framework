package com.rapidark.cloud.gateway.manage.service;

import com.rapidark.cloud.base.client.model.GatewayOpenClientAppApiAuthority;
import com.rapidark.cloud.gateway.formwork.base.BaseService;
//import com.rapidark.cloud.gateway.manage.repository.ClientServerRegisterRepository;
import com.rapidark.cloud.gateway.manage.repository.GatewayOpenClientAppApiAuthorityRepository;
import org.springframework.stereotype.Service;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/1 16:25
 */
@Service
public class GatewayOpenClientAppApiAuthorityService extends BaseService<GatewayOpenClientAppApiAuthority, String, GatewayOpenClientAppApiAuthorityRepository> {

    public void deleteByAppId(String appId) {
        this.entityRepository.deleteByAppId(appId);
    }

    public void deleteByAppIdAndAppSystemCode(String appId, String appSystemCode) {
        this.entityRepository.deleteByAppIdAndAppSystemCode(appId, appSystemCode);
    }
}
