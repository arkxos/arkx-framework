package com.bsd.user.server.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * js-sdk授权配置
 *
 * @Author: linrongxin
 * @Date: 2019/9/19 14:03
 */
@Data
public class JsSdkSignDTO implements Serializable {
    /**
     * 公众号的唯一标识
     */
    @Schema(title = "公众号的唯一标识")
    private String appId;
    /**
     * 生成签名的时间戳
     */
    @Schema(title = "生成签名的时间戳")
    private String timestamp;
    /**
     * 生成签名的随机串
     */
    @Schema(title = "生成签名的随机串")
    private String nonceStr;
    /**
     * 签名
     */
    @Schema(title = "签名")
    private String signature;
    /**
     * 授权URL
     */
    @Schema(title = "授权URL")
    private String url;
}
