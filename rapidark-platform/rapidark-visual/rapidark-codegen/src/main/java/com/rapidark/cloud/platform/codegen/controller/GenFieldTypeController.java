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

package com.rapidark.cloud.platform.codegen.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.rapidark.cloud.platform.codegen.entity.GenFieldType;
import com.rapidark.cloud.platform.codegen.service.GenFieldTypeService;
import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.common.log.annotation.SysLog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 列属性
 *
 * @author pigx code generator
 * @date 2023-02-06 20:16:01
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/fieldtype")
@Tag(description = "fieldtype", name = "列属性管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class GenFieldTypeController {

	private final GenFieldTypeService fieldTypeService;

	/**
	 * 分页查询
	 * @param page 分页对象
	 * @param fieldType 列属性
	 * @return
	 */
	@Operation(summary = "分页查询", description = "分页查询")
	@GetMapping("/page")
	public ResponseResult getFieldTypePage(Page page, GenFieldType fieldType) {
		return ResponseResult.ok(fieldTypeService.page(page,
				Wrappers.<GenFieldType>lambdaQuery()
					.like(StrUtil.isNotBlank(fieldType.getColumnType()), GenFieldType::getColumnType,
							fieldType.getColumnType())));
	}

	@Operation(summary = "查询列表", description = "查询列表")
	@GetMapping("/list")
	public ResponseResult list(GenFieldType fieldType) {
		return ResponseResult.ok(fieldTypeService.list(Wrappers.query(fieldType)));
	}

	/**
	 * 通过id查询列属性
	 * @param id id
	 * @return ResponseResult
	 */
	@Operation(summary = "通过id查询", description = "通过id查询")
	@GetMapping("/details/{id}")
	public ResponseResult getById(@PathVariable("id") Long id) {
		return ResponseResult.ok(fieldTypeService.getById(id));
	}

	@GetMapping("/details")
	public ResponseResult getDetails(GenFieldType query) {
		return ResponseResult.ok(fieldTypeService.getOne(Wrappers.query(query), false));
	}

	/**
	 * 新增列属性
	 * @param fieldType 列属性
	 * @return ResponseResult
	 */
	@Operation(summary = "新增列属性", description = "新增列属性")
	@SysLog("新增列属性")
	@PostMapping
	public ResponseResult save(@RequestBody GenFieldType fieldType) {
		return ResponseResult.ok(fieldTypeService.save(fieldType));
	}

	/**
	 * 修改列属性
	 * @param fieldType 列属性
	 * @return ResponseResult
	 */
	@Operation(summary = "修改列属性", description = "修改列属性")
	@SysLog("修改列属性")
	@PutMapping
	public ResponseResult updateById(@RequestBody GenFieldType fieldType) {
		return ResponseResult.ok(fieldTypeService.updateById(fieldType));
	}

	/**
	 * 通过id删除列属性
	 * @param ids id
	 * @return ResponseResult
	 */
	@Operation(summary = "通过id删除列属性", description = "通过id删除列属性")
	@SysLog("通过id删除列属性")
	@DeleteMapping
	public ResponseResult removeById(@RequestBody Long[] ids) {
		return ResponseResult.ok(fieldTypeService.removeBatchByIds(CollUtil.toList(ids)));
	}

	/**
	 * 导出excel 表格
	 * @param fieldType 查询条件
	 * @return excel 文件流
	 */
	@ResponseExcel
	@GetMapping("/export")
	public List<GenFieldType> export(GenFieldType fieldType) {
		return fieldTypeService.list(Wrappers.query(fieldType));
	}

}
