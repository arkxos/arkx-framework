package com.rapidark.cloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.common.mybatis.base.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liuyadu
 */
@Getter
@Setter
@TableName("gateway_ip_limit")
public class GatewayIpLimit extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 策略ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String policyId;

    /**
     * 策略名称
     */
    private String policyName;

    /**
     * 策略类型:0-拒绝/黑名单 1-允许/白名单
     */
    private Integer policyType;

    /**
     * ip地址/IP段:多个用隔开;最多10个
     */
    private String ipAddress;

}
