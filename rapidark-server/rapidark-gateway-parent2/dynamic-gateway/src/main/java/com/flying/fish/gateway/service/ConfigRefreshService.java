package com.flying.fish.gateway.service;

import com.alibaba.fastjson.JSONObject;
import com.flying.fish.gateway.cache.IpListCache;
import com.flying.fish.gateway.cache.RegServerCache;
import com.flying.fish.gateway.cache.RouteCache;
import com.flying.fish.gateway.event.ApplicationEventPublisherFactory;
import com.flying.fish.gateway.vo.GatewayRegServer;
import com.rapidark.cloud.base.client.model.entity.OpenApp;
import com.rapidark.cloud.gateway.formwork.bean.GatewayNacosConfigBean;
import com.rapidark.cloud.gateway.formwork.entity.*;
import com.rapidark.cloud.gateway.formwork.service.*;
import com.rapidark.cloud.gateway.formwork.util.Constants;
import com.rapidark.cloud.gateway.formwork.util.RouteConstants;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Description 为NacosConfigRefreshEventListener监听事件服务提供网关路由更新与删除方法
 * @Author JL
 * @Date 2021/09/17
 * @Version V1.0
 */
@Slf4j
@Service
public class ConfigRefreshService {

    @Resource
    private RouteService routeService;
    @Resource
    private LoadRouteService loadRouteService;
    @Resource
    private DynamicRouteService dynamicRouteService;
    @Resource
    private SecureIpService secureIpService;
    @Resource
    private RegServerService regServerService;
    @Resource
    private BalancedService balancedService;
    @Resource
    private LoadServerService loadServerService;
    @Resource
    private ClientService clientService;
    @Resource
    private DynamicGroovyService dynamicGroovyService;
    @Resource
    private ApplicationEventPublisherFactory applicationEventPublisherFactory;

    /**
     * 解析nacos推送的网关路由动态变更配置
     * @param gatewayConfig
     */
    public void setGatewayConfig(String gatewayConfig){
        log.info("获取Nacos中网关路由配置内容，gateway={}" , gatewayConfig);
        try {
            GatewayNacosConfigBean gatewayNacosConfig = JSONObject.parseObject(gatewayConfig, GatewayNacosConfigBean.class);
            refreshGatewayConfig(gatewayNacosConfig);
        } catch (Exception e){
            log.error("加载Nacos中网关路由配置异常！" + e.getMessage() + "\n", e);
        }
    }

    /**
     * 重新设置网关路由配置：balanced,route,client,ip
     * @param gatewayNacosConfig
     */
    public void refreshGatewayConfig(GatewayNacosConfigBean gatewayNacosConfig){
        String balancedId = gatewayNacosConfig.getBalancedId();
        String routeId = gatewayNacosConfig.getRouteId();
        Long regServerId = gatewayNacosConfig.getRegServerId();
        String clientId = gatewayNacosConfig.getClientId();
        String ip = gatewayNacosConfig.getIp();
        Long groovyScriptId = gatewayNacosConfig.getGroovyScriptId();
        //刷新配置项
        refreshBalanced(balancedId);
        refreshRoute(routeId);
        refreshRegServer(regServerId);
        refreshClient(clientId);
        refreshIp(ip);
        refreshGroovyScript(groovyScriptId);
    }

    /**
     * 重新加载route网关负载均衡路由并刷新网关
     * @param balancedId
     */
    public void refreshBalanced(String balancedId){
        if (StringUtils.isBlank(balancedId)) {
            return ;
        }
        Balanced balanced = balancedService.findById(balancedId);
        if (balanced == null){
            dynamicRouteService.deleteBalanced(balancedId);
            log.info("未获取网关负载均衡数据库配置！balancedId={}" , balancedId);
            return;
        }
        List<LoadServer> loadServerList = loadServerService.queryByBalancedId(balancedId);
        Assert.notEmpty(loadServerList, getExceptionMessage("未获取网关负载均衡数据库配置路由列表！balancedId=" , balancedId));
        boolean isClose = Constants.NO.equals(balanced.getStatus());
        Route route;
        String balancedRouteId;
        //不管任何操作先发布清除事件，清除分组权重配置
        publisherWeightRemoveEvent(balancedId);
        //重新加载每个负载均衡网关路由
        for (LoadServer loadServer : loadServerList){
            //获取route，改变参数，构造一个负载均衡routeId
            balancedRouteId = loadServerService.setBalancedRouteId(balancedId, loadServer.getRouteId());
            //如果当负载均衡网关已禁用，则删除该负载均衡下所有网关路由
            if (isClose){
                dynamicRouteService.delete(balancedRouteId);
                continue;
            }
            //如果路由权重为0，则表示无流量调用，则清除负载路由
            if (loadServer.getWeight() <= 0){
                dynamicRouteService.delete(balancedRouteId);
                continue;
            }
            route = routeService.findById(loadServer.getRouteId());
            // 状态，0是启用，1是禁用
            if (route == null || Constants.NO.equals(route.getStatus())) {
                continue;
            }
            loadServerService.setBalancedRoute(balanced, loadServer, route);
            dynamicRouteService.update(loadRouteService.loadRouteDefinition(route));
        }
        if (isClose){
            log.info("成功移除网关负载均衡配置缓存路由！balancedId={}", balancedId);
        }else {
            log.info("成功加载网关负载均衡配置路由缓存！balancedId={}", balancedId);
        }
    }

    /**
     * 重新加载route网关路由并刷新网关
     * @param routeId
     */
    public void refreshRoute(String routeId){
        if (StringUtils.isBlank(routeId)) {
            return ;
        }
        Route route = routeService.findById(routeId);
        // null表示已删除，状态，0是启用，1是禁用
        boolean isClose = false;
        if (route == null) {
            route = new Route();
            route.setId(routeId);
            isClose = true;
        } else if (Constants.NO.equals(route.getStatus())){
            isClose = true;
        }
        if (isClose) {
            dynamicRouteService.delete(routeId);
            //从本地缓存中移除
            RouteCache.remove(routeId);
            RegServerCache.remove(routeId);
            log.info("成功移除网关路由缓存配置！routeId={}", routeId);
        }else {
            dynamicRouteService.update(loadRouteService.loadRouteDefinition(route));
            //记录到本地缓存中
            RouteCache.put(routeId, route);
            List<Map<String, Object>> regClientList = regServerService.getByRouteRegClientList(routeId);
            if (CollectionUtils.isEmpty(regClientList)){
                log.error("未获取注册到网关路由客户端数据库配置！routeId={}", routeId);
            }else {
                setRegRouteClient(regClientList, isClose);
            }
            log.info("成功加载网关路由缓存配置！routeId={}", routeId);
        }
        // 更新负载均衡中关联的网关路由配置；
        refreshBalancedRoute(route, isClose);
    }

    /**
     * 刷新负载均衡下的网关路由
     * @param route
     * @param isClose
     */
    public void refreshBalancedRoute(Route route, boolean isClose){
        String routeId = route.getId();
        //删除
        if (isClose){
            Flux<RouteDefinition> routeDefinitionFlux = dynamicRouteService.getRouteDefinitions();
            Mono<List<RouteDefinition>> routeDefinitionMono = routeDefinitionFlux
                    .filter(r->r.getId().startsWith(RouteConstants.BALANCED))
                    .filter(r->r.getId().endsWith("-" + routeId))
                    .collectList();
            List<RouteDefinition> routeDefinitionList = routeDefinitionMono.block();
            if (CollectionUtils.isEmpty(routeDefinitionList)){
                return;
            }
            List<String> balancedList = new ArrayList<>();
            //网关路由已删除，必需将route从已加载的负载均衡列表中全部删除
            for (RouteDefinition routeDefinition: routeDefinitionList){
                String balancedId = routeDefinition.getId()
                        .replaceAll(RouteConstants.BALANCED, "")
                        .replaceAll(routeId, "")
                        .replaceAll("-", "");
                balancedList.add(balancedId);
                dynamicRouteService.delete(routeDefinition.getId());
            }

            // 重新计算负载均衡下的权重
            for (String balancedId : balancedList) {
                //发布事件，清除分组权重配置
                //publisherWeightRemoveEvent(balancedId);
                //重新加载负载均衡网关路由配置，会对权重重新进行计算（已在gateway中存在的负载均衡路由，理论上在数据库里的状态是启用）
                refreshBalanced(balancedId);
            }
            return;
        }
        //启用
        List<LoadServer> loadServerList = loadServerService.queryByRouteId(routeId);
        if (CollectionUtils.isEmpty(loadServerList)){
            return ;
        }
        Balanced balanced;
        for (LoadServer loadServer : loadServerList){
            balanced = balancedService.findById(loadServer.getBalancedId());
            // 状态，0是启用，1是禁用
            if (balanced == null || Constants.NO.equals(balanced.getStatus())){
                continue;
            }
            //向负载列表中增加网关路由
            loadServerService.setBalancedRoute(balanced, loadServer, route);
            dynamicRouteService.add(loadRouteService.loadRouteDefinition(route));
        }
    }

    /**
     * 重新设置注册到route网关缓存客户端ID，有些网关路由开启了注册客户端过滤（鉴权）
     * @param regServerId
     */
    public void refreshRegServer(Long regServerId){
        if (regServerId == null || regServerId <= 0){
            return ;
        }
        RegServer regServer = regServerService.findById(regServerId);
        Assert.notNull(regServer, getExceptionMessage("未获取注册网关路由客户端服务ID数据库配置！regServerId=", String.valueOf(regServerId)));
        OpenApp client = clientService.findById(regServer.getClientId());
        Assert.notNull(client, getExceptionMessage("未获取注册客户端ID数据库配置！clientId=", regServer.getClientId()));
        //封装配置数据
        List regClientList = new ArrayList<>(1);
        regClientList.add(new Object[]{regServer.getRouteId(), regServer.getClientId(), client.getIp(), regServer.getToken(), regServer.getSecretKey(), regServer.getStatus()});
        // 状态，0是启用，1是禁用
        boolean clientClose = Constants.NO.equals(client.getStatus());
        if (setRegRouteClient(regClientList, clientClose)){
            log.info("成功移除注册到网关路由的客户端服务ID缓存配置！regServerId={}", String.valueOf(regServerId));
        }else {
            log.info("成功加载注册到网关路由客户端服务ID缓存配置！regServerId={}", String.valueOf(regServerId));
        }
    }

    /**
     * 重新设置route网关缓存客户端ID（鉴权）
     * @param clientId
     */
    public void refreshClient(String clientId){
        if (StringUtils.isBlank(clientId)){
            return ;
        }
        OpenApp client = clientService.findById(clientId);
        //null表示已删除
        if (client == null){
            deleteRegRouteClient(clientId);
            log.info("成功移除网关路由客户端ID缓存配置！clientId={}", clientId);
            return ;
        }
        List<Map<String, Object>> regClientList = regServerService.getRegClientList(clientId);
        if (CollectionUtils.isEmpty(regClientList)){
            log.error("未获取注册到网关路由客户端ID数据库配置！clientId={}", clientId);
            return ;
        }
        // 状态，0是启用，1是禁用
        boolean clientClose = Constants.NO.equals(client.getStatus());
        if (setRegRouteClient(regClientList, clientClose)){
            log.info("成功移除网关路由客户端ID缓存配置！clientId={}", clientId);
        }else {
            log.info("成功加载网关路由客户端ID缓存配置！clientId={}", clientId);
        }
    }

    /**
     * 重新设置route网关缓存IP（黑|白名单）
     * @param ip
     */
    public void refreshIp(String ip){
        if (StringUtils.isBlank(ip)) {
            return ;
        }
        SecureIp secureIp = secureIpService.findById(ip);
        // 状态，0是启用，1是禁用
        if (secureIp == null || Constants.NO.equals(secureIp.getStatus())){
            IpListCache.remove(ip);
            log.info("成功移除网关路由IP名单中缓存配置！ip={}", ip);
        }else {
            IpListCache.put(secureIp.getIp(), true);
            log.info("成功加载网关路由IP名单中缓存配置！ip={}", ip);
        }
    }

    /**
     * 重新设置route网关缓存groovyScript规则引擎动态脚本
     * @param groovyScriptId
     */
    public void refreshGroovyScript(Long groovyScriptId){
        if (groovyScriptId == null || groovyScriptId <= 0){
            return ;
        }
        dynamicGroovyService.refresh(groovyScriptId);
        log.info("成功刷新网关路由groovyScript规则引擎动态脚本缓存配置！groovyScriptId={}", groovyScriptId);
    }

    /**
     * 删除缓存中注册客户端ID和客户端IP
     * @param clientId
     */
    private void deleteRegRouteClient(String clientId){
        // 添加或移除注册的客户端
        ConcurrentHashMap<String,List<GatewayRegServer>> cacheMap = RegServerCache.getCacheMap();
        if (CollectionUtils.isEmpty(cacheMap)) {
            return ;
        }
        for (Map.Entry<String, List<GatewayRegServer>> entry : cacheMap.entrySet()) {
            // 移除客户端，返回新集合
            List<GatewayRegServer> newRegServers = entry.getValue().stream().filter(r -> !clientId.equals(r.getClientId())).collect(Collectors.toList());
            entry.setValue(newRegServers);
        }
    }

    /**
     * 设置网关路由中的客户端鉴权信息
     * @param regClientList
     * @param clientClose
     */
    private boolean setRegRouteClient(List regClientList, boolean clientClose){
        String routeId;
        String secretKey;
        String status;
        List<GatewayRegServer> regServers;
        Object[] objects;
        boolean isClose = clientClose;
        Iterator iterator = regClientList.iterator();
        while (iterator.hasNext()){
            objects = (Object[]) iterator.next();
            routeId = String.valueOf(objects[0]);
            String clientId = String.valueOf(objects[1]);
            String ip = String.valueOf(objects[2]);
            String token = String.valueOf(objects[3]);
            secretKey = String.valueOf(objects[4]);
            status = String.valueOf(objects[5]);

            // 如果客户端状态或注册到当前网关路由的访问状态已禁用，则都不可以访问网关路由，需要删除允许通行鉴权配置
            isClose = clientClose || Constants.NO.equals(status);

            // 添加或移除注册的客户端
            regServers = RegServerCache.get(routeId);
            if (CollectionUtils.isEmpty(regServers)){
                regServers = new ArrayList<>();
            }else {
                List<GatewayRegServer> newRegServers = regServers.stream().filter(r -> !clientId.equals(r.getClientId())).collect(Collectors.toList());
                regServers.clear();
                regServers.addAll(newRegServers);
            }
            if (!isClose) {
                GatewayRegServer regServer = new GatewayRegServer();
                regServer.setClientId(clientId);
                regServer.setIp(ip);
                regServer.setToken(token);
                regServer.setSecretKey(secretKey);
                regServers.add(regServer);
            }
            RegServerCache.put(routeId, regServers);
        }
        return isClose;
    }

    /**
     * 抛出异常信息
     * @param message
     * @param id
     */
    private String getExceptionMessage(String message, String id){
        return String.format("%s=%s", message, id);
    }

    /**
     * 发布事件，清除分组权重配置
     * @param balancedId
     */
    private void publisherWeightRemoveEvent(String balancedId){
        applicationEventPublisherFactory.publisherWeightRemoveEvent(loadServerService.setBalancedWeightName(balancedId));
    }

}
