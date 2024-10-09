package com.rapidark.cloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rapidark.common.annotation.TableAlias;
import com.rapidark.common.mybatis.base.entity.AbstractEntity;
import lombok.Data;

/**
 * 系统用户-基础信息
 *
 * @author liuyadu
 */
@Data
@TableAlias("user")
@TableName("base_user")
public class BaseUser extends AbstractEntity {

    private static final long serialVersionUID = -735161640894047414L;

    /**
     * 系统用户ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String userId;

    /**
     * 登陆名
     */
    private String userName;

    /**
     * 用户类型:super-超级管理员 normal-普通管理员
     */
    private String userType;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 描述
     */
    private String userDesc;

    /**
     * 密码
     */
    @JsonIgnore
    @TableField(exist = false)
    private String password;

    /**
     * 状态:0-禁用 1-正常 2-锁定
     */
    private Integer status;

}
