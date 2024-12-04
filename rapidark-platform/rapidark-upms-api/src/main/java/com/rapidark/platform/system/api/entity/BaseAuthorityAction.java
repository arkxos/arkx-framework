package com.rapidark.platform.system.api.entity;


import com.rapidark.framework.data.jpa.entity.IdLongEntity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * 系统权限-功能操作关联表
 *
 * @author: liuyadu
 * @date: 2018/10/24 16:21
 * @description:
 */
@Data
@Entity
@Table(name="base_authority_action")
public class BaseAuthorityAction extends IdLongEntity {

    private static final long serialVersionUID = 1471599074044557390L;

    /**
     * 操作资源ID
     */
    private Long actionId;

    /**
     * 权限ID
     */
    private Long authorityId;

}
