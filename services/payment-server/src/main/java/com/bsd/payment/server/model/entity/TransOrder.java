package com.bsd.payment.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.framework.common.mybatis.base.entity.AbstractEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author LJH-PC
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("转账订单表")
@NoArgsConstructor
@TableName("bsd_trans_order")
public class TransOrder extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Schema( "转账订单号")
    private String transOrderId;

    @Schema( "商户ID")
    private String mchId;

    @Schema( "商户转账单号")
    private String mchTransNo;

    @Schema( "渠道编码")
    private String channelCode;

    @Schema( name = "amount", value = "转账金额,单位分", example = "00")
    private Long amount;

    @Schema( "三位货币代码,人民币:cny")
    private String currency;

    @Schema( value = "转账状态：0-订单生成,1-转账中,2-转账成功,3-转账失败,4-业务处理完成", name = "status", example = "0")
    private Byte status;

    @Schema( value = "转账结果：0-不确认结果,1-等待手动处理,2-确认成功,3-确认失败", name = "result", example = "0")
    private Byte result;

    @Schema( "客户端IP")
    private String clientIp;

    @Schema( "设备")
    private String device;

    @Schema( "备注")
    private String remarkInfo;

    @Schema( "渠道用户标识：微信openId,支付宝账号")
    private String channelUser;

    @Schema( "用户姓名")
    private String userName;

    @Schema( "渠道商户ID")
    private String channelMchId;

    @Schema( "渠道订单号")
    private String channelOrderNo;

    @Schema( "渠道错误码")
    private String channelErrCode;

    @Schema( "渠道错误描述")
    private String channelErrMsg;

    @Schema( "特定渠道发起时额外参数")
    private String extra;

    @Schema( "通知地址")
    private String notifyUrl;

    @Schema( "扩展参数1")
    private String param1;

    @Schema( "扩展参数2")
    private String param2;

    @Schema( "订单失效时间")
    private Date expireTime;

    @Schema( "订单转账成功时间")
    private Date transSuccTime;
}