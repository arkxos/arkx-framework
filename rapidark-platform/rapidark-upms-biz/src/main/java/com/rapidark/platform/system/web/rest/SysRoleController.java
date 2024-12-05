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

package com.rapidark.platform.system.web.rest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.rapidark.cloud.base.server.service.SysRoleService;
import com.rapidark.framework.commons.data.model.PageParams;
import com.rapidark.platform.system.api.entity.SysRole;
import com.rapidark.platform.system.api.vo.RoleExcelVO;
import com.rapidark.platform.system.api.vo.RoleVO;
import com.rapidark.cloud.platform.common.core.constant.CacheConstants;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.cloud.platform.common.log.annotation.SysLog;
import com.rapidark.cloud.platform.common.security.annotation.HasPermission;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lengleng
 * @date 2020-02-10
 */
@RestController
@AllArgsConstructor
@RequestMapping("/role")
@Tag(description = "role", name = "角色管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysRoleController {

	private final SysRoleService sysRoleService;

	/**
	 * 通过ID查询角色信息
	 * @param id ID
	 * @return 角色信息
	 */
	@GetMapping("/details/{id}")
	public ResponseResult getById(@PathVariable Long id) {
		return ResponseResult.ok(sysRoleService.findById(id));
	}

	/**
	 * 查询角色信息
	 * @param query 查询条件
	 * @return 角色信息
	 */
	@GetMapping("/details")
	public ResponseResult getDetails(@ParameterObject SysRole query) {
		return ResponseResult.ok(sysRoleService.findOneByExample(query));
	}

	/**
	 * 添加角色
	 * @param sysRole 角色信息
	 * @return success、false
	 */
	@SysLog("添加角色")
	@PostMapping
	@HasPermission("sys_role_add")
	@CacheEvict(value = CacheConstants.ROLE_DETAILS, allEntries = true)
	public ResponseResult save(@Valid @RequestBody SysRole sysRole) {
		sysRoleService.save(sysRole);
		return ResponseResult.ok();
	}

	/**
	 * 修改角色
	 * @param sysRole 角色信息
	 * @return success/false
	 */
	@SysLog("修改角色")
	@PutMapping
	@HasPermission("sys_role_edit")
	@CacheEvict(value = CacheConstants.ROLE_DETAILS, allEntries = true)
	public ResponseResult update(@Valid @RequestBody SysRole sysRole) {
		return ResponseResult.ok(sysRoleService.updateRole(sysRole));
	}

	/**
	 * 删除角色
	 * @param ids
	 * @return
	 */
	@SysLog("删除角色")
	@DeleteMapping
	@HasPermission("sys_role_del")
	@CacheEvict(value = CacheConstants.ROLE_DETAILS, allEntries = true)
	public ResponseResult removeById(@RequestBody Long[] ids) {
		sysRoleService.removeRoleByIds(ids);
		return ResponseResult.ok();
	}

	/**
	 * 获取角色列表
	 * @return 角色列表
	 */
	@GetMapping("/list")
	public ResponseResult listRoles() {
		return ResponseResult.ok(sysRoleService.findAll());
	}

	/**
	 * 分页查询角色信息
	 * @param page 分页对象
	 * @param role 查询条件
	 * @return 分页对象
	 */
	@GetMapping("/page")
	public ResponseResult getRolePage(PageParams page, SysRole role) {
		sysRoleService.findAll();
//		page, Wrappers.<SysRole>lambdaQuery()
//				.like(StrUtil.isNotBlank(role.getRoleName()), SysRole::getRoleName, role.getRoleName()));
		return ResponseResult.ok();
	}

	/**
	 * 更新角色菜单
	 * @param roleVo 角色对象
	 * @return success、false
	 */
	@SysLog("更新角色菜单")
	@PutMapping("/menu")
	@HasPermission("sys_role_perm")
	public ResponseResult saveRoleMenus(@RequestBody RoleVO roleVo) {
		return ResponseResult.ok(sysRoleService.updateRoleMenus(roleVo));
	}

	/**
	 * 通过角色ID 查询角色列表
	 * @param roleIdList 角色ID
	 * @return
	 */
	@PostMapping("/getRoleList")
	public ResponseResult getRoleList(@RequestBody List<Long> roleIdList) {
		return ResponseResult.ok(sysRoleService.findRolesByRoleIds(roleIdList, CollUtil.join(roleIdList, StrUtil.UNDERLINE)));
	}

	/**
	 * 导出excel 表格
	 * @return
	 */
	@ResponseExcel
	@GetMapping("/export")
	@HasPermission("sys_role_export")
	public List<RoleExcelVO> export() {
		return sysRoleService.listRole();
	}

	/**
	 * 导入角色
	 * @param excelVOList 角色列表
	 * @param bindingResult 错误信息列表
	 * @return ok fail
	 */
	@PostMapping("/import")
	@HasPermission("sys_role_export")
	public ResponseResult importRole(@RequestExcel List<RoleExcelVO> excelVOList, BindingResult bindingResult) {
		return sysRoleService.importRole(excelVOList, bindingResult);
	}

}
