package com.rapidark.cloud.gateway.formwork.bean;

import com.rapidark.cloud.gateway.formwork.entity.GatewayAppRoute;
import com.rapidark.cloud.gateway.formwork.entity.Monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/05/14
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GatewayAppRouteFormBean extends GatewayAppRoute implements java.io.Serializable {
    private Monitor monitor;
}
