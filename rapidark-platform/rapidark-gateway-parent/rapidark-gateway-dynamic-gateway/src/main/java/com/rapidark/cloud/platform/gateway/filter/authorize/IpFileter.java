package com.rapidark.cloud.platform.gateway.filter.authorize;

import com.rapidark.cloud.platform.gateway.framework.util.NetworkIpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @Description 验证当前IP是否可访问（只限定指定IP访问）
 * @Author JL
 * @Date 2020/05/25
 * @Version V1.0
 */
@Slf4j
public class IpFileter extends FilterHandler {

    public IpFileter(FilterHandler handler){
        this.handler = handler;
    }

    @Override
    public void handleRequest(ServerHttpRequest request){
        if (gatewayAppRoute.getFilterAuthorizeName().contains("ip")){
            log.info("处理网关路由请求{},执行ip过滤 ", gatewayAppRoute.getId());
            String ip = NetworkIpUtils.getIpAddress(request);
            if (gatewayAppRoute.getAccessIp()!=null && gatewayAppRoute.getAccessIp().contains(ip)){
            }else {
                throw new IllegalStateException("执行ip过滤,自定义ip验证不通过 请求源="+ip+",自定义目标=" + gatewayAppRoute.getAccessIp());
            }
        }
    }

}
