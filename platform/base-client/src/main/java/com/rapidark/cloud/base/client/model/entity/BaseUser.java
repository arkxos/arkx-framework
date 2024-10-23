package com.rapidark.cloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * 系统用户-基础信息
 *
 * @author liuyadu
 */
@Data
@Entity
@Table(name="base_user")
public class BaseUser extends AbstractIdLongEntity {

    private static final long serialVersionUID = -735161640894047414L;

    /**
     * 系统用户ID
     */
    @Id
    @Column(name = "user_Id")
    @ApiModelProperty(value = "userId")
    private Long userId;

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
    @Transient
    private String password;

    @Override
    public Long getId() {
        return userId;
    }

    @Override
    public void setId(Long id) {

    }
}
