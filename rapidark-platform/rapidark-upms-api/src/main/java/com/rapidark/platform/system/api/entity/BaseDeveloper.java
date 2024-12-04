package com.rapidark.platform.system.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.rapidark.framework.data.jpa.entity.IdLongEntity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

/**
 * 系统用户-管理员信息
 * @author darkness
 * @date 2022/6/24 14:24
 * @version 1.0
 */
@Entity
@Getter
@Setter
@Table(name="base_developer")
public class BaseDeveloper extends IdLongEntity {

    private static final long serialVersionUID = -735161640894047414L;

    /**
     * 开发者类型:isp-服务提供商 dev-自研开发者
     */
    private Integer type;

    private String companyName; // 公司名称

    private String personName; // 联系人

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 登陆名
     */
    private String userName;

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
     * 描述
     */
    private String userDesc;

    /**
     * 密码
     */
    @JsonIgnore
    @Transient
    private String password;

}
