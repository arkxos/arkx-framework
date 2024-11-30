package com.rapidark.cloud.base.client.model.entity;


import com.rapidark.framework.data.jpa.entity.IdLongEntity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

/**
 * 系统权限-角色关联
 *
 * @author liuyadu
 */
@Data
@Entity
@Table(name="base_authority_role")
public class BaseAuthorityRole extends IdLongEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    private Long authorityId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 过期时间:null表示长期
     */
    private Date expireTime;

}
