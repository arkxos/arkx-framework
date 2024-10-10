package com.rapidark.cloud.gateway.formwork.bean;

import com.rapidark.cloud.gateway.formwork.entity.Monitor;
import com.rapidark.cloud.gateway.formwork.entity.GatewayAppRoute;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author JL
 * @Date 2021/04/16
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GatewayAppRouteRsp extends GatewayAppRoute {
    private Monitor monitor;
}
