package com.rapidark.platform.system.api.entity;


import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 系统角色-基础信息
 *
 * @author: liuyadu
 * @date: 2018/10/24 16:21
 * @description:
 */
@Data
@Entity
@Table(name="sys_role")
public class SysRole extends AbstractIdLongEntity {

    private static final long serialVersionUID = 5197785628543375591L;

    /**
     * 角色ID
     */
    @Id
    @Column(name = "role_Id")
	@Schema(description = "角色编号")
    private Long roleId;

	@NotBlank(message = "角色标识不能为空")
	@Schema(description = "角色标识")
    private String roleCode;

	@NotBlank(message = "角色名称不能为空")
	@Schema(description = "角色名称")
    private String roleName;

	@Schema(description = "角色描述")
    private String roleDesc;

    @Override
    public Long getId() {
        return roleId;
    }

    @Override
    public void setId(Long id) {
        this.roleId = id;
    }
}
