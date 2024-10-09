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
@TableName("gateway_rate_limit_api")
public class GatewayRateLimitApi extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 限制数量
     */
    private String policyId;

    /**
     * 时间间隔(秒)
     */
    private String apiId;

}
