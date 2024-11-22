package com.rapidark.cloud.platform.gateway.framework.bean;

import lombok.Data;

/**
 * @Description熔断器开关
 * @Author JL
 * @Date 2020/05/14
 * @Version V1.0
 */
@Data
@Deprecated
public class RouteHystrixBean {
    private Boolean defaultChecked;
    private Boolean customChecked;
}
