package com.rapidark.cloud.platform.gateway.filter;

import com.rapidark.cloud.platform.gateway.framework.util.HttpResponseUtils;
import com.rapidark.cloud.platform.gateway.framework.util.NetworkIpUtils;
import com.rapidark.cloud.platform.gateway.framework.util.RouteUtils;
import com.rapidark.cloud.platform.gateway.cache.IpListCache;
import com.rapidark.cloud.platform.gateway.cache.RegServerCache;
import com.rapidark.cloud.platform.gateway.vo.GatewayRegServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static com.rapidark.cloud.platform.gateway.support.CustomGatewayToStringStyler.filterToStringCreator;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @Description IP过滤
 * @Author JL
 * @Date 2020/05/19
 * @Version V1.0
 */
@Slf4j
public class IpGatewayFilter implements GatewayFilter, Ordered {

    private boolean enabled;
    public IpGatewayFilter(boolean enabled){
        this.enabled = enabled;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!enabled) {
            return chain.filter(exchange);
        }

        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
        String routeId = RouteUtils.getBalancedToRouteId(route.getId());
        String ip = NetworkIpUtils.getIpAddress(exchange.getRequest());
        if (!this.isPassIp(ip)){
            String msg = "客户端IP已被限制，无权限访问网关!" +" ip:" + ip;
            log.error(msg);
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        GatewayRegServer regServer = getCacheRegServer(ip, routeId);
        if (regServer == null){
            String msg = "客户端IP未注册使用，无权限访问网关路由："+ routeId +"! Ip:" + ip;
            log.error(msg);
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        return chain.filter(exchange);
    }

    /**
     * 查询和对比缓存中的注册客户端
     * @param ip
     * @param routeId
     * @return
     */
    public GatewayRegServer getCacheRegServer(String ip, String routeId){
        List<GatewayRegServer> regServers = RegServerCache.get(routeId);
        if (CollectionUtils.isEmpty(regServers)){
            return null;
        }
        Optional<GatewayRegServer> optional = regServers.stream().filter(r -> ip.equals(r.getIp())).findFirst();
        return optional.orElse(null);
    }

    /**
     * 是否允许通行IP
     * @return
     */
    private boolean isPassIp(String ip){
        Object isPass = IpListCache.get(ip);
        //如果没有设置IP白名单，则不做限制
        return isPass == null || (boolean) isPass;
    }

    @Override
    public String toString() {
        return filterToStringCreator(IpGatewayFilter.this)
                .append("enabled", true)
                .append("order", getOrder())
                .toString();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
