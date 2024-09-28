package com.flying.fish.formwork.bean;

import com.flying.fish.formwork.entity.Monitor;
import lombok.Data;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/05/11
 * @Version V1.0
 */
@Data
public class RouteReq implements java.io.Serializable {

    //表单值
    private RouteFormBean form;
    //过滤器开关
    private RouteFilterBean filter;
    //熔断器开关
    private RouteHystrixBean hystrix;
    //限流器开关
    private RouteLimiterBean limiter;
    //鉴权器开关
    private RouteAccessBean access;
    //监控开关
    private MonitorBean monitor;

    private Integer currentPage;
    private Integer pageSize;
}
