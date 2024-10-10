package com.rapidark.cloud.gateway.formwork.bean;

import lombok.Data;

/**
 * @Description熔断器开关
 * @Author jianglong
 * @Date 2020/05/14
 * @Version V1.0
 */
@Data
public class RouteHystrixBean {
    private Boolean defaultChecked;
    private Boolean customChecked;
}
