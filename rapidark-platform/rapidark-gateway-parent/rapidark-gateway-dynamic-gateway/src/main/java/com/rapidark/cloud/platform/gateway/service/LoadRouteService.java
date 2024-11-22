package com.rapidark.cloud.platform.gateway.service;

import com.rapidark.cloud.platform.gateway.framework.entity.Route;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import com.rapidark.cloud.platform.gateway.framework.util.RouteConstants;
import com.rapidark.cloud.platform.gateway.filter.CacheResultGatewayFilter;
import com.rapidark.cloud.platform.gateway.filter.ClientIdGatewayFilter;
import com.rapidark.cloud.platform.gateway.filter.IpGatewayFilter;
import com.rapidark.cloud.platform.gateway.filter.TokenGatewayFilter;
import com.rapidark.cloud.platform.gateway.vo.GatewayFilterDefinition;
import com.rapidark.cloud.platform.gateway.vo.GatewayPredicateDefinition;
import com.rapidark.cloud.platform.gateway.vo.GatewayRouteConfig;
import com.rapidark.cloud.platform.gateway.vo.GatewayRouteDefinition;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.Resource;
import java.net.URI;
import java.util.*;

/**
 * @Description 将数据转换为Gateway网关需要数据格式，并返回服务路由对象
 * @Author JL
 * @Date 2022/12/10
 * @Version V1.0
 */
@Service
public class LoadRouteService {

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 把传递进来的参数转换成路由对象
     * @param gwdefinition
     * @return
     */
    public RouteDefinition assembleRouteDefinition(GatewayRouteDefinition gwdefinition) {
        RouteDefinition definition = new RouteDefinition();
        definition.setId(gwdefinition.getId());
        definition.setOrder(gwdefinition.getOrder());
        //设置断言
        List<PredicateDefinition> pdList=new ArrayList<>();
        List<GatewayPredicateDefinition> gatewayPredicateDefinitionList=gwdefinition.getPredicates();
        for (GatewayPredicateDefinition gpDefinition: gatewayPredicateDefinitionList) {
            PredicateDefinition predicate = new PredicateDefinition();
            predicate.setArgs(gpDefinition.getArgs());
            predicate.setName(gpDefinition.getName());
            pdList.add(predicate);
        }
        definition.setPredicates(pdList);
        //设置过滤器
        List<FilterDefinition> filters = new ArrayList();
        List<GatewayFilterDefinition> gatewayFilters = gwdefinition.getFilters();
        for(GatewayFilterDefinition filterDefinition : gatewayFilters){
            FilterDefinition filter = new FilterDefinition();
            filter.setName(filterDefinition.getName());
            filter.setArgs(filterDefinition.getArgs());
            filters.add(filter);
        }
        definition.setFilters(filters);
        definition.setUri(this.getURI(gwdefinition.getUri()));
        return definition;
    }

    /**
     * 封装网关路由参数，返回RouteDefinition
     * @param r
     * @return
     */
    public RouteDefinition loadRouteDefinition(Route r){
        RouteDefinition definition = new RouteDefinition();
        definition.setId(r.getId());
        definition.setOrder(0);
        List<PredicateDefinition> predicates = new ArrayList<>();
        definition.setPredicates(predicates);
        List<FilterDefinition> filters = new ArrayList<>();
        definition.setFilters(filters);
        definition.setUri(this.getURI(r.getUri()));
        //权重（注意此处，当权重值小于等于0，则表示无流量流入，不创建网关）
        if (r.getWeight() != null && r.getWeight() > 0) {
            predicates.add(setPredicateDefinition(RouteConstants.WEIGHT, r.getWeightName(), String.valueOf(r.getWeight())));
        }
        //断言路径
        if (StringUtils.isNotBlank(r.getPath())) {
            predicates.add(setPredicateDefinition(RouteConstants.PATH, r.getPath()));
        }
        //请求模式
        if (StringUtils.isNotBlank(r.getMethod())) {
            predicates.add(setPredicateDefinition(RouteConstants.METHOD, r.getMethod()));
        }
        //断言主机
        if (StringUtils.isNotBlank(r.getHost())) {
            String[] parameters = r.getHost().split(Constants.SEPARATOR_SIGN);
            predicates.add(setPredicateDefinition(RouteConstants.HOST, parameters));
        }
        //断言远程地址
        if (StringUtils.isNotBlank(r.getRemoteAddr())) {
            String[] parameters = r.getRemoteAddr().split(Constants.SEPARATOR_SIGN);
            predicates.add(setPredicateDefinition(RouteConstants.REMOTE_ADDR, parameters));
        }
        //断言Header
        if (StringUtils.isNotBlank(r.getHeader())) {
            String[] parameters = r.getHeader().split(Constants.SEPARATOR_SIGN);
            predicates.add(setPredicateDefinition(RouteConstants.HEADER, parameters));
        }
        //URL截取方式
        if (r.getStripPrefix() != null && r.getStripPrefix() > 0) {
            filters.add(setFilterDefinition(RouteConstants.STRIP_PREFIX, String.valueOf(r.getStripPrefix())));
        }
        //请求参数
        if (StringUtils.isNotBlank(r.getRequestParameter())) {
            String[] parameters = r.getRequestParameter().split(Constants.SEPARATOR_SIGN);
            filters.add(setFilterDefinition(RouteConstants.ADD_REQUEST_PARAMETER, parameters));
        }
        //重写Path路径
        if (StringUtils.isNotBlank(r.getRewritePath())) {
            String[] parameters = r.getRewritePath().split(Constants.SEPARATOR_SIGN);
            filters.add(setFilterDefinition(RouteConstants.REWRITE_PATH, parameters));
        }
        //鉴权
        if (StringUtils.isNotBlank(r.getFilterAuthorizeName())){
            filters.add(setFilterDefinition(RouteConstants.AUTHORIZE, RouteConstants.TRUE));
        }
        //过滤器,id,ip,token
        if (StringUtils.isNotBlank(r.getFilterGatewayName())) {
            String names = r.getFilterGatewayName();
            if (names.contains(RouteConstants.IP)) {
                filters.add(setFilterDefinition(RouteConstants.Secure.SECURE_IP, RouteConstants.TRUE));
            }
            if (names.contains(RouteConstants.ID)) {
                filters.add(setFilterDefinition(RouteConstants.Secure.SECURE_CLIENT_ID, RouteConstants.TRUE));
            }
            if (names.contains(RouteConstants.TOKEN)) {
                filters.add(setFilterDefinition(RouteConstants.Secure.SECURE_TOKEN, RouteConstants.TRUE));
            }
        }
        //缓存策略
        if (r.getCacheTtl() != null && r.getCacheTtl() > 0) {
            Map<String, String> args = new LinkedHashMap<>();
            args.put(RouteConstants.TTL, String.valueOf(r.getCacheTtl()));
            filters.add(setFilterDefinition(RouteConstants.Secure.CACHE_RESULT, args));
        }
        return definition;
    }

    /**
     * 封装Predicate属性值，返回PredicateDefinition对象
     * @param name
     * @param values
     * @return
     */
    private PredicateDefinition setPredicateDefinition(String name, String ... values){
        PredicateDefinition predicate = new PredicateDefinition();
        Map<String, String> args = new HashMap<String, String>();
        predicate.setName(name);
        int i=0;
        for (String value : values){
            args.put(RouteConstants._GENKEY_ + i, value);
            i++;
        }
        predicate.setArgs(args);
        return predicate;
    }

    /**
     * 封装Filter属性值，返回FilterDefinition对象
     * @param name
     * @param args
     * @return
     */
    private FilterDefinition setFilterDefinition(String name, Map<String,String> args){
        FilterDefinition filter = new FilterDefinition();
        filter.setName(name);
        filter.setArgs(args);
        return filter;
    }

    /**
     * 封装Filter属性值，返回FilterDefinition对象
     * @param name
     * @param values
     * @return
     */
    private FilterDefinition setFilterDefinition(String name, String ... values){
        Map<String, String> args = new LinkedHashMap<>();
        int i=0;
        for (String value : values){
            args.put(RouteConstants._GENKEY_ + i, value);
            i++;
        }
        return setFilterDefinition(name, args);
    }

    /**
     * 封状URI
     * @param uri
     * @return
     */
    private URI getURI(String uri){
        if(uri.startsWith(Constants.HTTP)){
            return UriComponentsBuilder.fromHttpUrl(uri).build().toUri();
        }else{
            // uri为lb://consumer-service
            return URI.create(uri);
        }
    }

    /**
     * 封装网关路由对象，用于系统执行config初始化加载时使用(已弃用)
     * @param r     自定义服务路由对象
     * @return
     */
    @Deprecated
    public GatewayRouteConfig loadRouteConfig(Route r){
        GatewayRouteConfig config = new GatewayRouteConfig();
        config.setId(r.getId());
        config.setOrder(0);
        config.setUri(r.getUri());
        //断言路径
        if (StringUtils.isNotBlank(r.getPath())) {
            config.setPath(r.getPath());
        }
        //请求模式
        if (StringUtils.isNotBlank(r.getMethod())) {
            config.setMethod(r.getMethod());
        }
        //断言主机
        if (StringUtils.isNotBlank(r.getHost())) {
            config.setHost(r.getHost());
        }
        //断言远程地址
        if (StringUtils.isNotBlank(r.getRemoteAddr())) {
            config.setRemoteAddr(r.getRemoteAddr());
        }
        //断言截取
        if (r.getStripPrefix() != null && r.getStripPrefix() > 0) {
            config.setStripPrefix(r.getStripPrefix());
        }
        //请求参数
        if (StringUtils.isNotBlank(r.getRequestParameter())) {
            String[] parameters = r.getRequestParameter().split(Constants.SEPARATOR_SIGN);
            config.setRequestParameterName(parameters[0]);
            config.setRequestParameterValue(parameters[1]);
        }
        //重写Path路径
        if (StringUtils.isNotBlank(r.getRewritePath())) {
            String[] parameters = r.getRewritePath().split(Constants.SEPARATOR_SIGN);
            config.setRewritePathName(parameters[0]);
            config.setRewritePathValue(parameters[1]);
        }
        //过滤器,id,ip,token
        if (StringUtils.isNotBlank(r.getFilterGatewayName())) {
            List<GatewayFilter> filters = new ArrayList<>();
            String names = r.getFilterGatewayName();
            if (names.contains(RouteConstants.IP)) {
                filters.add(new IpGatewayFilter(true));
            }
            if (names.contains(RouteConstants.ID)) {
                filters.add(new ClientIdGatewayFilter(true));
            }
            if (names.contains(RouteConstants.TOKEN)) {
                filters.add(new TokenGatewayFilter(true));
            }
            config.setGatewayFilter(filters.toArray(new GatewayFilter[]{}));
        }
        //缓存过滤器
        if (r.getCacheTtl() > 0) {
            GatewayFilter[] filters = config.getGatewayFilter();
            filters = ObjectUtils.isEmpty(filters)?new GatewayFilter[]{}:filters;
            filters[filters.length] = new CacheResultGatewayFilter(r.getCacheTtl(), redisTemplate);
            config.setGatewayFilter(filters);
        }
        //鉴权
        if (StringUtils.isNotBlank(r.getFilterAuthorizeName())){
            config.setAuthorize(true);
        }
        return config;
    }


}
