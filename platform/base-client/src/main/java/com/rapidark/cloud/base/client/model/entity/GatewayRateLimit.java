package com.rapidark.cloud.base.client.model.entity;

import com.rapidark.framework.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author liuyadu
 */
@Getter
@Setter
@Entity
@Table(name="gateway_rate_limit")
public class GatewayRateLimit extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "policy_Id")
    @ApiModelProperty(value = "policyId")
    private Long policyId;

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
