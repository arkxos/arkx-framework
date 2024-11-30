package com.bsd.org.server.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: linrongxin
 * @Date: 2019/9/20 15:10
 */
@Data
public class DingtalkVO {
    /**
     * 公司ID
     */
    @Schema(value = "公司ID")
    private Long companyId;

    /**
     * 公司名称
     */
    @Schema(value = "公司名称")
    private String companyName;

    /**
     * 企业corpid
     */
    @Schema(value = "钉钉企业corpid")
    private String corpId;

    /**
     * 应用的agentdId
     */
    @Schema(value = "应用的agentdId")
    private String agentdId;

    /**
     * 应用的AppKey
     */
    @Schema(value = "应用的AppKey")
    private String appKey;

    /**
     * 应用的AppSecret
     */
    @Schema(value = "应用的appSecret")
    private String appSecret;

    /**
     * 数据加密密钥
     */
    @Schema(value = "数据加密密钥")
    private String encodingAesKey;

    /**
     * 加解密需要用到的token
     */
    @Schema(value = "加解密需要用到的token")
    private String token;
}
