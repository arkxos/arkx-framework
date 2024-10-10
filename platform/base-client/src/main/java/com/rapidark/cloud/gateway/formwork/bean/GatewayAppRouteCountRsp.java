package com.rapidark.cloud.gateway.formwork.bean;

import com.rapidark.cloud.gateway.formwork.entity.GatewayAppRoute;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author JL
 * @Date 2020/12/30
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class GatewayAppRouteCountRsp extends GatewayAppRoute implements java.io.Serializable {
    /**
     * 统计量
     */
    private Integer count;

}
