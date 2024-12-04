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

package com.rapidark.cloud.platform.admin.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.rapidark.cloud.platform.admin.api.entity.SysOauthClientDetails;
import com.rapidark.cloud.platform.admin.service.SysOauthClientDetailsService;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.cloud.platform.common.log.annotation.SysLog;
import com.rapidark.cloud.platform.common.security.annotation.HasPermission;
import com.rapidark.cloud.platform.common.security.annotation.Inner;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lengleng
 * @since 2018-05-15
 */
@RestController
@AllArgsConstructor
@RequestMapping("/client")
@Tag(description = "client", name = "客户端管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysClientController {

	private final SysOauthClientDetailsService clientDetailsService;

	/**
	 * 通过ID查询
	 * @param clientId clientId
	 * @return SysOauthClientDetails
	 */
	@GetMapping("/{clientId}")
	public ResponseResult getByClientId(@PathVariable String clientId) {
		SysOauthClientDetails details = clientDetailsService
			.getOne(Wrappers.<SysOauthClientDetails>lambdaQuery().eq(SysOauthClientDetails::getClientId, clientId));
		return ResponseResult.ok(details);
	}

	/**
	 * 简单分页查询
	 * @param page 分页对象
	 * @param sysOauthClientDetails 系统终端
	 * @return
	 */
	@GetMapping("/page")
	public ResponseResult getOauthClientDetailsPage(@ParameterObject Page page,
													@ParameterObject SysOauthClientDetails sysOauthClientDetails) {
		LambdaQueryWrapper<SysOauthClientDetails> wrapper = Wrappers.<SysOauthClientDetails>lambdaQuery()
			.like(StrUtil.isNotBlank(sysOauthClientDetails.getClientId()), SysOauthClientDetails::getClientId,
					sysOauthClientDetails.getClientId())
			.like(StrUtil.isNotBlank(sysOauthClientDetails.getClientSecret()), SysOauthClientDetails::getClientSecret,
					sysOauthClientDetails.getClientSecret());
		return ResponseResult.ok(clientDetailsService.page(page, wrapper));
	}

	/**
	 * 添加
	 * @param clientDetails 实体
	 * @return success/false
	 */
	@SysLog("添加终端")
	@PostMapping
	@HasPermission("sys_client_add")
	public ResponseResult add(@Valid @RequestBody SysOauthClientDetails clientDetails) {
		return ResponseResult.ok(clientDetailsService.saveClient(clientDetails));
	}

	/**
	 * 删除
	 * @param ids ID 列表
	 * @return success/false
	 */
	@SysLog("删除终端")
	@DeleteMapping
	@HasPermission("sys_client_del")
	public ResponseResult removeById(@RequestBody Long[] ids) {
		clientDetailsService.removeBatchByIds(CollUtil.toList(ids));
		return ResponseResult.ok();
	}

	/**
	 * 编辑
	 * @param clientDetails 实体
	 * @return success/false
	 */
	@SysLog("编辑终端")
	@PutMapping
	@HasPermission("sys_client_edit")
	public ResponseResult update(@Valid @RequestBody SysOauthClientDetails clientDetails) {
		return ResponseResult.ok(clientDetailsService.updateClientById(clientDetails));
	}

	@Inner
	@GetMapping("/getClientDetailsById/{clientId}")
	public ResponseResult getClientDetailsById(@PathVariable String clientId) {
		return ResponseResult.ok(clientDetailsService.getOne(
				Wrappers.<SysOauthClientDetails>lambdaQuery().eq(SysOauthClientDetails::getClientId, clientId), false));
	}

	/**
	 * 同步缓存字典
	 * @return ResponseResult
	 */
	@SysLog("同步终端")
	@PutMapping("/sync")
	public ResponseResult sync() {
		return clientDetailsService.syncClientCache();
	}

	/**
	 * 导出所有客户端
	 * @return excel
	 */
	@ResponseExcel
	@SysLog("导出excel")
	@GetMapping("/export")
	public List<SysOauthClientDetails> export(SysOauthClientDetails sysOauthClientDetails) {
		return clientDetailsService.list(Wrappers.query(sysOauthClientDetails));
	}

}
