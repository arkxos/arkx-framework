package com.rapidark.cloud.platform.gateway.filter.authorize;

import com.rapidark.cloud.platform.gateway.framework.entity.RouteConfig;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @Description 责任链设计模式，抽象业务父类
 * @Author JL
 * @Date 2020/05/25
 * @Version V1.0
 */
public abstract class FilterHandler {

    public FilterHandler handler = null;
    protected RouteConfig routeConfig;

    public void handler(ServerHttpRequest request, RouteConfig routeConfig){
        this.routeConfig = routeConfig;
        handleRequest(request);
        nextHandle(request);
    }

    public abstract void handleRequest(ServerHttpRequest request);

    public void nextHandle(ServerHttpRequest request){
        if (handler != null){
            handler.handler(request, routeConfig);
        }
    }
}
