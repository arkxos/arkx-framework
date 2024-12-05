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

package com.rapidark.platform.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rapidark.platform.system.api.entity.SysPublicParam;
import com.rapidark.framework.common.model.ResponseResult;

/**
 * 公共参数配置
 *
 * @author Lucky
 * @date 2019-04-29
 */
public interface SysPublicParamService extends IService<SysPublicParam> {

	/**
	 * 通过key查询公共参数指定值
	 * @param publicKey
	 * @return
	 */
	String getSysPublicParamKeyToValue(String publicKey);

	/**
	 * 更新参数
	 * @param sysPublicParam
	 * @return
	 */
	ResponseResult updateParam(SysPublicParam sysPublicParam);

	/**
	 * 删除参数
	 * @param publicIds 参数列表
	 * @return
	 */
	ResponseResult removeParamByIds(Long[] publicIds);

	/**
	 * 同步缓存
	 * @return ResponseResult
	 */
	ResponseResult syncParamCache();

}
