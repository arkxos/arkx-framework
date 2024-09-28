package com.flying.fish.example.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @Description 过滤网关路由的心跳检测,如果请求的headers中存在"Keepalive:rapidark-gateway"则表示心跳请求
 * @Author JL
 * @Date 2021/04/27
 * @Version V1.0
 */
@Slf4j
@Component
public class CustomGlobalFilter implements WebFilter {

    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";
    private static final String HEADER_KEEPALIVE = "Keepalive";
    private static final String GATEWAY_KEEPALIVE = "rapidark-gateway";
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        if (headers != null && headers.size()>0){
            //"Keepalive", "rapidark-gateway"
            String keepalive = headers.getFirst(HEADER_KEEPALIVE);
            if (keepalive != null && keepalive.equals(GATEWAY_KEEPALIVE)){
                log.info("心跳检测：{}", GATEWAY_KEEPALIVE);
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.OK);
                response.getHeaders().add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
                DataBuffer buffer = response.bufferFactory().wrap(HttpStatus.OK.getReasonPhrase().getBytes(StandardCharsets.UTF_8));
                return response.writeWith(Mono.just(buffer));
            }
        }
        return chain.filter(exchange);
    }
}
