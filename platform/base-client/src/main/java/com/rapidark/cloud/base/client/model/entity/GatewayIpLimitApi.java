package com.rapidark.cloud.base.client.model.entity;


import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;
import com.rapidark.framework.data.jpa.entity.IdLongEntity;
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
