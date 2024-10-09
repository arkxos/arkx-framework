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
@TableName("gateway_rate_limit")
public class GatewayRateLimit extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String policyId;

    /**
     * 策略名称
     */
    private String policyName;

    /**
     * 限流规则类型:url,origin,user
     */
    private String policyType;

    /**
     * 限制数
     */
    private Long limitQuota;

    /**
     * 单位时间:seconds-秒,minutes-分钟,hours-小时,days-天
     */
    private String intervalUnit;

}
