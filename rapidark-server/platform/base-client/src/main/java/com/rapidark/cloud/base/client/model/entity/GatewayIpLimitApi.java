package com.rapidark.cloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.common.mybatis.base.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liuyadu
 */
@Getter
@Setter
@TableName("gateway_ip_limit_api")
public class GatewayIpLimitApi extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 策略ID
     */
    private String policyId;

    /**
     * 接口资源ID
     */
    private String apiId;

}
