package com.rapidark.cloud.platform.gateway.filter;

import com.rapidark.cloud.platform.gateway.framework.util.HttpResponseUtils;
import com.rapidark.cloud.platform.gateway.framework.util.NetworkIpUtils;
import com.rapidark.cloud.platform.gateway.framework.util.RouteConstants;
import com.rapidark.cloud.platform.gateway.framework.util.RouteUtils;
import com.rapidark.cloud.platform.gateway.cache.RegServerCache;
import com.rapidark.cloud.platform.gateway.vo.GatewayRegServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static com.rapidark.cloud.platform.gateway.support.CustomGatewayToStringStyler.filterToStringCreator;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @Description 客户端ID过滤
 * @Author JL
 * @Date 2020/05/19
 * @Version V1.0
 */
@Slf4j
public class ClientIdGatewayFilter implements GatewayFilter, Ordered {

    private boolean enabled;

    public ClientIdGatewayFilter(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!enabled) {
            return chain.filter(exchange);
        }

        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
        String routeId = route.getId();
        //做了负载均衡的route服务,单独提取routeId做客户端ID验证
        if (routeId.startsWith(RouteConstants.BALANCED)){
            routeId = RouteUtils.getBalancedToRouteId(routeId);
        }
        String ip = NetworkIpUtils.getIpAddress(exchange.getRequest());
        String clientId = this.getClientId(exchange.getRequest());
        if (StringUtils.isBlank(clientId)){
            String msg = "客户端ID值为空，无权限访问网关路由："+ routeId +"! Ip:" + ip;
            log.error(msg);
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        GatewayRegServer regServer = getCacheRegServer(clientId, routeId);
        if (regServer == null){
            String msg = "客户端ID未注册使用，无权限访问网关路由："+ routeId +"! Ip:" + ip;
            log.error(msg);
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        return chain.filter(exchange);
    }

    /**
     * 查询和对比缓存中的注册客户端
     * @param clientId
     * @param routeId
     * @return
     */
    public GatewayRegServer getCacheRegServer(String clientId, String routeId){
        List<GatewayRegServer> regServers = RegServerCache.get(routeId);
        if (CollectionUtils.isEmpty(regServers)){
            return null;
        }
        Optional<GatewayRegServer> optional = regServers.stream().filter(r -> clientId.equals(r.getClientId())).findFirst();
        return optional.orElse(null);
    }

    /**
     * 获取请求头部的clientId值
     * @param request
     * @return
     */
    public String getClientId(ServerHttpRequest request){
        String clientId = request.getQueryParams().getFirst(RouteConstants.CLIENT_ID);
        if (StringUtils.isBlank(clientId)){
            clientId = request.getHeaders().getFirst(RouteConstants.CLIENT_ID);
        }
        return clientId;
    }

    @Override
    public String toString() {
        return filterToStringCreator(ClientIdGatewayFilter.this)
                .append("enabled", true)
                .append("order", getOrder())
                .toString();
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
