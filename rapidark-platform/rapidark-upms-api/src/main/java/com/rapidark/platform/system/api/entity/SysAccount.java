package com.rapidark.platform.system.api.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class SysAccount extends AbstractIdLongEntity {

    private static final long serialVersionUID = -4484479600033295192L;

    @Id
    @Column(name = "account_id")
    @Schema(title = "accountId")
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
	@Schema(description = "密码")
    private String password;

	@JsonIgnore
	@Schema(description = "随机盐")
	private String salt;

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

	/**
	 * 微信openid
	 */
	@Schema(description = "微信openid")
	private String wxOpenid;

	/**
	 * 微信小程序openId
	 */
	@Schema(description = "微信小程序openid")
	private String miniOpenid;

	/**
	 * QQ openid
	 */
	@Schema(description = "QQ openid")
	private String qqOpenid;

	/**
	 * 码云唯一标识
	 */
	@Schema(description = "码云唯一标识")
	private String giteeLogin;

	/**
	 * 开源中国唯一标识
	 */
	@Schema(description = "开源中国唯一标识")
	private String oscId;

    public SysAccount() {

    }

    public SysAccount(Long userId, String account, String password, String accountType, String domain, String registerIp) {
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
