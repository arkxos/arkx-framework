package com.bsd.payment.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.framework.data.mybatis.model.AbstractEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author LJH-PC
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "转账订单表")
@NoArgsConstructor
@TableName("bsd_trans_order")
public class TransOrder extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Schema(title = "转账订单号")
    private String transOrderId;

    @Schema(title = "商户ID")
    private String mchId;

    @Schema(title = "商户转账单号")
    private String mchTransNo;

    @Schema(title = "渠道编码")
    private String channelCode;

    @Schema(name = "amount", title = "转账金额,单位分", example = "00")
    private Long amount;

    @Schema(title = "三位货币代码,人民币:cny")
    private String currency;

    @Schema(title = "转账状态：0-订单生成,1-转账中,2-转账成功,3-转账失败,4-业务处理完成", name = "status", example = "0")
    private Byte status;

    @Schema(title = "转账结果：0-不确认结果,1-等待手动处理,2-确认成功,3-确认失败", name = "result", example = "0")
    private Byte result;

    @Schema(title = "客户端IP")
    private String clientIp;

    @Schema(title = "设备")
    private String device;

    @Schema(title = "备注")
    private String remarkInfo;

    @Schema(title = "渠道用户标识：微信openId,支付宝账号")
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

    @Schema(title = "订单转账成功时间")
    private Date transSuccTime;
}