package com.rapidark.cloud.platform.gateway.framework.bean;

import lombok.Data;

/**
 * @Description
 * @Author JL
 * @Date 2020/05/11
 * @Version V1.0
 */
@Data
public class RouteReq implements java.io.Serializable {

    /**
     * 表单配置数据
     */
    private RouteFormBean form;
    /**
     * 过滤器开关
     */
    private RouteFilterBean filter;
    /**
     * 熔断器开关
     */
    private RouteHystrixBean hystrix;
    /**
     * 限流器开关
     */
    private RouteLimiterBean limiter;
    /**
     * 鉴权器开关
     */
    private RouteAccessBean access;
    /**
     * 监控开关
     */
    private MonitorBean monitor;
    /**
     * sentinel组件限流开关(用于替换原生的限流、熔断组件)
     */
    private FlowRuleBean flowRule;
    /**
     * sentinel组件熔断开关(用于替换原生的限流、熔断组件)
     */
    private DegradeRuleBean degradeRule;
    /**
     * 缓存开关
     */
    private CacheResultBean cacheResult;

    private Integer currentPage;
    private Integer pageSize;
}
