package com.rapidark.cloud.platform.gateway.framework.bean;

import lombok.Data;

/**
 * @Description 限流器开关
 * @Author JL
 * @Date 2020/05/14
 * @Version V1.0
 */
@Data
@Deprecated
public class RouteLimiterBean {
    private Boolean ipChecked;
    private Boolean uriChecked;
    private Boolean idChecked;
}
