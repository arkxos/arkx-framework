package com.rapidark.cloud.base.client.model.entity;

import com.rapidark.framework.commons.model.BaseEntity;
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
@Table(name="gateway_ip_limit")
public class GatewayIpLimit extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 策略ID
     */
    @Id
    @Column(name = "policy_Id")
    @ApiModelProperty(value = "policyId")
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

}
