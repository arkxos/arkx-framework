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
package com.rapidark.cloud.platform.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rapidark.cloud.platform.admin.api.entity.SysDictItem;
import com.rapidark.cloud.platform.admin.mapper.SysDictItemRepository;
import com.rapidark.cloud.platform.admin.service.SysDictItemService;
import com.rapidark.cloud.platform.admin.service.SysDictService;
import com.rapidark.cloud.platform.common.core.constant.CacheConstants;
import com.rapidark.cloud.platform.common.core.util.ResponseResult;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * 字典项
 *
 * @author lengleng
 * @date 2019/03/19
 */
@Service
@AllArgsConstructor
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemRepository, SysDictItem> implements SysDictItemService {

	private final SysDictService dictService;

	/**
	 * 删除字典项
	 * @param id 字典项ID
	 * @return
	 */
	@Override
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public ResponseResult delete(Long id) {
		// 根据ID查询字典ID
		SysDictItem dictItem = this.getById(id);
//		SysDict dict = dictService.getById(dictItem.getDictId());
		// 系统内置
//		if (DictTypeEnum.SYSTEM.getType().equals(dict.getSystemFlag())) {
//			return ResponseResult.failed(MsgUtils.getMessage(ErrorCodes.SYS_DICT_DELETE_SYSTEM));
//		}
		return ResponseResult.ok(this.removeById(id));
	}

	/**
	 * 更新字典项
	 * @param item 字典项
	 * @return
	 */
	@Override
	@CacheEvict(value = CacheConstants.DICT_DETAILS, key = "#item.dictCode")
	public ResponseResult update(SysDictItem item) {
		// 查询字典
//		SysDict dict = dictService.getById(item.getDictId());
//		// 系统内置
//		if (DictTypeEnum.SYSTEM.getType().equals(dict.getSystemFlag())) {
//			return ResponseResult.failed(MsgUtils.getMessage(ErrorCodes.SYS_DICT_UPDATE_SYSTEM));
//		}
		return ResponseResult.ok(this.updateById(item));
	}

}
