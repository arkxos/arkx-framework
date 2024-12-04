package com.rapidark.platform.system.api.entity;


import com.rapidark.framework.data.jpa.entity.IdLongEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 系统权限-角色关联
 *
 * @author liuyadu
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name="sys_role_authority")
@Schema(description = "角色授权")
public class SysRoleAuthority extends IdLongEntity {

    private static final long serialVersionUID = 1L;

	@Schema(description = "角色id")
	private Long roleId;

	@Schema(description = "权限ID")
    private Long authorityId;

    /**
     * 过期时间:null表示长期
     */
    private Date expireTime;

}
