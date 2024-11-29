package com.rapidark.cloud.platform.gateway.framework.bean;

//import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
//import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.rapidark.cloud.platform.gateway.framework.entity.Monitor;
import com.rapidark.cloud.platform.gateway.framework.entity.GatewayAppRoute;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author JL
 * @Date 2020/05/14
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RouteFormBean extends GatewayAppRoute implements java.io.Serializable {
    private Monitor monitor;
//    private FlowRule flowRule;
//    private DegradeRule degradeRule;
}
