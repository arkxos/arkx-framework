package com.rapidark.cloud.base.server.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rapidark.cloud.base.client.model.IpLimitApi;
import com.rapidark.cloud.base.client.model.RateLimitApi;
import com.rapidark.cloud.base.client.service.IGatewayServiceClient;
import com.rapidark.cloud.base.server.service.GatewayIpLimitService;
import com.rapidark.cloud.base.server.service.GatewayRateLimitService;
import com.rapidark.cloud.platform.gateway.framework.entity.GatewayAppRoute;
import com.rapidark.cloud.platform.gateway.framework.service.GatewayAppRouteService;
import com.rapidark.framework.common.model.ResultBody;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 网关接口
 *
 * @author: liuyadu
 * @date: 2019/3/12 15:12
 * @description:
 */
@Schema(title = "网关对外接口")
@RestController
public class GatewayController implements IGatewayServiceClient {

    @Autowired
    private GatewayIpLimitService gatewayIpLimitService;
    @Autowired
    private GatewayRateLimitService gatewayRateLimitService;
    @Autowired
    private GatewayAppRouteService gatewayAppRouteService;

    @Schema(title = "获取服务列表", name = "获取服务列表")
    @GetMapping("/gateway/service/list")
    public ResultBody getServiceList() {
        List<Map> services = Lists.newArrayList();
        List<GatewayAppRoute> routes = gatewayAppRouteService.findAll();
        if (routes != null && routes.size() > 0) {
            routes.forEach(route -> {
                Map service = Maps.newHashMap();
                service.put("serviceId", route.getSystemCode());
                service.put("serviceName", route.getName());
                services.add(service);
            });
        }
        return ResultBody.ok(services);
    }

    /**
     * 获取接口黑名单列表
     *
     * @return
     */
    @Schema(title = "获取接口黑名单列表", name = "仅限内部调用")
    @GetMapping("/gateway/api/blackList")
    @Override
    public ResultBody<List<IpLimitApi>> getApiBlackList() {
        return ResultBody.ok(gatewayIpLimitService.findBlackList());
    }

    /**
     * 获取接口白名单列表
     *
     * @return
     */
    @Schema(title = "获取接口白名单列表", name = "仅限内部调用")
    @GetMapping("/gateway/api/whiteList")
    @Override
    public ResultBody<List<IpLimitApi>> getApiWhiteList() {
        return ResultBody.ok(gatewayIpLimitService.findWhiteList());
    }

    /**
     * 获取限流列表
     *
     * @return
     */
    @Schema(title = "获取限流列表", name = "仅限内部调用")
    @GetMapping("/gateway/api/rateLimit")
    @Override
    public ResultBody<List<RateLimitApi>> getApiRateLimitList() {
        return ResultBody.ok(gatewayRateLimitService.findRateLimitApiList());
    }

    /**
     * 获取路由列表
     *
     * @return
     */
    @Schema(title = "获取路由列表", name = "仅限内部调用")
    @GetMapping("/gateway/api/route")
    @Override
    public ResultBody<List<GatewayAppRoute>> getApiRouteList() {
        return ResultBody.ok(gatewayAppRouteService.findAll());
    }
}
