package com.rapidark.cloud.platform.gateway.framework.bean;

import lombok.Data;

/**
 * @Description 过滤器开关
 * @Author JL
 * @Date 2020/05/14
 * @Version V1.0
 */
@Data
public class GatewayAppRouteFilterBean {
    private Boolean ipChecked;
    private Boolean tokenChecked;
    private Boolean idChecked;
}
