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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.rapidark.cloud.platform.admin.api.dto.UserDTO;
import com.rapidark.cloud.platform.admin.api.entity.SysUser;
import com.rapidark.cloud.platform.admin.api.vo.UserExcelVO;
import com.rapidark.cloud.platform.admin.service.SysUserService;
import com.rapidark.cloud.platform.common.core.constant.CommonConstants;
import com.rapidark.cloud.platform.common.core.exception.ErrorCodes;
import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.common.core.util.MsgUtils;
import com.rapidark.cloud.platform.common.log.annotation.SysLog;
import com.rapidark.cloud.platform.common.security.annotation.HasPermission;
import com.rapidark.cloud.platform.common.security.annotation.Inner;
import com.rapidark.cloud.platform.common.security.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lengleng
 * @date 2018/12/16
 */
@RestController
@AllArgsConstructor
@Tag(description = "user", name = "用户管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysUserController {

	private final SysUserService userService;

	/**
	 * 获取指定用户全部信息
	 * @return 用户信息
	 */
	@Inner
	@GetMapping(value = { "/user/info/query" })
	public ResponseResult info(@RequestParam(required = false) String username, @RequestParam(required = false) String phone) {
		SysUser user = userService.getOne(Wrappers.<SysUser>query()
			.lambda()
			.eq(StrUtil.isNotBlank(username), SysUser::getUsername, username)
			.eq(StrUtil.isNotBlank(phone), SysUser::getPhone, phone));
		if (user == null) {
			return ResponseResult.failed(MsgUtils.getMessage(ErrorCodes.SYS_USER_USERINFO_EMPTY, username));
		}
		return ResponseResult.ok(userService.findUserInfo(user));
	}

	/**
	 * 获取当前用户全部信息
	 * @return 用户信息
	 * @see SysUserController#currentUserInfo
	 */
	@Deprecated
	@GetMapping(value = { "/user/info" })
	public ResponseResult info() {
		return currentUserInfo();
	}

	/**
	 * 获取当前用户全部信息
	 * @return 用户信息
	 */
	@GetMapping(value = { "/user/currentUserInfo" })
	public ResponseResult currentUserInfo() {
		String username = SecurityUtils.getUser().getUsername();
		SysUser user = userService.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));
		if (user == null) {
			return ResponseResult.failed(MsgUtils.getMessage(ErrorCodes.SYS_USER_QUERY_ERROR));
		}
		return ResponseResult.ok(userService.findUserInfo(user));
	}

	/**
	 * 通过ID查询用户信息
	 * @param id ID
	 * @return 用户信息
	 */
	@GetMapping("/user/details/{id}")
	public ResponseResult user(@PathVariable Long id) {
		return ResponseResult.ok(userService.selectUserVoById(id));
	}

	/**
	 * 查询用户信息
	 * @param query 查询条件
	 * @return 不为空返回用户名
	 */
	@Inner(value = false)
	@GetMapping("/user/details")
	public ResponseResult getDetails(@ParameterObject SysUser query) {
		SysUser sysUser = userService.getOne(Wrappers.query(query), false);
		return ResponseResult.ok(sysUser == null ? null : CommonConstants.SUCCESS);
	}

	/**
	 * 删除用户信息
	 * @param ids ID
	 * @return ResponseResult
	 */
	@SysLog("删除用户信息")
	@DeleteMapping("/user")
	@HasPermission("sys_user_del")
	@Operation(summary = "删除用户", description = "根据ID删除用户")
	public ResponseResult userDel(@RequestBody Long[] ids) {
		return ResponseResult.ok(userService.deleteUserByIds(ids));
	}

	/**
	 * 添加用户
	 * @param userDto 用户信息
	 * @return success/false
	 */
	@SysLog("添加用户")
	@PostMapping("/user")
	@HasPermission("sys_user_add")
	public ResponseResult user(@RequestBody UserDTO userDto) {
		return ResponseResult.ok(userService.saveUser(userDto));
	}

	/**
	 * 更新用户信息
	 * @param userDto 用户信息
	 * @return ResponseResult
	 */
	@SysLog("更新用户信息")
	@PutMapping("/user")
	@HasPermission("sys_user_edit")
	public ResponseResult updateUser(@Valid @RequestBody UserDTO userDto) {
		return ResponseResult.ok(userService.updateUser(userDto));
	}

	/**
	 * 分页查询用户
	 * @param page 参数集
	 * @param userDTO 查询参数列表
	 * @return 用户集合
	 */
	@GetMapping("/user/page")
	public ResponseResult getUserPage(@ParameterObject Page page, @ParameterObject UserDTO userDTO) {
		return ResponseResult.ok(userService.getUsersWithRolePage(page, userDTO));
	}

	/**
	 * 修改个人信息
	 * @param userDto userDto
	 * @return success/false
	 */
	@SysLog("修改个人信息")
	@PutMapping("/user/edit")
	public ResponseResult updateUserInfo(@Valid @RequestBody UserDTO userDto) {
		return userService.updateUserInfo(userDto);
	}

	/**
	 * 导出excel 表格
	 * @param userDTO 查询条件
	 * @return
	 */
	@ResponseExcel
	@GetMapping("/user/export")
	@HasPermission("sys_user_export")
	public List export(UserDTO userDTO) {
		return userService.listUser(userDTO);
	}

	/**
	 * 导入用户
	 * @param excelVOList 用户列表
	 * @param bindingResult 错误信息列表
	 * @return ResponseResult
	 */
	@PostMapping("/user/import")
	@HasPermission("sys_user_export")
	public ResponseResult importUser(@RequestExcel List<UserExcelVO> excelVOList, BindingResult bindingResult) {
		return userService.importUser(excelVOList, bindingResult);
	}

	/**
	 * 锁定指定用户
	 * @param username 用户名
	 * @return ResponseResult
	 */
	@PutMapping("/user/lock/{username}")
	public ResponseResult lockUser(@PathVariable String username) {
		return userService.lockUser(username);
	}

	@PutMapping("/user/password")
	public ResponseResult password(@RequestBody UserDTO userDto) {
		String username = SecurityUtils.getUser().getUsername();
		userDto.setUsername(username);
		return userService.changePassword(userDto);
	}

	@PostMapping("/user/check")
	public ResponseResult check(String password) {
		return userService.checkPassword(password);
	}

}
