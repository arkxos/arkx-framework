package com.rapidark.cloud.base.client.model.entity;

import com.rapidark.framework.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
public class BaseRole extends BaseEntity {

    private static final long serialVersionUID = 5197785628543375591L;

    /**
     * 角色ID
     */
    @Id
    @Column(name = "role_Id")
    @ApiModelProperty(value = "roleId")
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

    /**
     * 状态:0-无效 1-有效
     */
    private Integer status;

    /**
     * 保留数据0-否 1-是 不允许删除
     */
    private Integer isPersist;

}
