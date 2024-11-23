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

import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.rapidark.cloud.platform.admin.api.entity.SysDept;
import com.rapidark.cloud.platform.admin.api.vo.DeptExcelVo;
import com.rapidark.cloud.platform.admin.service.SysDeptService;
import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.common.log.annotation.SysLog;
import com.rapidark.cloud.platform.common.security.annotation.HasPermission;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 部门管理 前端控制器
 * </p>
 *
 * @author lengleng
 * @since 2018-01-20
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dept")
@Tag(description = "dept", name = "部门管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysDeptController {

	private final SysDeptService sysDeptService;

	/**
	 * 通过ID查询
	 * @param id ID
	 * @return SysDept
	 */
	@GetMapping("/{id}")
	public ResponseResult getById(@PathVariable Long id) {
		return ResponseResult.ok(sysDeptService.getById(id));
	}

	/**
	 * 查询全部部门
	 */
	@GetMapping("/list")
	public ResponseResult list() {
		return ResponseResult.ok(sysDeptService.list());
	}

	/**
	 * 返回树形菜单集合
	 * @param deptName 部门名称
	 * @return 树形菜单
	 */
	@GetMapping(value = "/tree")
	public ResponseResult getTree(String deptName) {
		return ResponseResult.ok(sysDeptService.selectTree(deptName));
	}

	/**
	 * 添加
	 * @param sysDept 实体
	 * @return success/false
	 */
	@SysLog("添加部门")
	@PostMapping
	@HasPermission("sys_dept_add")
	public ResponseResult save(@Valid @RequestBody SysDept sysDept) {
		return ResponseResult.ok(sysDeptService.save(sysDept));
	}

	/**
	 * 删除
	 * @param id ID
	 * @return success/false
	 */
	@SysLog("删除部门")
	@DeleteMapping("/{id}")
	@HasPermission("sys_dept_del")
	public ResponseResult removeById(@PathVariable Long id) {
		return ResponseResult.ok(sysDeptService.removeDeptById(id));
	}

	/**
	 * 编辑
	 * @param sysDept 实体
	 * @return success/false
	 */
	@SysLog("编辑部门")
	@PutMapping
	@HasPermission("sys_dept_edit")
	public ResponseResult update(@Valid @RequestBody SysDept sysDept) {
		sysDept.setUpdateTime(LocalDateTime.now());
		return ResponseResult.ok(sysDeptService.updateById(sysDept));
	}

	/**
	 * 查收子级列表
	 * @return 返回子级
	 */
	@GetMapping(value = "/getDescendantList/{deptId}")
	public ResponseResult getDescendantList(@PathVariable Long deptId) {
		return ResponseResult.ok(sysDeptService.listDescendant(deptId));
	}

	/**
	 * 导出部门
	 * @return
	 */
	@ResponseExcel
	@GetMapping("/export")
	public List<DeptExcelVo> export() {
		return sysDeptService.listExcelVo();
	}

	/**
	 * 导入部门
	 * @param excelVOList
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("import")
	public ResponseResult importDept(@RequestExcel List<DeptExcelVo> excelVOList, BindingResult bindingResult) {

		return sysDeptService.importDept(excelVOList, bindingResult);
	}

}
