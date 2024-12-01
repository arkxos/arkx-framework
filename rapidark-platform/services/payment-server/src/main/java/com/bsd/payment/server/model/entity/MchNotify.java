package com.bsd.payment.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.framework.data.mybatis.model.AbstractEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author liujianhong
 */
@Schema(title = "商户通知表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "bsd_mch_notify")
public class MchNotify extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Schema(title = "订单ID")
    private String orderId;

    @Schema(title = "商户ID")
    private String mchId;

    @Schema(title = "商户订单号")
    private String mchOrderNo;

    @Schema(title = "订单类型:1-支付,2-转账,3-退款")
    private String orderType;

    @Schema(title = "通知地址")
    private String notifyUrl;

    @Schema(title = "通知次数", name = "notifyCount", example = "0")
    private Byte notifyCount;

    @Schema(title = "通知响应结果")
    private String result;

    @Schema(title = "通知状态,1-通知中,2-通知成功,3-通知失败", name = "status", example = "0")
    private Byte status;

    @Schema(title = "最后一次通知时间")
    private Date lastNotifyTime;
}