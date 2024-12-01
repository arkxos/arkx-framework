package com.bsd.payment.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.framework.data.mybatis.model.AbstractEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author LJH-PC
 */
@Schema(title = "支付订单表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("bsd_pay_order")
public class PayOrder extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Schema(title = "支付订单号")
    private String payOrderId;

    @Schema(title = "商户ID")
    private String mchId;

    @Schema(title = "商户订单号")
    private String mchOrderNo;

    @Schema(title = "渠道编码")
    private String channelCode;

    @Schema(name = "amount", title = "支付金额,单位分", example = "00")
    private Long amount;

    @Schema(title = "三位货币代码,人民币:cny")
    private String currency;

    @Schema(title = "支付状态,0-订单生成,1-支付中,2-支付成功,3-业务处理完成(支付完成业务回调成功)", name = "status", example = "0")
    private Byte status;

    @Schema(title = "客户端IP")
    private String clientIp;

    @Schema(title = "设备")
    private String device;

    @Schema(title = "商品标题")
    private String subject;

    @Schema(title = "商品描述信息")
    private String body;

    @Schema(title = "特定渠道发起时额外参数")
    private String extra;

    @Schema(title = "渠道商户ID")
    private String channelMchId;

    @Schema(title = "渠道订单号")
    private String channelOrderNo;

    @Schema(title = "渠道支付错误码")
    private String errCode;

    @Schema(title = "渠道支付错误描述")
    private String errMsg;

    @Schema(title = "扩展参数1")
    private String param1;

    @Schema(title = "扩展参数2")
    private String param2;

    @Schema(title = "通知地址")
    private String notifyUrl;

    @Schema(title = "通知次数", name = "notifyCount", example = "0")
    private Byte notifyCount;

    @Schema(title = "最后一次通知时间", name = "lastNotifyTime", example = "0")
    private Date lastNotifyTime;

    @Schema(title = "订单失效时间", name = "expireTime", example = "0")
    private Date expireTime;

    @Schema(title = "订单支付成功时间", name = "paySuccTime", example = "0")
    private Date paySuccTime;
}