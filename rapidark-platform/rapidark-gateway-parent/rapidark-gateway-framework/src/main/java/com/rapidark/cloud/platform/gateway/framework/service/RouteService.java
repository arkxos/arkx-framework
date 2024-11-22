package com.rapidark.cloud.platform.gateway.framework.service;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.fastjson.JSONObject;
import com.rapidark.cloud.platform.gateway.framework.base.BaseService;
import com.rapidark.cloud.platform.gateway.framework.bean.RouteDataBean;
import com.rapidark.cloud.platform.gateway.framework.bean.RouteRsp;
import com.rapidark.cloud.platform.gateway.framework.dao.MonitorDao;
import com.rapidark.cloud.platform.gateway.framework.dao.RouteDao;
import com.rapidark.cloud.platform.gateway.framework.dao.SentinelRuleDao;
import com.rapidark.cloud.platform.gateway.framework.entity.Monitor;
import com.rapidark.cloud.platform.gateway.framework.entity.RegServer;
import com.rapidark.cloud.platform.gateway.framework.entity.Route;
import com.rapidark.cloud.platform.gateway.framework.entity.SentinelRule;
import com.rapidark.cloud.platform.gateway.framework.util.ApiResult;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import com.rapidark.cloud.platform.gateway.framework.util.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 路由管理业务类
 * @Author JL
 * @Date 2022/11/19
 * @Version V2.0
 */
@Service
public class RouteService extends BaseService<Route,String,RouteDao> {

    @Resource
    private RegServerService regServerService;

    @Resource
    private SentinelRuleService sentinelRuleService;

    @Resource
    private CustomNacosConfigService customNacosConfigService;

    @Resource
    private MonitorDao monitorDao;

    @Resource
    private SentinelRuleDao sentinelRuleDao;

    @Resource
    private RouteDao routeDao;

    @Resource
    private GroovyScriptService groovyScriptService;

    /**
     * 删除网关路由以及已注册的客户端（关联表）
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void delete(String id){
        Route route = this.findById(id);
        if (route != null) {
            RegServer regServer = new RegServer();
            regServer.setRouteId(id);
            List<RegServer> regServerList = regServerService.findAll(regServer);
            //删除服务列表
            if (regServerList != null && regServerList.size()>0) {
                regServerService.deleteInBatch(regServerList);
            }
            //删除监控配置
            monitorDao.findById(id).ifPresent(monitor -> monitorDao.delete(monitor));
            //删除路由对象
            this.delete(route);
        }
    }

    /**
     * 获取需要监控的网关路由服务
     * @return
     */
    public List<Route> monitorRouteList(){
        return routeDao.monitorRouteList();
    }

    /**
     * 分页查询
     * @param route
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<Route> pageList(Route route, int currentPage, int pageSize){
        //构造条件查询方式
        ExampleMatcher matcher = ExampleMatcher.matching();
        if (StringUtils.isNotBlank(route.getName())) {
            //支持模糊条件查询
            matcher = matcher.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        }
        PageResult<Route> result = this.pageList(route, matcher, currentPage, pageSize);
        List<Route> routeList = result.getLists();
        if (CollectionUtils.isEmpty(routeList)){
            return result;
        }
        //获取所有监控配置
        List<Monitor> monitorList = monitorDao.findAll();
        //将监控配置重新封装到数据集合中
        Map<String,Monitor> monitorMap =
                CollectionUtils.isEmpty(monitorList) ?
                new HashMap<>() :
                monitorList.stream().collect(Collectors.toMap(Monitor::getId, m -> m));

        List<SentinelRule> ruleList = sentinelRuleDao.findAll();
        Map<String, SentinelRule> ruleMap =
                CollectionUtils.isEmpty(ruleList) ?
                new HashMap<>() :
                ruleList.stream().collect(Collectors.toMap(SentinelRule::getId, s->s));

        List<Map> routeScriptList = groovyScriptService.findRouteScriptNum();
        Map<String,Integer> routeScriptMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(routeScriptList)) {
            routeScriptList.forEach(map-> routeScriptMap.put((String) map.get("routeId"), ((Long) map.get("num")).intValue()));
        }

        if (monitorMap.size() == 0 && ruleMap.size() == 0 && routeScriptMap.size() == 0){
            return result;
        }

        List<Route> routeRspList = routeList.stream().map(r ->{
            ruleMap.get(r.getId());
            RouteRsp routeRsp = new RouteRsp();
            BeanUtils.copyProperties(r, routeRsp);
            routeRsp.setMonitor(monitorMap.get(r.getId()));
            routeRsp.setUseScript(routeScriptMap.get(r.getId()));
            SentinelRule sentinelRule = ruleMap.get(r.getId());
            if (sentinelRule != null){
                if (StringUtils.isNotBlank(sentinelRule.getFlowRule())){
                    routeRsp.setFlowRule(JSONObject.parseObject(sentinelRule.getFlowRule(), FlowRule.class));
                }
                if (StringUtils.isNotBlank(sentinelRule.getDegradeRule())){
                    routeRsp.setDegradeRule(JSONObject.parseObject(sentinelRule.getDegradeRule(), DegradeRule.class));
                }
            }
            return routeRsp;
        }).collect(Collectors.toList());
        result.setLists(routeRspList);
        return result;
    }

    /**
     * 保存网关路由服务
     * @param routeDataBean
     * @param isNews
     * @return
     */
    public ApiResult saveForm(RouteDataBean routeDataBean, boolean isNews){
        Route route = save(routeDataBean, isNews);
        //推送变更事件到nacos注册与配置中心
        customNacosConfigService.publishRouteNacosConfig(route.getId());
        return new ApiResult();
    }

    /**
     * 保存网关路由服务
     * @param routeDataBean
     * @param isNews
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Route save(RouteDataBean routeDataBean, boolean isNews){
        Route route = routeDataBean.getRoute();
        //清理已缓存的数据
        route.setUpdateTime(new Date());
        super.save(route);
        //保存监控配置
        saveMonitor(route.getId(), routeDataBean.getMonitor(), isNews);
        //保存限流配置
        saveSetinelRule(route.getId(), routeDataBean.getSentinelRule());
        return route;
    }

    /**
     * 保存监控配置
     * @param monitor
     */
    private void saveMonitor(String routeId, Monitor monitor, boolean isNews){
        if (monitor != null) {
            monitorDao.save(monitor);
        } else {
            if (!isNews) {
                Optional<Monitor> optional = monitorDao.findById(routeId);
                //修改时，如果前端取消选中，并且数据库中又存在记录，则需要置为禁用状态(用于下一次恢复无需再次输入)
                if (optional.isPresent()){
                    Monitor dbMonitor = optional.get();
                    dbMonitor.setStatus(Constants.NO);
                    dbMonitor.setUpdateTime(new Date());
                    monitorDao.save(dbMonitor);
                }
            }
        }
    }

    /**
     * 保存限流配置
     * @param sentinelRule
     */
    private void saveSetinelRule(String routeId, SentinelRule sentinelRule){
        if (sentinelRule != null){
            sentinelRule.setId(routeId);
            sentinelRuleService.save(sentinelRule);
        } else {
            sentinelRuleService.deleteById(routeId);
        }
    }

}
