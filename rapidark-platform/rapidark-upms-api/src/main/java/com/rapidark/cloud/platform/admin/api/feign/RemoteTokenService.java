/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package com.rapidark.cloud.platform.admin.api.feign;

import com.rapidark.cloud.platform.common.core.constant.ServiceNameConstants;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.cloud.platform.common.feign.annotation.NoToken;

import com.rapidark.framework.common.utils.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author lengleng
 * @date 2018/9/4
 */
@FeignClient(contextId = "remoteTokenService", value = ServiceNameConstants.AUTH_SERVICE)
public interface RemoteTokenService {

	/**
	 * 分页查询token 信息
	 * @param params 分页参数
	 * @return page
	 */
	@NoToken
	@PostMapping("/token/page")
	ResponseResult<PageResult> getTokenPage(@RequestBody Map<String, Object> params);

	/**
	 * 删除token
	 * @param token token
	 * @return
	 */
	@NoToken
	@DeleteMapping("/token/remove/{token}")
	ResponseResult<Boolean> removeTokenById(@PathVariable("token") String token);

	/**
	 * 校验令牌获取用户信息
	 * @param token
	 * @return
	 */
	@NoToken
	@GetMapping("/token/query-token")
	ResponseResult<Map<String, Object>> queryToken(@RequestParam("token") String token);

}
