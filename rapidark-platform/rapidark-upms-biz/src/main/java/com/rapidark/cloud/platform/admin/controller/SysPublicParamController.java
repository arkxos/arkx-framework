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

package com.rapidark.cloud.platform.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.rapidark.platform.system.api.entity.SysPublicParam;
import com.rapidark.cloud.platform.admin.service.SysPublicParamService;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.cloud.platform.common.log.annotation.SysLog;
import com.rapidark.cloud.platform.common.security.annotation.HasPermission;
import com.rapidark.cloud.platform.common.security.annotation.Inner;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公共参数
 *
 * @author Lucky
 * @date 2019-04-29
 */
@RestController
@AllArgsConstructor
@RequestMapping("/param")
@Tag(description = "param", name = "公共参数配置")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysPublicParamController {

	private final SysPublicParamService sysPublicParamService;

	/**
	 * 通过key查询公共参数值
	 * @param publicKey
	 * @return
	 */
	@Inner(value = false)
	@Operation(description = "查询公共参数值", summary = "根据key查询公共参数值")
	@GetMapping("/publicValue/{publicKey}")
	public ResponseResult publicKey(@PathVariable("publicKey") String publicKey) {
		return ResponseResult.ok(sysPublicParamService.getSysPublicParamKeyToValue(publicKey));
	}

	/**
	 * 分页查询
	 * @param page 分页对象
	 * @param sysPublicParam 公共参数
	 * @return
	 */
	@Operation(description = "分页查询", summary = "分页查询")
	@GetMapping("/page")
	public ResponseResult getSysPublicParamPage(@ParameterObject Page page, @ParameterObject SysPublicParam sysPublicParam) {
		LambdaUpdateWrapper<SysPublicParam> wrapper = Wrappers.<SysPublicParam>lambdaUpdate()
			.like(StrUtil.isNotBlank(sysPublicParam.getPublicName()), SysPublicParam::getPublicName,
					sysPublicParam.getPublicName())
			.like(StrUtil.isNotBlank(sysPublicParam.getPublicKey()), SysPublicParam::getPublicKey,
					sysPublicParam.getPublicKey())
			.eq(StrUtil.isNotBlank(sysPublicParam.getSystemFlag()), SysPublicParam::getSystemFlag,
					sysPublicParam.getSystemFlag());

		return ResponseResult.ok(sysPublicParamService.page(page, wrapper));
	}

	/**
	 * 通过id查询公共参数
	 * @param publicId id
	 * @return ResponseResult
	 */
	@Operation(description = "通过id查询公共参数", summary = "通过id查询公共参数")
	@GetMapping("/details/{publicId}")
	public ResponseResult getById(@PathVariable("publicId") Long publicId) {
		return ResponseResult.ok(sysPublicParamService.getById(publicId));
	}

	@GetMapping("/details")
	public ResponseResult getDetail(@ParameterObject SysPublicParam param) {
		return ResponseResult.ok(sysPublicParamService.getOne(Wrappers.query(param), false));
	}

	/**
	 * 新增公共参数
	 * @param sysPublicParam 公共参数
	 * @return ResponseResult
	 */
	@Operation(description = "新增公共参数", summary = "新增公共参数")
	@SysLog("新增公共参数")
	@PostMapping
	@HasPermission("sys_syspublicparam_add")
	public ResponseResult save(@RequestBody SysPublicParam sysPublicParam) {
		return ResponseResult.ok(sysPublicParamService.save(sysPublicParam));
	}

	/**
	 * 修改公共参数
	 * @param sysPublicParam 公共参数
	 * @return ResponseResult
	 */
	@Operation(description = "修改公共参数", summary = "修改公共参数")
	@SysLog("修改公共参数")
	@PutMapping
	@HasPermission("sys_syspublicparam_edit")
	public ResponseResult updateById(@RequestBody SysPublicParam sysPublicParam) {
		return sysPublicParamService.updateParam(sysPublicParam);
	}

	/**
	 * 通过id删除公共参数
	 * @param ids ids
	 * @return ResponseResult
	 */
	@Operation(description = "删除公共参数", summary = "删除公共参数")
	@SysLog("删除公共参数")
	@DeleteMapping
	@HasPermission("sys_syspublicparam_del")
	public ResponseResult removeById(@RequestBody Long[] ids) {
		return ResponseResult.ok(sysPublicParamService.removeParamByIds(ids));
	}

	/**
	 * 导出excel 表格
	 * @return
	 */
	@ResponseExcel
	@GetMapping("/export")
	@HasPermission("sys_syspublicparam_edit")
	public List<SysPublicParam> export() {
		return sysPublicParamService.list();
	}

	/**
	 * 同步参数
	 * @return ResponseResult
	 */
	@SysLog("同步参数")
	@PutMapping("/sync")
	@HasPermission("sys_syspublicparam_edit")
	public ResponseResult sync() {
		return sysPublicParamService.syncParamCache();
	}

}
