package com.bsd.org.server.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(title = "公司ID")
    private Long companyId;

    /**
     * 公司名称
     */
    @Schema(title = "公司名称")
    private String companyName;

    /**
     * 企业corpid
     */
    @Schema(title = "钉钉企业corpid")
    private String corpId;

    /**
     * 应用的agentdId
     */
    @Schema(title = "应用的agentdId")
    private String agentdId;

    /**
     * 应用的AppKey
     */
    @Schema(title = "应用的AppKey")
    private String appKey;

    /**
     * 应用的AppSecret
     */
    @Schema(title = "应用的appSecret")
    private String appSecret;

    /**
     * 数据加密密钥
     */
    @Schema(title = "数据加密密钥")
    private String encodingAesKey;

    /**
     * 加解密需要用到的token
     */
    @Schema(title = "加解密需要用到的token")
    private String token;
}
