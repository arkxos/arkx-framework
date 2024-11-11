package com.rapidark.cloud.base.client.model.entity;


import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 系统用户-登录账号
 * @author darkness
 * @date 2022/5/27 11:46
 * @version 1.0
 */
@Data
@Entity
@Table(name="base_account")
public class BaseAccount extends AbstractIdLongEntity {

    private static final long serialVersionUID = -4484479600033295192L;

    @Id
    @Column(name = "account_id")
    @ApiModelProperty(value = "accountId")
    private Long accountId;

    /**
     * 系统用户Id
     */
    private Long userId;

    /**
     * 标识：手机号、邮箱、 系统用户名、或第三方应用的唯一标识
     */
    private String account;

    /**
     * 密码凭证：站内的保存密码、站外的不保存或保存token）
     */
    private String password;

    /**
     * 登录类型:password-密码、mobile-手机号、email-邮箱、weixin-微信、weibo-微博、qq-等等
     */
    private String accountType;

    /**
     * 注册IP
     */
    private String registerIp;

    /**
     * 账号域
     */
    private String domain;

    public BaseAccount() {

    }

    public BaseAccount(Long userId, String account, String password, String accountType, String domain, String registerIp) {
        this.userId = userId;
        this.account = account;
        this.password = password;
        this.accountType = accountType;
        this.domain = domain;
        this.registerIp = registerIp;
    }

    @Override
    public Long getId() {
        return accountId;
    }

    @Override
    public void setId(Long id) {
        this.accountId = id;
    }
}
