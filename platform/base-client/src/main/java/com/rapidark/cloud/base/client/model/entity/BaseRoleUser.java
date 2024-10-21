package com.rapidark.cloud.base.client.model.entity;

import com.rapidark.framework.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 系统角色-角色与用户关联
 *
 * @author: liuyadu
 * @date: 2018/10/24 16:21
 * @description:
 */
@Data
@Entity
@Table(name="base_role_user")
public class BaseRoleUser extends BaseEntity {

    private static final long serialVersionUID = -667816444278087761L;

    @Id
    @Column(name = "ID")
    @ApiModelProperty(value = "ID")
    private Long id;

    /**
     * 系统用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

}
