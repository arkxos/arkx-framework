package com.bsd.org.server.service.feign;

import com.rapidark.platform.system.api.constants.BaseConstants;
import com.rapidark.platform.system.api.service.IBaseUserServiceClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @Author: linrongxin
 * @Date: 2019/9/21 16:22
 */
@Component
@FeignClient(value = BaseConstants.BASE_SERVER)
public interface BaseUserServiceClient extends IBaseUserServiceClient {

}
