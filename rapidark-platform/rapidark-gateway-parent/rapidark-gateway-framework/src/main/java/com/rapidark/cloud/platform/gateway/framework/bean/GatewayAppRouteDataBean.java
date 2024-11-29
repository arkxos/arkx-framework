package com.rapidark.cloud.platform.gateway.framework.bean;

import com.rapidark.cloud.platform.gateway.framework.entity.Monitor;
import com.rapidark.cloud.platform.gateway.framework.entity.GatewayAppRoute;
import com.rapidark.cloud.platform.gateway.framework.entity.SentinelRule;
import lombok.Data;

/**
 * @Description
 * @Author JL
 * @Date 2022/12/09
 * @Version V1.0
 */
@Data
public class GatewayAppRouteDataBean {

    private GatewayAppRoute gatewayAppRoute;
    private Monitor monitor;
    private SentinelRule sentinelRule;

}
