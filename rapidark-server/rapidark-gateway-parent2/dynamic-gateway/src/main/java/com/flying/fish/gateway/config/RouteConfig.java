package com.flying.fish.gateway.config;


import com.rapidark.cloud.gateway.cache.RouteCache;
import com.flying.fish.gateway.filter.IpGatewayFilter;
import com.flying.fish.gateway.filter.TokenGatewayFilter;
import com.flying.fish.gateway.filter.factory.AuthorizeGatewayFilterFactory;
import com.flying.fish.gateway.service.LoadRouteService;
import com.flying.fish.gateway.vo.GatewayRouteConfig;
import com.rapidark.cloud.gateway.formwork.config.ApplicationContextProvider;
import com.rapidark.cloud.gateway.formwork.entity.GatewayAppRoute;
import com.rapidark.cloud.gateway.formwork.repository.GatewayAppRouteRepository;
import com.rapidark.common.utils.Constants;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.Route.AsyncBuilder;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.BooleanSpec;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.function.Function;

/**
 * @Description 配置路由规则
 * @Author jianglong
 * @Date 2020/04/27
 * @Version V1.0
 */
@Slf4j
@Configuration
public class RouteConfig {

    @Resource
    private ApplicationContextProvider applicationContextProvider;

    @Resource
    private AuthorizeGatewayFilterFactory authorizeGatewayFilterFactory;

    @Resource
    private GatewayAppRouteRepository gatewayAppRouteRepository;

    @Resource
    private KeyResolver uriKeyResolver;

    @Resource
    private LoadRouteService loadRouteService;

    /**
     * 项目启动时,初始化RouteLocator对象
     * @param builder
     * @return
     */
    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RouteLocator routeLocatorObject(RouteLocatorBuilder builder){
        return builder.routes().build();
    }

    /**
     * 通过Bean实例化，配置单个route规则(方法弃用,采用DataRouteDefinitionRepository事件监听机制刷新gateway内存配置)
     * @param builder
     * @return
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Deprecated
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        String routeId = "gatewayAppRoute-producer";
        return builder.routes().route(r ->
                r.method("GET").and().path("/gatewayAppRoute/producer/**").
                        filters(f -> f.stripPrefix(1).
                                addRequestParameter("version", "test").
                                requestRateLimiter(config -> {
                                    //用于限流的键的解析器的 Bean 对象的名字
                                    config.setKeyResolver(uriKeyResolver);
                                    //每1秒限制请求数(令牌数)，令牌桶的容量
                                    config.setRateLimiter(this.redisRateLimiter(1, 1));
                                }).
                                hystrix(config -> {
                                    //熔断名称
                                    config.setName("fallbackcmd");
                                    //熔断回调方法
                                    config.setFallbackUri("forward:/fallback");
                                })
                        ).
                        //生产者服务注册名,格式：“lb://server-name”
                        uri("lb://EXAMPLES").
                        //配置多个自定义网关过滤器
                        filters(new IpGatewayFilter(routeId),
                                new TokenGatewayFilter(routeId)
//                                ,new ClientIdGatewayFilter(routeId)
                        ).
                        //配置路由ID
                        id(routeId)
                ).build();
    }

    /**
     * 项目启动时，加载数据库已存在的路由配置，实现批量配置route规则(方法弃用,采用DataRouteDefinitionRepository事件监听机制刷新gateway内存配置)
     * @param builder
     * @return
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Deprecated
    public RouteLocator routeLocators(RouteLocatorBuilder builder){
        GatewayAppRoute query = new GatewayAppRoute();
        query.setStatus(Constants.YES);
        List<GatewayAppRoute> gatewayAppRouteList = gatewayAppRouteRepository.findAll(Example.of(query));
        RouteLocatorBuilder.Builder routeBuilder = builder.routes();
        gatewayAppRouteList.forEach(route->{
            RouteCache.put(route.getId(), route);
            GatewayRouteConfig gatewayRouteConfig = loadRouteService.loadRouteConfig(route);
            routeBuilder.route(r -> {
                //设置断言
                BooleanSpec booleanSpec = r.path(gatewayRouteConfig.getPath());
                //设置请求模式
                if (StringUtils.isNotBlank(gatewayRouteConfig.getMethod())){
                    booleanSpec = booleanSpec.and().method(gatewayRouteConfig.getMethod());
                }
                //设置限流、熔断、鉴权
                Function<GatewayFilterSpec, UriSpec> fn = f -> {
                    if (StringUtils.isNotBlank(gatewayRouteConfig.getRequestParameterName())){
                        f.addRequestParameter(gatewayRouteConfig.getRequestParameterName(), gatewayRouteConfig.getRequestParameterValue());
                    }
                    if (gatewayRouteConfig.getKeyResolver() != null){
                        f.requestRateLimiter(config -> {
                            //用于限流的键的解析器的 Bean 对象的名字
                            config.setKeyResolver(gatewayRouteConfig.getKeyResolver());
                            //每1秒限制请求数(令牌数)，令牌桶的容量
                            config.setRateLimiter(this.redisRateLimiter(gatewayRouteConfig.getReplenishRate(), gatewayRouteConfig.getBurstCapacity()));
                        });
                    }
                    if (StringUtils.isNotBlank(gatewayRouteConfig.getHystrixName())){
                        f.hystrix(config -> {
                            //熔断名称,熔断回调方法
                            config.setName(gatewayRouteConfig.getHystrixName());
                            config.setFallbackUri(gatewayRouteConfig.getFallbackUri());
                            if (gatewayRouteConfig.getSetter() != null) {
                                config.setSetter(gatewayRouteConfig.getSetter());
                            }
                        });
                    }
                    //添加自定义鉴权工厂类
                    if (gatewayRouteConfig.isAuthorize()){
                        f.filter(authorizeGatewayFilterFactory.apply((c) -> c.setEnabled(true)));
                    }
                    //设置断言截取
                    if (gatewayRouteConfig.getStripPrefix() > 0) {
                        f.stripPrefix(gatewayRouteConfig.getStripPrefix());
                    }
                    return f;
                };
                AsyncBuilder asyncBuilder = booleanSpec.filters(fn).uri(gatewayRouteConfig.getUri());
                //添加过滤器
                if (gatewayRouteConfig.getGatewayFilter() != null){
                    asyncBuilder.filters(gatewayRouteConfig.getGatewayFilter());
                }
                //添加ID
                asyncBuilder.id(gatewayRouteConfig.getId());
                return asyncBuilder;
            });
        });
        return routeBuilder.build();
    }

    /**
     * 对请求基于ip的访问进行限流
     *
     * @return
     */
    @Primary
    @Bean("hostAddrKeyResolver")
    public KeyResolver hostAddrKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

    /**
     * 根据uri限流
     *
     * @return
     */
    @Bean("uriKeyResolver")
    public KeyResolver uriKeyResolver() {
        return (exchange) -> {
            return Mono.just(exchange.getRequest().getURI().getPath());
        };
    }

    /**
     * 根据user限流
     *
     * @return
     */
    @Bean("requestIdKeyResolver")
    KeyResolver requestIdKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("requestId"));
    }

    /**
     * 限流参数
     * @param defaultReplenishRate 每1秒限制请求数(令牌数)
     * @param defaultBurstCapacity 令牌桶的容量
     * @return
     */
    public RedisRateLimiter redisRateLimiter(int defaultReplenishRate, int defaultBurstCapacity){
        RedisRateLimiter redisRateLimiter= new RedisRateLimiter(defaultReplenishRate, defaultBurstCapacity);
        redisRateLimiter.setApplicationContext(applicationContextProvider.getApplicationContext());
        return redisRateLimiter;
    }

}