package com.rapidark.cloud.base.client.model.entity;


import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;
import com.rapidark.framework.data.jpa.entity.IdLongEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name="base_role_user")
public class BaseRoleUser extends IdLongEntity {

    private static final long serialVersionUID = -667816444278087761L;

    /**
     * 系统用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

}
