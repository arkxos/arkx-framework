package com.bsd.payment.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.framework.data.mybatis.model.AbstractEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LJH-PC
 */
@Schema(title = "商户信息表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("bsd_mch_info")
public class MchInfo extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Schema(title = "商户ID")
    private String mchId;

    @Schema(title = "名称")
    private String name;

    @Schema(title = "类型,0-平台,1-私有")
    private String type;

    @Schema(title = "请求私钥")
    private String reqKey;

    @Schema(title = "响应私钥")
    private String resKey;

    @Schema(title = "商户状态,0-停止使用,1-使用中", name = "state", example = "0")
    private Byte state;
}