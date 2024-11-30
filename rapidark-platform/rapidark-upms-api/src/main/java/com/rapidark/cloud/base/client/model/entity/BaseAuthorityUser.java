package com.rapidark.cloud.base.client.model.entity;


import com.rapidark.framework.data.jpa.entity.IdLongEntity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

/**
 * 系统权限-用户关联
 *
 * @author liuyadu
 */
@Data
@Entity
@Table(name="base_authority_user")
public class BaseAuthorityUser extends IdLongEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    private Long authorityId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 过期时间
     */
    private Date expireTime;

}
