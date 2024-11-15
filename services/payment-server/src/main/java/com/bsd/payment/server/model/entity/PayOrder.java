package com.bsd.payment.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.framework.common.mybatis.base.entity.AbstractEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author LJH-PC
 */
@ApiModel("支付订单表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("bsd_pay_order")
public class PayOrder extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Schema( "支付订单号")
    private String payOrderId;

    @Schema( "商户ID")
    private String mchId;

    @Schema( "商户订单号")
    private String mchOrderNo;

    @Schema( "渠道编码")
    private String channelCode;

    @Schema( name = "amount", value = "支付金额,单位分", example = "00")
    private Long amount;

    @Schema( "三位货币代码,人民币:cny")
    private String currency;

    @Schema( value = "支付状态,0-订单生成,1-支付中,2-支付成功,3-业务处理完成(支付完成业务回调成功)", name = "status", example = "0")
    private Byte status;

    @Schema( "客户端IP")
    private String clientIp;

    @Schema( "设备")
    private String device;

    @Schema( "商品标题")
    private String subject;

    @Schema( "商品描述信息")
    private String body;

    @Schema( "特定渠道发起时额外参数")
    private String extra;

    @Schema( "渠道商户ID")
    private String channelMchId;

    @Schema( "渠道订单号")
    private String channelOrderNo;

    @Schema( "渠道支付错误码")
    private String errCode;

    @Schema( "渠道支付错误描述")
    private String errMsg;

    @Schema( "扩展参数1")
    private String param1;

    @Schema( "扩展参数2")
    private String param2;

    @Schema( "通知地址")
    private String notifyUrl;

    @Schema( value = "通知次数", name = "notifyCount", example = "0")
    private Byte notifyCount;

    @Schema( value = "最后一次通知时间", name = "lastNotifyTime", example = "0")
    private Date lastNotifyTime;

    @Schema( value = "订单失效时间", name = "expireTime", example = "0")
    private Date expireTime;

    @Schema( value = "订单支付成功时间", name = "paySuccTime", example = "0")
    private Date paySuccTime;
}