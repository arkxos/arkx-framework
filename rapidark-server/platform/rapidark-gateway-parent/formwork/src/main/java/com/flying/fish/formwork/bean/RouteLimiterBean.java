package com.flying.fish.formwork.bean;

import lombok.Data;

/**
 * @Description 限流器开关
 * @Author jianglong
 * @Date 2020/05/14
 * @Version V1.0
 */
@Data
public class RouteLimiterBean {
    private Boolean ipChecked;
    private Boolean uriChecked;
    private Boolean idChecked;
}
