package com.rapidark.cloud.base.client.model.entity;


import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;
import com.rapidark.framework.data.jpa.entity.IdLongEntity;
import io.swagger.annotations.ApiModelProperty;
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
