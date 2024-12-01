package com.bsd.payment.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.framework.data.mybatis.model.AbstractEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LJH-PC
 */
@Schema(title = "苹果支付凭据表")
@TableName("bsd_iap_receipt")
@Data
@EqualsAndHashCode(callSuper = false)
public class IapReceipt extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Schema(title = "支付订单号")
    private String payOrderId;

    @Schema(title = "商户ID")
    private String mchId;

    @Schema(title = "IAP业务号")
    private String transactionId;

    @Schema(title = "处理状态:0-未处理,1-处理成功,-1-处理失败", name = "status", example = "0")
    private Byte status;

    @Schema(title = "处理次数", name = "handleCount", example = "0")
    private Byte handleCount;

    @Schema(title = "凭据内容")
    private String receiptData;
}