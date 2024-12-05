package com.rapidark.cloud.portal.uaa.server.service.feign;

import com.rapidark.platform.system.api.constants.BaseConstants;
import com.rapidark.platform.system.api.service.IBaseDeveloperServiceClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author: liuyadu
 * @date: 2018/10/24 16:49
 * @description:
 */
@Component
@FeignClient(value = BaseConstants.BASE_SERVER)
public interface BaseDeveloperServiceClient extends IBaseDeveloperServiceClient {

}
