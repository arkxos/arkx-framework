package com.rapidark.cloud.platform.gateway.filter.global;

import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @Description 由于SpringCloudGateway转发网关路由服务后，由服务端返回的异常，基于Setinel组件的熔断降级事件，在DegradeRule规则的异常比例、异常数模式下无效
 * 此处通过将转发路由服务端后的异常重新包装成当前网关服务的异常，从而触发Setnel组件基于DegradeRule规则的异常统计，在根据异常比例或异常数规则阈值进行熔断降级
 * @Author JL
 * @Date 2023/03/18
 * @Version V1.0
 */
@Slf4j
public class CustomSentinelGatewayFilter extends SentinelGatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 注意Setinel目前熔断只支持RT慢请求降级，对于异常比例和异常数降级暂时无支持，参见：https://github.com/alibaba/Sentinel/issues/1842
        // 此处是将网关路由返回的异常，重新包装成gateway网关异常，从而激活Setinel基于服务端的异常统计，在达到阈值后即触发融断降级
        ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (getRawStatusCode() >= 400) {
                    HttpHeaders headers = getHeaders();
                    if (!headers.containsKey(Constants.RULE_ERROR)) {
                        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
                        for (DegradeRule degradeRule : DegradeRuleManager.getRules()) {
                            //只处理有配置熔断降级规则的路由,注意此处会丢弃原有异常信息，以new Exception()为新异常结果；
                            if (route.getId().equals(degradeRule.getResource())) {
                                return Mono.error(new Exception("sentinel gateway filter throw degrade exception!"));
                            }
                        }
                    }
                    headers.remove(Constants.RULE_ERROR);
                }
                return super.writeWith(body);
            }
        };
        // replace response with decorator
        return super.filter(exchange.mutate().response(responseDecorator).build(), chain);
    }

}
