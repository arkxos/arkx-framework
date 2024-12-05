package com.rapidark.cloud.uaa.admin.server.service.feign;

import com.rapidark.platform.system.api.constants.BaseConstants;
import com.rapidark.platform.system.api.service.IBaseUserServiceClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author: liuyadu
 * @date: 2018/10/24 16:49
 * @description:
 */
@Component
@FeignClient(value = BaseConstants.BASE_SERVER)
public interface BaseUserServiceClient extends IBaseUserServiceClient {

}
