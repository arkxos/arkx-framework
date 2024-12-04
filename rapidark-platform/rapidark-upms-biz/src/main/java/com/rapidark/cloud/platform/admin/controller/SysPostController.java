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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.rapidark.cloud.platform.admin.api.entity.SysPost;
import com.rapidark.cloud.platform.admin.api.vo.PostExcelVO;
import com.rapidark.cloud.platform.admin.service.SysPostService;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.cloud.platform.common.log.annotation.SysLog;
import com.rapidark.cloud.platform.common.security.annotation.HasPermission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位信息表
 *
 * @author fxz
 * @date 2022-03-26 12:50:43
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(description = "post", name = "岗位信息表管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysPostController {

	private final SysPostService sysPostService;

	/**
	 * 获取岗位列表
	 * @return 岗位列表
	 */
	@GetMapping("/list")
	public ResponseResult<List<SysPost>> listPosts() {
		return ResponseResult.ok(sysPostService.list(Wrappers.emptyWrapper()));
	}

	/**
	 * 分页查询
	 * @param page 分页对象
	 * @param sysPost 岗位信息表
	 * @return
	 */
	@Operation(description = "分页查询", summary = "分页查询")
	@GetMapping("/page")
	@HasPermission("sys_post_view")
	public ResponseResult getSysPostPage(@ParameterObject Page page, @ParameterObject SysPost sysPost) {
		return ResponseResult.ok(sysPostService.page(page, Wrappers.<SysPost>lambdaQuery()
			.like(StrUtil.isNotBlank(sysPost.getPostName()), SysPost::getPostName, sysPost.getPostName())));
	}

	/**
	 * 通过id查询岗位信息表
	 * @param postId id
	 * @return ResponseResult
	 */
	@Operation(description = "通过id查询", summary = "通过id查询")
	@GetMapping("/details/{postId}")
	@HasPermission("sys_post_view")
	public ResponseResult getById(@PathVariable("postId") Long postId) {
		return ResponseResult.ok(sysPostService.getById(postId));
	}

	/**
	 * 查询岗位信息信息
	 * @param query 查询条件
	 * @return ResponseResult
	 */
	@Operation(description = "查询角色信息", summary = "查询角色信息")
	@GetMapping("/details")
	@HasPermission("sys_post_view")
	public ResponseResult getDetails(SysPost query) {
		return ResponseResult.ok(sysPostService.getOne(Wrappers.query(query), false));
	}

	/**
	 * 新增岗位信息表
	 * @param sysPost 岗位信息表
	 * @return ResponseResult
	 */
	@Operation(description = "新增岗位信息表", summary = "新增岗位信息表")
	@SysLog("新增岗位信息表")
	@PostMapping
	@HasPermission("sys_post_add")
	public ResponseResult save(@RequestBody SysPost sysPost) {
		return ResponseResult.ok(sysPostService.save(sysPost));
	}

	/**
	 * 修改岗位信息表
	 * @param sysPost 岗位信息表
	 * @return ResponseResult
	 */
	@Operation(description = "修改岗位信息表", summary = "修改岗位信息表")
	@SysLog("修改岗位信息表")
	@PutMapping
	@HasPermission("sys_post_edit")
	public ResponseResult updateById(@RequestBody SysPost sysPost) {
		return ResponseResult.ok(sysPostService.updateById(sysPost));
	}

	/**
	 * 通过id删除岗位信息表
	 * @param ids id 列表
	 * @return ResponseResult
	 */
	@Operation(description = "通过id删除岗位信息表", summary = "通过id删除岗位信息表")
	@SysLog("通过id删除岗位信息表")
	@DeleteMapping
	@HasPermission("sys_post_del")
	public ResponseResult removeById(@RequestBody Long[] ids) {
		return ResponseResult.ok(sysPostService.removeBatchByIds(CollUtil.toList(ids)));
	}

	/**
	 * 导出excel 表格
	 * @return excel 文件流
	 */
	@ResponseExcel
	@GetMapping("/export")
	@HasPermission("sys_post_export")
	public List<PostExcelVO> export() {
		return sysPostService.listPost();
	}

	/**
	 * 导入岗位
	 * @param excelVOList 岗位列表
	 * @param bindingResult 错误信息列表
	 * @return ok fail
	 */
	@PostMapping("/import")
	@HasPermission("sys_post_export")
	public ResponseResult importRole(@RequestExcel List<PostExcelVO> excelVOList, BindingResult bindingResult) {
		return sysPostService.importPost(excelVOList, bindingResult);
	}

}
