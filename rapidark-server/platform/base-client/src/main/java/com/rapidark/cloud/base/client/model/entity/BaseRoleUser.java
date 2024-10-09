package com.rapidark.cloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.common.mybatis.base.entity.AbstractEntity;
import lombok.Data;

/**
 * 系统角色-角色与用户关联
 *
 * @author: liuyadu
 * @date: 2018/10/24 16:21
 * @description:
 */
@Data
@TableName("base_role_user")
public class BaseRoleUser extends AbstractEntity {

    private static final long serialVersionUID = -667816444278087761L;

    /**
     * 系统用户ID
     */
    private String userId;

    /**
     * 角色ID
     */
    private String roleId;

}
