package com.rapidark.cloud.gateway.server.filter;

import com.rapidark.cloud.gateway.server.filter.context.GatewayContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 移除GatewayContext过滤器
 * @author darkness
 * @date 2022/5/14 17:36
 * @version 1.0
 */
@Slf4j
public class RemoveGatewayContextFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange).doFinally(s -> exchange.getAttributes().remove(GatewayContext.CACHE_GATEWAY_CONTEXT));
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
