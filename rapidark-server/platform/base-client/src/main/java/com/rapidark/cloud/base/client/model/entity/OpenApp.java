package com.rapidark.cloud.base.client.model.entity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.common.annotation.TableAlias;
import com.rapidark.common.model.BaseEntity;
import com.rapidark.common.mybatis.base.entity.AbstractEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
public class OpenApp extends BaseEntity {

    private static final long serialVersionUID = -4606067795040222681L;

    @Id
    @Column(name = "APP_ID")
    @ApiModelProperty(value = "客户端ID")
    private String appId;

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
    @NotBlank
    @ApiModelProperty(value = "app名称")
    private String appName;

    /**
     * app英文名称
     */
    @Column(name = "APP_NAME_EN",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "app英文名称")
    private String appNameEn;

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
     * 状态:0-无效 1-有效
     */
    @Column(name = "STATUS",nullable = false)
    @NotNull
    @ApiModelProperty(value = "状态:0-无效 1-有效")
    private Integer status;

    /**
     * 保留数据0-否 1-是 不允许删除
     */
    @Column(name = "IS_PERSIST",nullable = false)
    @NotNull
    @ApiModelProperty(value = "保留数据0-否 1-是 不允许删除")
    private Integer isPersist;

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

    /**
     * @return app_id
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * 获取app名称
     *
     * @return app_name - app名称
     */
    public String getAppName() {
        return appName;
    }

    /**
     * 设置app名称
     *
     * @param appName app名称
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * 获取app英文名称
     *
     * @return app_name_en - app英文名称
     */
    public String getAppNameEn() {
        return appNameEn;
    }

    /**
     * 设置app英文名称
     *
     * @param appNameEn app英文名称
     */
    public void setAppNameEn(String appNameEn) {
        this.appNameEn = appNameEn;
    }


    /**
     * @return app_type
     */
    public String getAppType() {
        return appType;
    }

    /**
     * @param appType
     */
    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppOs() {
        return appOs;
    }

    public void setAppOs(String appOs) {
        this.appOs = appOs;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }


    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }


    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsPersist() {
        return isPersist;
    }

    public void setIsPersist(Integer isPersist) {
        this.isPersist = isPersist;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Integer getIsSign() {
        return isSign;
    }

    public void setIsSign(Integer isSign) {
        this.isSign = isSign;
    }

    public Integer getIsEncrypt() {
        return isEncrypt;
    }

    public void setIsEncrypt(Integer isEncrypt) {
        this.isEncrypt = isEncrypt;
    }

    public String getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(String encryptType) {
        this.encryptType = encryptType;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
