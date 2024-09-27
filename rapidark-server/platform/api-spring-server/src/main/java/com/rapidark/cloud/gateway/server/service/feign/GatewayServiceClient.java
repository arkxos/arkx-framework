package com.rapidark.cloud.gateway.server.service.feign;

import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.base.client.service.IGatewayServiceClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 *
 * @author darkness
 * @date 2022/5/14 17:39
 * @version 1.0
 */
@Component
@FeignClient(value = BaseConstants.BASE_SERVER)
public interface GatewayServiceClient extends IGatewayServiceClient {

}
