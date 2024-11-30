package com.bsd.payment.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.framework.common.mybatis.base.entity.AbstractEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LJH-PC
 */
@Schema(title = "商品订单表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("bsd_goods_order")
public class GoodsOrder extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Schema(title = "商品订单ID")
    private String goodsOrderId;

    @Schema(title = "商品ID")
    private String goodsId;

    @Schema(title = "商品名称")
    private String goodsName;

    @Schema(title = "金额,单位分", name = "amount", example = "0")
    private Long amount;

    @Schema(title = "用户ID")
    private String userId;

    @Schema(title = "订单状态,订单生成(0),支付成功(1),处理完成(2),处理失败(-1)", name = "status", example = "0")
    private Byte status;

    @Schema(title = "支付订单号")
    private String payOrderId;

    @Schema(title = "渠道编码")
    private String channelCode;

    @Schema(title = "支付渠道用户ID(微信openID或支付宝账号等第三方支付账号)")
    private String channelUserId;
}