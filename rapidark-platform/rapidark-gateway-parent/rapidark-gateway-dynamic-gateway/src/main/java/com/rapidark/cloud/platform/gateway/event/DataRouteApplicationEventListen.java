package com.rapidark.cloud.platform.gateway.event;

import com.rapidark.cloud.platform.gateway.cache.RouteCache;
import com.rapidark.cloud.platform.gateway.framework.entity.GatewayAppRoute;
import com.rapidark.cloud.platform.gateway.framework.repository.GatewayAppRouteRepository;
import com.rapidark.cloud.platform.gateway.framework.service.BalancedService;
import com.rapidark.cloud.platform.gateway.framework.service.LoadServerService;
import com.rapidark.cloud.platform.gateway.service.LoadRouteService;
import com.rapidark.framework.common.utils.Constants;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description 注册网关路由事件，动态加载数据库网关路由信息，监听路由配置事件，重新动态加载网关路由数据(已过时)
 * @Author JL
 * @Date 2020/05/27
 * @Version V1.0
 */
@Slf4j
@Component
@Deprecated
public class DataRouteApplicationEventListen implements RouteDefinitionLocator,ApplicationEventPublisherAware {
    @Resource
    private GatewayAppRouteRepository gatewayAppRouteRepository;
    @Resource
    private LoadRouteService loadRouteService;
    @Resource
    private BalancedService balancedService;
    @Resource
    private LoadServerService loadServerService;
    private ApplicationEventPublisher publisher;
    private List<RouteDefinition> routeDefinitions = new ArrayList<>();

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * 监听事件刷新配置；
     * 1.CustomApplicationEvent发布后，即触发listenEvent事件方法；
     * 2.publisher.publishEvent方法执行RefreshRoutesEvent事件后；
     * 3.gateway加调用getRouteDefinitions方法重新获取网关路由集合配置并刷新内存
     * （已过时，启用nacos配置监听事件，参见：NacosConfigRefreshEventListener）
     */
    @Deprecated
    @EventListener(classes = DataRouteApplicationEvent.class)
    public void listenEvent() {
        // Todo 停止使用，请用InitRouteService类init()方法
        //init();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 获取所有网关路由信息
     * @return
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(routeDefinitions);
    }

//    @Deprecated
//    @Override
//    public Mono<Void> save(Mono<RouteDefinition> gatewayAppRoute) {
//        return Mono.defer(() -> Mono.error(new NotFoundException("此save方法不提供功能实现，请勿调用")));
//    }

//    @Deprecated
//    @Override
//    public Mono<Void> delete(Mono<String> routeId) {
//        return Mono.defer(() -> Mono.error(new NotFoundException("此delete方法不提供功能实现，请勿调用")));
//    }

    @Deprecated
    //@PostConstruct
    public void init(){

    }

}
