package com.flying.fish.gateway.service.load;

import com.flying.fish.formwork.dao.RouteDao;
import com.flying.fish.formwork.entity.Balanced;
import com.flying.fish.formwork.entity.LoadServer;
import com.flying.fish.formwork.entity.Route;
import com.flying.fish.formwork.service.BalancedService;
import com.flying.fish.formwork.service.LoadServerService;
import com.flying.fish.formwork.util.Constants;
import com.flying.fish.gateway.cache.RouteCache;
import com.flying.fish.gateway.service.DynamicRouteService;
import com.flying.fish.gateway.service.LoadRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description 初始化负载均衡配置、网关路由配置
 * @Author JL
 * @Date 2021/09/22
 * @Version V1.0
 */
@Slf4j
@Service
public class InitRouteService {

    private List<RouteDefinition> routeDefinitions = new ArrayList<>();

    @Resource
    private RouteDao routeDao;
    @Resource
    private BalancedService balancedService;
    @Resource
    private LoadRouteService loadRouteService;
    @Resource
    private LoadServerService loadServerService;
    @Resource
    private DynamicRouteService dynamicRouteService;

    /**
     * 初始化执行
     */
    @PostConstruct
    public void init(){
        //一定要清空routeDefinitions否则每次刷新会往集合中添加重复数据
        routeDefinitions.clear();
        initLoadRoute();
        initLoadBalanced();
        dynamicRouteService.addAll(routeDefinitions);
    }

    /**
     * 初始化完毕后，加载路由
     */
    public void initLoadRoute(){
        Route query = new Route();
        query.setStatus(Constants.YES);
        try {
            List<Route> routeList = routeDao.findAll(Example.of(query));
            if (CollectionUtils.isEmpty(routeList)) {
                log.error("初始化网关路由，无可用网关路由配置...");
                return ;
            }
            routeList.forEach(r -> {
                RouteCache.put(r.getId(), r);
                routeDefinitions.add(loadRouteService.loadRouteDefinition(r));
            });
            log.info("初始化加载网关路由配置共{}条", routeList.size());
        }catch(Exception e){
            log.error("加载数据库中网关路由配置异常：",e);
        }
    }

    /**
     * 初始化完毕后，加载负载路由
     */
    public void initLoadBalanced(){
        Route query = new Route();
        query.setStatus(Constants.YES);
        Balanced balanced = new Balanced();
        balanced.setStatus(Constants.YES);
        List<Route> balancedRouteList = new ArrayList<>();
        List<Balanced> balancedList = balancedService.findAll(balanced);
        if (CollectionUtils.isEmpty(balancedList)) {
            log.info("初始化网关负载均衡路由，无可用配置...");
            return ;
        }
        //查找负载均衡关联的网关路由列表
        List<LoadServer> loadServerList = loadServerService.findAll(new LoadServer());
        if (CollectionUtils.isEmpty(loadServerList)) {
            log.error("初始化网关负载均衡路由，未添加网关路由配置...");
            return ;
        }
        //查询所有可用的网关路由列表
        List<Route> routeList = routeDao.findAll(Example.of(query));
        if (CollectionUtils.isEmpty(routeList)){
            log.error("初始化网关负载均衡路由，无可用网关路由配置...");
            return ;
        }
        Map<String, Route> routeMap =  routeList.stream().collect(Collectors.toMap(Route::getId, r->r));
        // 将同一个负载下的网关路由放在同一个集合中(注意：过滤掉权重值为0的网关，表示无流量流入，无需创建网关)
        Map<String, List<LoadServer>> serverRouteMap = loadServerList.stream().filter(s->s.getWeight()>0)
                .collect(Collectors.toMap(LoadServer::getBalancedId,
                    l-> new ArrayList<LoadServer>(){
                        {add(l);}
                    },
                    (List<LoadServer> v1, List<LoadServer> v2) ->{
                        v1.addAll(v2);
                        return v1;
                    })
                );
        try {
            // 遍历负载均衡配置，组装生成新的route结构，并加载到gateway网关路由集合中
            for (Balanced b : balancedList){
                //查找负载下注册的服务
                List<LoadServer> serverList = serverRouteMap.get(b.getId());
                if (CollectionUtils.isEmpty(serverList)) {
                    continue;
                }
                serverList.forEach(s -> {
                    Route route = routeMap.get(s.getRouteId());
                    if (route != null) {
                        Route bRoute = new Route();
                        BeanUtils.copyProperties(route, bRoute);
                        loadServerService.setBalancedRoute(b, s, bRoute);
                        //添加新路由集合中
                        balancedRouteList.add(bRoute);
                    }
                });
            }
            //将新的路由加载网关路由集合中
            balancedRouteList.forEach(r->{
                //记录到本地缓存中
                RouteCache.put(r.getId(), r);
                //添加新的负载均衡路由对象
                routeDefinitions.add(loadRouteService.loadRouteDefinition(r));
            });
        }catch(Exception e){
            log.error("加载数据库中网关负载路由配置异常：",e);
        }
        log.info("初始化加载网关负载路由配置共{}条", balancedRouteList.size());
    }

}
