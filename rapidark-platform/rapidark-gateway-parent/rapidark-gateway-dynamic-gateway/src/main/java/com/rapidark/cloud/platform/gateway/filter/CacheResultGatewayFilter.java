package com.rapidark.cloud.platform.gateway.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rapidark.cloud.platform.gateway.framework.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.rapidark.cloud.platform.gateway.support.CustomGatewayToStringStyler.filterToStringCreator;
import static io.netty.util.internal.EmptyArrays.EMPTY_BYTES;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @Description 基于redis做响应结果缓存策略
 * @Author JL
 * @Date 2023/10/10
 * @Version V1.0
 */
@Slf4j
public class CacheResultGatewayFilter implements GatewayFilter, Ordered {

    private RedisTemplate<String,String> redisTemplate;
    private long ttl;

    public CacheResultGatewayFilter(long ttl, RedisTemplate<String,String> redisTemplate) {
        this.ttl = ttl;
        this.redisTemplate = redisTemplate;
    }

    private static Cache<String, Object> cache;

    /**
     * 初始化Caffeine缓存配置
     * Caffeine是基于JDK8的高性能缓存库
     */
    static {
        cache = Caffeine.newBuilder()
                .maximumSize(Constants.MAX_SIZE)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (ttl <= 0) {
            return chain.filter(exchange);
        }
        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
        String routeId = RouteUtils.getBalancedToRouteId(route.getId());
        //强制限制每个网关路由，最大可缓存请求总数上限为100，达到100则该路由不在支持缓存功能
        //否则无限制，当出现高频动态参数请求时，将严重占用网关资源影响性能
        if (cacheRequestNun(routeId) > Constants.CACHE_TOTAL) {
            log.debug("> 100 ,not cache request routeId:{}", routeId);
            return chain.filter(exchange);
        }

        //databuffer管理工厂类
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        ServerHttpRequest request = exchange.getRequest();
        return DataBufferUtils.join(request.getBody())
                .defaultIfEmpty(dataBufferFactory.wrap(EMPTY_BYTES))
                //如果并未设置body值，则默认创建空dataBuffer对象
                .flatMap(dataBuffer -> {
                    // 获取请求参数
                    Map<String,String> paramMap = request.getQueryParams().toSingleValueMap();
                    String requestMd5 = requestDataMd5(paramMap, dataBuffer);
                    String cacheKey = String.format(RouteConstants.RESPONSE_RESULT_KEY, routeId, requestMd5);
                    ServerHttpResponse response = exchange.getResponse();

                    //查询缓存记录,如果存在，则直接输出，不在查询路由服务
                    if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
                        exchange.getAttributes().put(Constants.CACHE_CONTROL, Constants.GATEWAY_CACHE);
                        response.getHeaders().add(Constants.CACHE_CONTROL, Constants.GATEWAY_CACHE);
                        response.getHeaders().add(Constants.CONTENT_TYPE, request.getHeaders().toSingleValueMap().get(Constants.CONTENT_TYPE));
                        String result = redisTemplate.opsForValue().get(cacheKey);
                        return HttpResponseUtils.write(response, response.getStatusCode(), result);
                    }
                    exchange.getAttributes().put(Constants.GATEWAY_CACHE_KEY, cacheKey);
                    exchange.getAttributes().put(Constants.GATEWAY_MAX_AGE, ttl);
                    raiseLocalCacheRequestNum(routeId);
                    return chain.filter(exchange);
                });
    }

    /**
     * 请求参与与body进行md5加密
     * @param paramMap
     * @param dataBuffer
     * @return
     */
    private String requestDataMd5(Map<String,String> paramMap, DataBuffer dataBuffer) {
        String content = requestParam(paramMap) + requestBody(dataBuffer);
        return Md5Utils.md5Str(content);
    }

    /**
     * 获取请求参数
     * @param paramMap
     * @return
     */
    private String requestParam(Map<String,String> paramMap) {
        StringBuilder query = new StringBuilder();
        if (!CollectionUtils.isEmpty(paramMap)) {
            //先基于KEY进行排序
            paramMap.keySet().stream().sorted().forEach(k->query.append("&").append(k).append('=').append(paramMap.get(k)));
        }
        return query.toString();
    }

    /**
     * 从dataBuffer缓冲区中获取request请求的body内容（注意：基于netty下所有的请求数据会写入到dataBuffer中）
     * @param dataBuffer
     * @return
     */
    private String requestBody(DataBuffer dataBuffer){
        int count = dataBuffer.readableByteCount();
        if (count > 0) {
            byte[] bytes = new byte[count];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * 获取当前路由请求的缓存数量
     * (注意：此处的获取缓存总量会与实际请求记录的数量有差异，只有local缓存失效后（local缓存过期时间不宜设置过大），才会获得真实请求数量，
     * 因此存在容错空间，所以不适用于精准控制和高频动态参数请求，并且依赖redis在短时间内大量请求缓存过期策略，以防止无限制占用网关redis资源，影响网关处理性能；
     * 举例：1分钟内已知local缓存route_id=xxx所记录的大小为10，突发高并发+动态参数请求，可能在1分钟内暴涨到>100，此时不再取缓存请求结果，直接跳到路由服务
     * 待1分钟后local缓存route_id=xxx过期清除，重新获得redis中真实请求响应数量,如果>100，此后所有新的请求直接跳转到路由服务，不再记录响应缓存;
     * 待redis中请求缓存key部份失效后，同步到此route_id=xxx，并且记录请求响应缓存总量降低到100以内，则再次重新缓存请求响应数据)
     * @param routeId 网关路由ID
     * @return
     */
    private long cacheRequestNun(String routeId) {
        //查询local缓存中路由请求缓存数量
        String key = String.format(RouteConstants.CACHE_ROUTE_KEY, routeId);
        Long size = (Long) cache.getIfPresent(key);
        if (size == null) {
            //local缓存失效后，在从redis缓存中获得真实路由缓存数量
            size = redisTemplate.opsForSet().size(key);
        }
        return size == null ? 1L : size + 1;
    }

    /**
     * 递增local缓存路由ID的通过请求次数
     * @param routeId
     */
    private void raiseLocalCacheRequestNum(String routeId){
        String key = String.format(RouteConstants.CACHE_ROUTE_KEY, routeId);
        Long size = (Long) cache.getIfPresent(key);
        size = size == null ? 1L : size + 1;
        cache.put(key, size);
    }

    @Override
    public String toString() {
        return filterToStringCreator(CacheResultGatewayFilter.this)
                .append("ttl", ttl)
                .append("order", getOrder())
                .toString();
    }

    /**
     * 注意：此过滤器在业务规划上，排序尽量排在所有自定义filter集合末尾
     * @return
     */
    @Override
    public int getOrder() {
        return 5;
    }
}
