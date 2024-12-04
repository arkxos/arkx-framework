package com.rapidark.platform.system.api.entity;


import com.rapidark.framework.data.jpa.entity.IdLongEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * 系统角色-角色与用户关联
 *
 * @author: liuyadu
 * @date: 2018/10/24 16:21
 * @description:
 */
@Data
@Entity
@Table(name="sys_user_role")
@Schema(description = "用户角色")
public class SysUserRole extends IdLongEntity {

    private static final long serialVersionUID = -667816444278087761L;

    /**
     * 系统用户ID
     */
	@Schema(description = "用户id")
    private Long userId;

    /**
     * 角色ID
     */
	@Schema(description = "角色id")
    private Long roleId;

}
