package com.flying.fish.gateway.filter.authorize;

import com.rapidark.cloud.gateway.formwork.entity.GatewayAppRoute;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @Description 责任链设计模式，抽象业务父类
 * @Author jianglong
 * @Date 2020/05/25
 * @Version V1.0
 */
public abstract class FilterHandler {

    public FilterHandler handler = null;
    protected GatewayAppRoute gatewayAppRoute;

    public void handler(ServerHttpRequest request, GatewayAppRoute gatewayAppRoute){
        this.gatewayAppRoute = gatewayAppRoute;
        handleRequest(request);
        nextHandle(request);
    }

    public abstract void handleRequest(ServerHttpRequest request);

    public void nextHandle(ServerHttpRequest request){
        if (handler != null){
            handler.handler(request, gatewayAppRoute);
        }
    }
}
