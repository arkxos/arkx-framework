package com.rapidark.cloud.platform.gateway.filter;

import com.rapidark.cloud.platform.gateway.filter.authorize.*;
import com.rapidark.cloud.platform.gateway.framework.entity.GatewayAppRoute;
import com.rapidark.cloud.platform.gateway.framework.util.HttpResponseUtils;
import com.rapidark.cloud.platform.gateway.framework.util.NetworkIpUtils;
import com.rapidark.cloud.platform.gateway.framework.util.RouteUtils;
import com.rapidark.cloud.platform.gateway.cache.RouteCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.rapidark.cloud.platform.gateway.support.CustomGatewayToStringStyler.filterToStringCreator;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @Author JL, Darkness
 * @Date 2023/10/12
 * @Version V1.0
 * Since: 1.0
 */
@Slf4j
public class AuthorizeGatewayFilter implements GatewayFilter, Ordered {

    private boolean enabled;

    public AuthorizeGatewayFilter(boolean enabled){
        this.enabled = enabled;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (enabled) {
            //判断是否开启了签权验证，- Authorize=true，只有为true才继续执行，否则直接跳过
            ServerHttpRequest request = exchange.getRequest();
            String clientIp = NetworkIpUtils.getIpAddress(request);
            Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
            String routeId = RouteUtils.getBalancedToRouteId(route.getId());
            FilterHandler headerFilter = createHandler();
            //获取缓存中的指定key路由对象
            Object obj = RouteCache.get(routeId);
            if (obj == null) {
                return HttpResponseUtils.writeUnauth(exchange.getResponse(), "未获取到指定网关注册服务路由信息或路由请求无效！");
            }
            try {
                //执行header,ip,parameter,time,cookie验证
                headerFilter.handler(request, (GatewayAppRoute) obj);
            } catch (Exception e) {
                log.error("网关转发客户端【{}】路由请求【{}】，执行验证异常：", clientIp, route.getId(), e);
                return HttpResponseUtils.writeUnauth(exchange.getResponse(), "网关转发客户端【" + clientIp + "】路由请求【" + route.getId() + "】，执行验证异常：" + e.getMessage());
            }
        }
        return chain.filter(exchange);
    }

    /**
     * 责任链初始化
     * @return
     */
    public FilterHandler createHandler(){
        FilterHandler cookieFilter = new CookieFilter(null);
        FilterHandler parameterFilter = new ParameterFilter(cookieFilter);
        FilterHandler ipFileter = new IpFileter(parameterFilter);
        FilterHandler timeFilter = new TimeFilter(ipFileter);
        FilterHandler headerFilter = new HeaderFilter(timeFilter);
        return headerFilter;
    }

    @Override
    public String toString() {
        return filterToStringCreator(AuthorizeGatewayFilter.this)
                .append("enabled", true)
                .append("order", getOrder())
                .toString();
    }

    @Override
    public int getOrder() {
        return 4;
    }

}
