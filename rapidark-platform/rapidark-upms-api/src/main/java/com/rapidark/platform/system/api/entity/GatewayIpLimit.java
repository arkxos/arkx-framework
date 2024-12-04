package com.rapidark.platform.system.api.entity;


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
@Table(name="gateway_ip_limit")
public class GatewayIpLimit extends AbstractIdLongEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 策略ID
     */
    @Id
    @Column(name = "policy_Id")
    @Schema(title = "policyId")
    private Long policyId;

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

    @Override
    public Long getId() {
        return policyId;
    }

    @Override
    public void setId(Long id) {
        this.policyId = id;
    }
}
