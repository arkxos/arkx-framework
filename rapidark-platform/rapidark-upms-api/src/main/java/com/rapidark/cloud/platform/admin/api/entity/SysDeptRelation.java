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

package com.rapidark.cloud.platform.admin.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 部门关系表
 * </p>
 *
 * @author lengleng
 * @since 2018-01-22
 */
@Data
@Schema(description = "部门关系")
public class SysDeptRelation implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 祖先节点
	 */
	@Schema(description = "祖先节点")
	private Long ancestor;

	/**
	 * 后代节点
	 */
	@Schema(description = "后代节点")
	private Long descendant;

}
