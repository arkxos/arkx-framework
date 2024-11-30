package com.bsd.payment.server.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LJH-PC
 */
@Data
public class PayChannelDto {
    private static final long serialVersionUID = 1L;

    @Schema("渠道编码")
    private String channelCode;

    @Schema("渠道名称,如:alipay,wechat")
    private String channelName;
}