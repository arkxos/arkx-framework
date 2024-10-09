package com.rapidark.cloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.common.mybatis.base.entity.AbstractEntity;
import lombok.Data;

import java.util.Date;

/**
 * 系统权限-用户关联
 *
 * @author liuyadu
 */
@Data
@TableName("base_authority_user")
public class BaseAuthorityUser extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    private Long authorityId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 过期时间
     */
    private Date expireTime;

}
