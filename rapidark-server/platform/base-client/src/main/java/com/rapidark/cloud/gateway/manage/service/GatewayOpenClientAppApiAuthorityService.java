package com.rapidark.cloud.gateway.manage.service;

import com.rapidark.cloud.base.client.model.GatewayOpenClientAppApiAuthority;
import com.rapidark.cloud.gateway.formwork.base.BaseService;
import com.rapidark.cloud.gateway.formwork.entity.ClientServerRegister;
//import com.rapidark.cloud.gateway.manage.repository.ClientServerRegisterRepository;
import com.rapidark.cloud.gateway.repository.GatewayOpenClientAppApiAuthorityRepository;
import org.springframework.stereotype.Service;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/1 16:25
 */
@Service
public class GatewayOpenClientAppApiAuthorityService extends BaseService<GatewayOpenClientAppApiAuthority, String, GatewayOpenClientAppApiAuthorityRepository> {

    public void deleteByOpenClientId(String openClientId) {
        this.entityRepository.deleteByOpenClientId(openClientId);
    }

    public void deleteByOpenClientIdAndAppSystemCode(String openClientId, String appSystemCode) {
        this.entityRepository.deleteByOpenClientIdAndAppSystemCode(openClientId, appSystemCode);
    }
}
