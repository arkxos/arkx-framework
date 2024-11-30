package com.rapidark.cloud.base.client.model.entity;


import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Table(name="base_role")
public class BaseRole extends AbstractIdLongEntity {

    private static final long serialVersionUID = 5197785628543375591L;

    /**
     * 角色ID
     */
    @Id
    @Column(name = "role_Id")
    @Schema(title = "roleId")
    private Long roleId;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
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
