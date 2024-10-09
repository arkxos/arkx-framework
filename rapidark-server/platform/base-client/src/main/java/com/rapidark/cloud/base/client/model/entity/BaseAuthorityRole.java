package com.rapidark.cloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.common.mybatis.base.entity.AbstractEntity;
import lombok.Data;

import java.util.Date;

/**
 * 系统权限-角色关联
 *
 * @author liuyadu
 */
@Data
@TableName("base_authority_role")
public class BaseAuthorityRole extends AbstractEntity {

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
