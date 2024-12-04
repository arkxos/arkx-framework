package com.rapidark.cloud.base.client.service.dto;


import com.rapidark.framework.data.jpa.entity.Status;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @website http://rapidark.com
 * @description /
 * @author Darkness
 * @date 2022-05-25
 **/
@Data
public class OpenAppDto implements Serializable {

    /** 更新人 */
    private String updateBy;

    /** 创建人 */
    private String createBy;

    /** 客户端ID */
    private String appId;

    private String groupCode;

    /** app英文名称 */
    private String appNameEn;

    /** app名称 */
    private String appName;

    private String ip;

    /** API访问key */
    private String apiKey;

    /** API访问密钥 */
    private String secretKey;



    /** 应用图标 */
    private String appIcon;

    /** app类型:server-服务应用 app-手机应用 pc-PC网页应用 wap-手机网页应用 */
    private String appType;

    /** app描述 */
    private String appDesc;

    /** 移动应用操作系统:ios-苹果 android-安卓 */
    private String appOs;

    /** 官网地址 */
    private String website;

    /** 开发者ID:默认为0 */
    private Integer developerId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 状态:0-无效 1-有效 */
    private Status status;

    /** 是否验签:0-否 1-是 不允许删除 */
    private Integer isSign;

    /** 是否加密:0-否 1-是 不允许删除 */
    private Integer isEncrypt;

    /** 加密类型:DES TripleDES AES RSA */
    private String encryptType;

    /** RSA加解密公钥 */
    private String publicKey;
}
