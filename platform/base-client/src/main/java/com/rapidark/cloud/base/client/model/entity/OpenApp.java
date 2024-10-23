package com.rapidark.cloud.base.client.model.entity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.rapidark.framework.common.annotation.TableAlias;

import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;
import com.rapidark.framework.data.jpa.entity.AbstractIdStringEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 系统应用-基础信息
 * @author darkness
 * @version 1.0
 * @date 2022/5/25 11:10
 */
@Entity
@Getter
@Setter
@Table(name="base_app")
@TableAlias("app")
public class OpenApp extends AbstractIdStringEntity {

    private static final long serialVersionUID = -4606067795040222681L;

    @Id
    @Column(name = "APP_ID")
    @ApiModelProperty(value = "客户端ID")
    private String appId;

    @NotNull(message = "客户端分组不能为空")
    @Column(name = "group_code")
    private String groupCode;

    /**
     * API访问key
     */
    @Column(name = "API_KEY")
    @ApiModelProperty(value = "API访问key")
    private String apiKey;
    /**
     * API访问密钥
     */
    @Column(name = "SECRET_KEY",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "API访问密钥")
    private String secretKey;

    /**
     * app名称
     */
    @Column(name = "APP_NAME",nullable = false)
    @NotBlank(message = "客户端名称不能为空")
    @Size(min = 2, max = 40, message = "客户端系统名称长度必需在2到40个字符内")
    @ApiModelProperty(value = "客户端系统名称")
    private String appName;

    /**
     * app英文名称
     */
    @Column(name = "APP_NAME_EN",nullable = false)
    @NotBlank(message = "客户端系统代号不能为空")
    @Size(min = 2, max = 40, message = "客户端系统代号长度必需在2到40个字符内")
    @ApiModelProperty(value = "客户端系统代号")
    private String appNameEn;

    @Column(name = "ip")
    private String ip;

    /**
     * app类型：server-服务应用 app-手机应用 pc-PC网页应用 wap-手机网页应用
     */
    @Column(name = "APP_TYPE",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "app类型:server-服务应用 app-手机应用 pc-PC网页应用 wap-手机网页应用")
    private String appType;

    /**
     * 应用图标
     */
    @Column(name = "APP_ICON")
    @ApiModelProperty(value = "应用图标")
    private String appIcon;

    /**
     * 移动应用操作系统：ios-苹果 android-安卓
     */
    @Column(name = "APP_OS")
    @ApiModelProperty(value = "移动应用操作系统:ios-苹果 android-安卓")
    private String appOs;

    /**
     * 用户ID:默认为0
     */
    @Column(name = "DEVELOPER_ID",nullable = false)
    @NotNull
    @ApiModelProperty(value = "开发者ID:默认为0")
    private Long developerId;

    /**
     * app描述
     */
    @Column(name = "APP_DESC")
    @ApiModelProperty(value = "app描述")
    private String appDesc;

    /**
     * 官方网址
     */
    @Column(name = "WEBSITE")
    @ApiModelProperty(value = "官网地址")
    private String website;

    /**
     * 是否验签:0-否 1-是
     */
    @Column(name = "IS_SIGN",nullable = false)
    @NotNull
    @ApiModelProperty(value = "是否验签:0-否 1-是 不允许删除")
    private Integer isSign;

    /**
     * 是否加密:0-否 1-是
     */
    @Column(name = "IS_ENCRYPT",nullable = false)
    @NotNull
    @ApiModelProperty(value = "是否加密:0-否 1-是 不允许删除")
    private Integer isEncrypt;

    /**
     * 加密类型:DES TripleDES AES RSA
     */
    @Column(name = "ENCRYPT_TYPE",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "加密类型:DES TripleDES AES RSA")
    private String encryptType;

    /**
     * RSA加解密公钥
     */
    @Column(name = "PUBLIC_KEY")
    @ApiModelProperty(value = "RSA加解密公钥")
    private String publicKey;

    public void copy(OpenApp source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @Override
    public String getId() {
        return appId;
    }

    @Override
    public void setId(String id) {
        this.appId = id;
    }
}
