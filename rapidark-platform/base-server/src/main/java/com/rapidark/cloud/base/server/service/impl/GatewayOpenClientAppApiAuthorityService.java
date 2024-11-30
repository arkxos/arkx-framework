package com.rapidark.cloud.base.server.service.impl;

import com.rapidark.cloud.base.client.model.entity.GatewayOpenClientAppApiAuthority;
import com.rapidark.framework.data.jpa.service.BaseService;
//import com.rapidark.cloud.gateway.manage.repository.ClientServerRegisterRepository;
import com.rapidark.cloud.gateway.manage.repository.GatewayOpenClientAppApiAuthorityRepository;
import org.springframework.stereotype.Service;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/1 16:25
 */
@Service
public class GatewayOpenClientAppApiAuthorityService extends BaseService<GatewayOpenClientAppApiAuthority, Long, GatewayOpenClientAppApiAuthorityRepository> {

    public void deleteByAppId(String appId) {
        this.entityRepository.deleteByAppId(appId);
    }

    public void deleteByAppIdAndAppSystemCode(String appId, String appSystemCode) {
        this.entityRepository.deleteByAppIdAndAppSystemCode(appId, appSystemCode);
    }
}
