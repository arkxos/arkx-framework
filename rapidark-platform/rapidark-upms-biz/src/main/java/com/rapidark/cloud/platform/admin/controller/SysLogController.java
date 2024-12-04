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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.rapidark.platform.system.api.dto.SysLogDTO;
import com.rapidark.platform.system.api.entity.SysLog;
import com.rapidark.cloud.platform.admin.service.SysLogService;
import com.rapidark.framework.common.model.ResponseResult;
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
 * 日志表 前端控制器
 * </p>
 *
 * @author lengleng
 * @since 2017-11-20
 */
@RestController
@AllArgsConstructor
@RequestMapping("/log")
@Tag(description = "log", name = "日志管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysLogController {

	private final SysLogService sysLogService;

	/**
	 * 简单分页查询
	 * @param page 分页对象
	 * @param sysLog 系统日志
	 * @return
	 */
	@GetMapping("/page")
	public ResponseResult getLogPage(@ParameterObject Page page, @ParameterObject SysLogDTO sysLog) {
		return ResponseResult.ok(sysLogService.getLogByPage(page, sysLog));
	}

	/**
	 * 批量删除日志
	 * @param ids ID
	 * @return success/false
	 */
	@DeleteMapping
	@HasPermission("sys_log_del")
	public ResponseResult removeByIds(@RequestBody Long[] ids) {
		return ResponseResult.ok(sysLogService.removeBatchByIds(CollUtil.toList(ids)));
	}

	/**
	 * 插入日志
	 * @param sysLog 日志实体
	 * @return success/false
	 */
	@Inner
	@PostMapping("/save")
	public ResponseResult save(@Valid @RequestBody SysLog sysLog) {
		return ResponseResult.ok(sysLogService.saveLog(sysLog));
	}

	/**
	 * 导出excel 表格
	 * @param sysLog 查询条件
	 * @return
	 */
	@ResponseExcel
	@GetMapping("/export")
	@HasPermission("sys_log_export")
	public List<SysLog> export(SysLogDTO sysLog) {
		return sysLogService.getList(sysLog);
	}

}
