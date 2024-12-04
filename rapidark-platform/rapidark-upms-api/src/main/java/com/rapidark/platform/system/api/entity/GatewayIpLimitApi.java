package com.rapidark.platform.system.api.entity;


import com.rapidark.framework.data.jpa.entity.IdLongEntity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * @author liuyadu
 */
@Getter
@Setter
@Entity
@Table(name="gateway_ip_limit_api")
public class GatewayIpLimitApi extends IdLongEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 策略ID
     */
    private Long policyId;

    /**
     * 接口资源ID
     */
    private Long apiId;

}
