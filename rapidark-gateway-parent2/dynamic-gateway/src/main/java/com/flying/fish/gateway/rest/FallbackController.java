package com.flying.fish.gateway.rest;

import com.rapidark.cloud.gateway.formwork.cache.RouteCache;
import com.rapidark.cloud.gateway.formwork.entity.GatewayAppRoute;
import com.rapidark.common.model.ResultBody;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @Description 触发熔断机制响应控制器
 * @Author jianglong
 * @Date 2020/05/26
 * @Version V1.0
 */
@Slf4j
@RestController
@ResponseStatus(value = HttpStatus.GATEWAY_TIMEOUT)
public class FallbackController {

    /**
     * 触发熔断机制的回调方法
     * @return
     */
    @RequestMapping(value = "/fallback", method = {RequestMethod.GET,RequestMethod.POST})
    public ResultBody fallback(@RequestParam(required = false) String routeId) {
        log.error("触发熔断机制的回调方法:fallback,routeId={}", routeId);
        return ResultBody.failed().msg("提示：服务响应超时，触发熔断机制，请联系运维人员处理。此消息由网关服务返回！");
    }

    /**
     * 触发自定义熔断机制的回调方法
     * @return
     */
    @RequestMapping(value = "/fallback/custom", method = {RequestMethod.GET,RequestMethod.POST})
    public ResultBody fallbackCustom(@RequestParam String routeId) {
        log.error("触发自定义熔断机制的回调方法:fallback,routeId={}", routeId);
        GatewayAppRoute gatewayAppRoute = (GatewayAppRoute) RouteCache.get(routeId);
        if (gatewayAppRoute != null){
            return ResultBody.failed().msg("提示：" + gatewayAppRoute.getFallbackMsg());
        }
        return ResultBody.failed().msg("提示：服务响应超时，触发自定义熔断机制，请联系运维人员处理。此消息由网关服务返回！");
    }

}
