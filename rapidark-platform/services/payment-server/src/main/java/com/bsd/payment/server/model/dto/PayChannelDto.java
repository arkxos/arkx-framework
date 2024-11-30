package com.bsd.payment.server.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author LJH-PC
 */
@Data
public class PayChannelDto {
    private static final long serialVersionUID = 1L;

    @Schema(title = "渠道编码")
    private String channelCode;

    @Schema(title = "渠道名称,如:alipay,wechat")
    private String channelName;
}