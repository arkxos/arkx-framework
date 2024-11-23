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

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.rapidark.cloud.platform.admin.api.entity.SysDict;
import com.rapidark.cloud.platform.admin.api.entity.SysDictItem;
import com.rapidark.cloud.platform.admin.service.SysDictItemService;
import com.rapidark.cloud.platform.admin.service.SysDictService;
import com.rapidark.cloud.platform.common.core.constant.CacheConstants;
import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.common.log.annotation.SysLog;
import com.rapidark.cloud.platform.common.security.annotation.Inner;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author lengleng
 * @since 2019-03-19
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dict")
@Tag(description = "dict", name = "字典管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysDictController {

	private final SysDictService sysDictService;

	private final SysDictItemService sysDictItemService;

	/**
	 * 通过ID查询字典信息
	 * @param id ID
	 * @return 字典信息
	 */
	@GetMapping("/details/{id}")
	public ResponseResult getById(@PathVariable Long id) {
		return ResponseResult.ok(sysDictService.getById(id));
	}

	/**
	 * 查询字典信息
	 * @param query 查询信息
	 * @return 字典信息
	 */
	@GetMapping("/details")
	public ResponseResult getDetails(@ParameterObject SysDict query) {
		return ResponseResult.ok(sysDictService.getOne(Wrappers.query(query), false));
	}

	/**
	 * 分页查询字典信息
	 * @param page 分页对象
	 * @return 分页对象
	 */
	@GetMapping("/page")
	public ResponseResult<IPage> getDictPage(@ParameterObject Page page, @ParameterObject SysDict sysDict) {
		return ResponseResult.ok(sysDictService.page(page,
				Wrappers.<SysDict>lambdaQuery()
					.eq(StrUtil.isNotBlank(sysDict.getSystemFlag()), SysDict::getSystemFlag, sysDict.getSystemFlag())
					.like(StrUtil.isNotBlank(sysDict.getDictType()), SysDict::getDictType, sysDict.getDictType())));
	}

	/**
	 * 添加字典
	 * @param sysDict 字典信息
	 * @return success、false
	 */
	@SysLog("添加字典")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys_dict_add')")
	public ResponseResult save(@Valid @RequestBody SysDict sysDict) {
		sysDictService.save(sysDict);
		return ResponseResult.ok(sysDict);
	}

	/**
	 * 删除字典，并且清除字典缓存
	 * @param ids ID
	 * @return ResponseResult
	 */
	@SysLog("删除字典")
	@DeleteMapping
	@PreAuthorize("@pms.hasPermission('sys_dict_del')")
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public ResponseResult removeById(@RequestBody Long[] ids) {
		return ResponseResult.ok(sysDictService.removeDictByIds(ids));
	}

	/**
	 * 修改字典
	 * @param sysDict 字典信息
	 * @return success/false
	 */
	@PutMapping
	@SysLog("修改字典")
	@PreAuthorize("@pms.hasPermission('sys_dict_edit')")
	public ResponseResult updateById(@Valid @RequestBody SysDict sysDict) {
		return sysDictService.updateDict(sysDict);
	}

	/**
	 * 分页查询
	 * @param name 名称或者字典项
	 * @return
	 */
	@GetMapping("/list")
	public ResponseResult getDictList(String name) {
		return ResponseResult.ok(sysDictService.list(Wrappers.<SysDict>lambdaQuery()
			.like(StrUtil.isNotBlank(name), SysDict::getDictType, name)
			.or()
			.like(StrUtil.isNotBlank(name), SysDict::getDescription, name)));
	}

	/**
	 * 分页查询
	 * @param page 分页对象
	 * @param sysDictItem 字典项
	 * @return
	 */
	@GetMapping("/item/page")
	public ResponseResult getSysDictItemPage(Page page, SysDictItem sysDictItem) {
		return ResponseResult.ok(sysDictItemService.page(page, Wrappers.query(sysDictItem)));
	}

	/**
	 * 通过id查询字典项
	 * @param id id
	 * @return ResponseResult
	 */
	@GetMapping("/item/details/{id}")
	public ResponseResult getDictItemById(@PathVariable("id") Long id) {
		return ResponseResult.ok(sysDictItemService.getById(id));
	}

	/**
	 * 查询字典项详情
	 * @param query 查询条件
	 * @return ResponseResult
	 */
	@GetMapping("/item/details")
	public ResponseResult getDictItemDetails(SysDictItem query) {
		return ResponseResult.ok(sysDictItemService.getOne(Wrappers.query(query), false));
	}

	/**
	 * 新增字典项
	 * @param sysDictItem 字典项
	 * @return ResponseResult
	 */
	@SysLog("新增字典项")
	@PostMapping("/item")
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public ResponseResult save(@RequestBody SysDictItem sysDictItem) {
		return ResponseResult.ok(sysDictItemService.save(sysDictItem));
	}

	/**
	 * 修改字典项
	 * @param sysDictItem 字典项
	 * @return ResponseResult
	 */
	@SysLog("修改字典项")
	@PutMapping("/item")
	public ResponseResult updateById(@RequestBody SysDictItem sysDictItem) {
		return sysDictItemService.updateDictItem(sysDictItem);
	}

	/**
	 * 通过id删除字典项
	 * @param id id
	 * @return ResponseResult
	 */
	@SysLog("删除字典项")
	@DeleteMapping("/item/{id}")
	public ResponseResult removeDictItemById(@PathVariable Long id) {
		return sysDictItemService.removeDictItem(id);
	}

	/**
	 * 同步缓存字典
	 * @return ResponseResult
	 */
	@SysLog("同步字典")
	@PutMapping("/sync")
	public ResponseResult sync() {
		return sysDictService.syncDictCache();
	}

	@ResponseExcel
	@GetMapping("/export")
	public List<SysDictItem> export(SysDictItem sysDictItem) {
		return sysDictItemService.list(Wrappers.query(sysDictItem));
	}

	/**
	 * 通过字典类型查找字典
	 * @param type 类型
	 * @return 同类型字典
	 */
	@GetMapping("/type/{type}")
	@Cacheable(value = CacheConstants.DICT_DETAILS, key = "#type", unless = "#result.data.isEmpty()")
	public ResponseResult<List<SysDictItem>> getDictByType(@PathVariable String type) {
		return ResponseResult.ok(sysDictItemService.list(Wrappers.<SysDictItem>query().lambda().eq(SysDictItem::getDictType, type)));
	}

	/**
	 * 通过字典类型查找字典 (针对feign调用) TODO: 兼容性方案，代码重复
	 * @param type 类型
	 * @return 同类型字典
	 */
	@Inner
	@GetMapping("/remote/type/{type}")
	@Cacheable(value = CacheConstants.DICT_DETAILS, key = "#type", unless = "#result.data.isEmpty()")
	public ResponseResult<List<SysDictItem>> getRemoteDictByType(@PathVariable String type) {
		return ResponseResult.ok(sysDictItemService.list(Wrappers.<SysDictItem>query().lambda().eq(SysDictItem::getDictType, type)));
	}

}
