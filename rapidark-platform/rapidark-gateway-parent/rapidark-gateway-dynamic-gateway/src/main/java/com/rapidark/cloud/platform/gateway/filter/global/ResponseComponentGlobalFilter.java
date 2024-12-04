package com.rapidark.cloud.platform.gateway.filter.global;

import com.alibaba.fastjson.JSONObject;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.cloud.platform.gateway.cache.RotueGroovyCache;
import com.rapidark.cloud.platform.gateway.service.DynamicGroovyService;
import com.rapidark.cloud.platform.gateway.vo.GroovyHandleData;
import com.rapidark.cloud.platform.gateway.framework.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import jakarta.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @Description 自定义组件全局过滤器，对所有响应触发GroovyScript规则引擎动态脚本较验
 * @Author JL
 * @Date 2022/3/16
 * @Version V1.0
 */
@Slf4j
@Component
public class ResponseComponentGlobalFilter implements GlobalFilter, Ordered {
    @Resource
    private DynamicGroovyService dynamicGroovyService;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange.mutate()
                .response(getServerHttpResponseDecorator(exchange))
                .build());
    }

    /**
     * 对response响应数据流重新包装，返回新的ServerHttpResponse对象
     * @param exchange
     * @return
     */
    public ServerHttpResponse getServerHttpResponseDecorator(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String clientIp = NetworkIpUtils.getIpAddress(request);

        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
        String routeId = RouteUtils.getBalancedToRouteId(route.getId());

        // 获取请求参数
        Map<String,String> paramMap = request.getQueryParams().toSingleValueMap();

        return new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> responseBody) {
                //只有正常响应才进入到groovy执行链路中
                if (!HttpStatus.OK.equals(getStatusCode())) {
                    return super.writeWith(responseBody);
                }

                //已获得网关缓存，不在进入groovy执行链路中
                String cacheControl = exchange.getAttribute(Constants.CACHE_CONTROL);
                if (Constants.GATEWAY_CACHE.equals(cacheControl)) {
                    return super.writeWith(responseBody);
                }

                DataBufferFactory dataBufferFactory = super.bufferFactory();
                Mono<DataBuffer> dataBufferMono = DataBufferUtils.join(Flux.from(responseBody))
                        .publishOn(Schedulers.parallel())
                        .map(buffer -> getBody(buffer)).map(body -> {
                            byte [] bytes ;
                            //判断Groovy缓存中的指定路由ID是否存在
                            if (RotueGroovyCache.containsKey(routeId)){
                                // 封装请求参数，用于groovy规则引擎动态脚本中执行
                                GroovyHandleData handleData = new GroovyHandleData(paramMap, body);
                                try {
                                    handleData = dynamicGroovyService.responseHandle(exchange, handleData);
                                    body = handleData.getBody();
                                    log.info("网关转发客户端【{}】路由请求【{}】，执行Groovy规则引擎动态脚本组件，返回内容：\n{}", clientIp, routeId, body);
                                } catch (InvocationTargetException e) {
                                    log.error("网关转发客户端【{}】路由请求【{}】，执行Groovy规则引擎动态脚本反射组件异常：", clientIp, routeId, e);
                                    getHeaders().add(Constants.RULE_ERROR, Constants.FAILED);
                                    body = getErrMsg(clientIp, routeId, e.getTargetException().getMessage());
                                } catch (Exception e) {
                                    log.error("网关转发客户端【{}】路由请求【{}】，执行Groovy规则引擎组件异常：", clientIp, routeId, e);
                                    getHeaders().add(Constants.RULE_ERROR, Constants.FAILED);
                                    body = getErrMsg(clientIp, routeId, e.getMessage());
                                }
                                // 重新计算内容长度,否则长度与内容不匹配会被浏览器、客户端不显示或显示不完整
                                bytes = StringUtils.isBlank(body) ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
                                getHeaders().setContentLength(bytes.length);
                            } else {
                                bytes = body.getBytes(StandardCharsets.UTF_8);
                            }
                            //缓存响应结果
                            cacheResponseBody(exchange, routeId, body);
                            return bytes;
                        }).map(dataBufferFactory::wrap);
                return getDelegate().writeWith(dataBufferMono);
            }

        };
    }


    /**
     * 从dataBuffer缓冲区中获取response响应的body内容（注意：基于netty下所有的响应数据会写入到dataBuffer中）
     * @param dataBuffer
     * @return
     */
    private String getBody(DataBuffer dataBuffer){
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 缓存响应结果（秒）
     * @param exchange
     * @param body
     */
    private void cacheResponseBody(ServerWebExchange exchange, String routeId, String body) {
        String key = exchange.getAttributeOrDefault(Constants.GATEWAY_CACHE_KEY, "");
        long ttl = exchange.getAttributeOrDefault(Constants.GATEWAY_MAX_AGE, 0L);
        if (StringUtils.isNotBlank(key) && ttl > 0) {
            redisTemplate.opsForValue().set(key, body, ttl, TimeUnit.SECONDS);
            String routeKey = String.format(RouteConstants.CACHE_ROUTE_KEY, routeId);
            redisTemplate.opsForSet().add(routeKey, key);
        }
    }

    /**
     * 包装异常消息
     * @param clientIp
     * @param routeId
     * @param errMsg
     * @return
     */
    private String getErrMsg(String clientIp, String routeId, String errMsg){
        String message= String.format("网关转发客户端【%s】路由请求【%s】，执行组件异常：%s", clientIp, routeId, errMsg);
        return JSONObject.toJSONString(ResponseResult.failed(message));
    }

    @Override
    public int getOrder() {
        // 注意一定要设置成负数，保证优先级别高，否则无法触发
        return -2;
    }
}
