package com.bsd.payment.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.framework.data.mybatis.model.AbstractEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LJH-PC
 */
@Schema(title = "支付渠道表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("bsd_pay_channel")
public class PayChannel extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Schema(name = "channel_id", title = "渠道ID", example = "1")
    private Long channelId;

    @Schema(title = "渠道编码")
    private String channelCode;

    @Schema(title = "渠道名称,如:alipay,wechat")
    private String channelName;

    @Schema(title = "渠道商户ID")
    private String channelMchId;

    @Schema(title = "商户ID")
    private String mchId;

    @Schema(title = "渠道状态,0-停止使用,1-使用中", name = "state", example = "0")
    private Byte state;

    @Schema(title = "配置参数,json字符串")
    private String param;

    @Schema(title = "备注")
    private String remark;
}