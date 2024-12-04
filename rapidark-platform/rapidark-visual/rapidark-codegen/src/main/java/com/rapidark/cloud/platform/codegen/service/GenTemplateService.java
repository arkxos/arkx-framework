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

package com.rapidark.cloud.platform.codegen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rapidark.cloud.platform.codegen.entity.GenTemplateEntity;
import com.rapidark.framework.common.model.ResponseResult;

/**
 * 模板
 *
 * @author PIG
 * @date 2023-02-21 17:15:44
 */
public interface GenTemplateService extends IService<GenTemplateEntity> {

	/**
	 * 检查版本
	 * @return {@link ResponseResult }
	 */
	ResponseResult checkVersion();

	/**
	 * 在线更新
	 * @return {@link ResponseResult }
	 */
	ResponseResult onlineUpdate();

}
