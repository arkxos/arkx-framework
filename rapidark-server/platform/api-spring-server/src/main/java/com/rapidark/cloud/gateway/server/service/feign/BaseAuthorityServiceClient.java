package com.rapidark.cloud.gateway.server.service.feign;

import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.base.client.service.IBaseAuthorityServiceClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author: liuyadu
 * @date: 2018/10/24 16:49
 * @description:
 */
@Component
@FeignClient(value = BaseConstants.BASE_SERVER)
public interface BaseAuthorityServiceClient extends IBaseAuthorityServiceClient {

}
