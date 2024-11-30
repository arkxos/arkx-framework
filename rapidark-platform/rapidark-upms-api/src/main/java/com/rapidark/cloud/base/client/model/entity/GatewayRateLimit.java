package com.rapidark.cloud.base.client.model.entity;


import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author liuyadu
 */
@Getter
@Setter
@Entity
@Table(name="gateway_rate_limit")
public class GatewayRateLimit extends AbstractIdLongEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "policy_Id")
    @Schema(title = "policyId")
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

    @Override
    public Long getId() {
        return policyId;
    }

    @Override
    public void setId(Long id) {
        this.policyId = id;
    }
}
