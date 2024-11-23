/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */

package com.rapidark.cloud.platform.admin.api.feign;

import com.rapidark.cloud.platform.admin.api.entity.SysOauthClientDetails;
import com.rapidark.cloud.platform.common.core.constant.ServiceNameConstants;
import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.common.feign.annotation.NoToken;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author lengleng
 * @date 2020/12/05
 */
@FeignClient(contextId = "remoteClientDetailsService", value = ServiceNameConstants.UPMS_SERVICE)
public interface RemoteClientDetailsService {

	/**
	 * 通过clientId 查询客户端信息 (未登录，需要无token 内部调用)
	 * @param clientId 用户名
	 * @return ResponseResult
	 */
	@NoToken
	@GetMapping("/client/getClientDetailsById/{clientId}")
	ResponseResult<SysOauthClientDetails> getClientDetailsById(@PathVariable("clientId") String clientId);

}
