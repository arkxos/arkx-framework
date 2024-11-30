package com.bsd.payment.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.framework.common.mybatis.base.entity.AbstractEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author LJH-PC
 */
@Schema(title = "退款订单表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("bsd_refund_order")
public class RefundOrder extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Schema(title = "退款订单号")
    private String refundOrderId;

    @Schema(title = "支付订单号")
    private String payOrderId;

    @Schema(title = "渠道支付单号")
    private String channelPayOrderNo;

    @Schema(title = "商户ID")
    private String mchId;

    @Schema(title = "商户退款单号")
    private String mchRefundNo;

    @Schema(title = "渠道编码")
    private String channelCode;

    @Schema(title = "支付金额,单位分", name = "payAmount", example = "0")
    private Long payAmount;

    @Schema(title = "退款金额,单位分", name = "refundAmount", example = "0")
    private Long refundAmount;

    @Schema(title = "三位货币代码,人民币:cny")
    private String currency;

    @Schema(title = "退款状态:0-订单生成,1-退款中,2-退款成功,3-退款失败,4-业务处理完成", name = "status", example = "0")
    private Byte status;

    @Schema(title = "退款结果:0-不确认结果,1-等待手动处理,2-确认成功,3-确认失败", name = "result", example = "0")
    private Byte result;

    @Schema(title = "客户端IP")
    private String clientIp;

    @Schema(title = "设备")
    private String device;

    @Schema(title = "备注")
    private String remarkInfo;

    @Schema(title = "渠道用户标识,如微信openId,支付宝账号")
    private String channelUser;

    @Schema(title = "用户姓名")
    private String userName;

    @Schema(title = "渠道商户ID")
    private String channelMchId;

    @Schema(title = "渠道订单号")
    private String channelOrderNo;

    @Schema(title = "渠道错误码")
    private String channelErrCode;

    @Schema(title = "渠道错误描述")
    private String channelErrMsg;

    @Schema(title = "特定渠道发起时额外参数")
    private String extra;

    @Schema(title = "通知地址")
    private String notifyUrl;

    @Schema(title = "扩展参数1")
    private String param1;

    @Schema(title = "扩展参数2")
    private String param2;

    @Schema(title = "订单失效时间")
    private Date expireTime;

    @Schema(title = "订单退款成功时间")
    private Date refundSuccTime;
}