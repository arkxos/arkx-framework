package com.rapidark.cloud.platform.gateway.framework.service;

//import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
//import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
//import com.alibaba.fastjson.JSONObject;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.cloud.platform.gateway.framework.bean.GatewayAppRouteDataBean;
import com.rapidark.cloud.platform.gateway.framework.bean.GatewayAppRouteRsp;
import com.rapidark.cloud.platform.gateway.framework.repository.MonitorRepository;
import com.rapidark.cloud.platform.gateway.framework.repository.GatewayAppRouteRepository;
import com.rapidark.cloud.platform.gateway.framework.repository.SentinelRuleRepository;
import com.rapidark.cloud.platform.gateway.framework.entity.Monitor;
import com.rapidark.cloud.platform.gateway.framework.entity.ClientServerRegister;
import com.rapidark.cloud.platform.gateway.framework.entity.GatewayAppRoute;
import com.rapidark.cloud.platform.gateway.framework.entity.SentinelRule;
import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import com.rapidark.framework.common.utils.PageResult;
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
public class GatewayAppRouteService extends BaseService<GatewayAppRoute,String, GatewayAppRouteRepository> {

    @Resource
    private ClientServerRegisterService clientServerRegisterService;

    @Resource
    private SentinelRuleService sentinelRuleService;

    @Resource
    private CustomNacosConfigService customNacosConfigService;

    @Resource
    private MonitorRepository monitorRepository;

    @Resource
    private SentinelRuleRepository sentinelRuleRepository;

    @Resource
    private GatewayAppRouteRepository gatewayAppRouteRepository;

    @Resource
    private GroovyScriptService groovyScriptService;

    /**
     * 删除网关路由以及已注册的客户端（关联表）
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void delete(String id){
        GatewayAppRoute gatewayAppRoute = this.findById(id);
        if (gatewayAppRoute != null) {
            ClientServerRegister clientServerRegister = new ClientServerRegister();
            clientServerRegister.setRouteId(id);
            List<ClientServerRegister> clientServerRegisterList = clientServerRegisterService.findAll(clientServerRegister);
            //删除服务列表
            if (clientServerRegisterList != null && clientServerRegisterList.size()>0) {
                clientServerRegisterService.deleteInBatch(clientServerRegisterList);
            }
            //删除监控配置
            monitorRepository.findById(id).ifPresent(monitor -> monitorRepository.delete(monitor));
            //删除路由对象
            this.delete(gatewayAppRoute);
        }
    }

    /**
     * 获取需要监控的网关路由服务
     * @return
     */
    public List<GatewayAppRoute> monitorRouteList(){
        return gatewayAppRouteRepository.monitorRouteList();
    }

    /**
     * 分页查询
     * @param gatewayAppRoute
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<GatewayAppRoute> pageList(GatewayAppRoute gatewayAppRoute, int currentPage, int pageSize){
        //构造条件查询方式
        ExampleMatcher matcher = ExampleMatcher.matching();
        if (StringUtils.isNotBlank(gatewayAppRoute.getName())) {
            //支持模糊条件查询
            matcher = matcher.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        }
        PageResult<GatewayAppRoute> result = this.pageList(gatewayAppRoute, matcher, currentPage, pageSize);
        List<GatewayAppRoute> gatewayAppRouteList = result.getLists();
        if (CollectionUtils.isEmpty(gatewayAppRouteList)){
            return result;
        }
        //获取所有监控配置
        List<Monitor> monitorList = monitorRepository.findAll();
        //将监控配置重新封装到数据集合中
        Map<String,Monitor> monitorMap =
                CollectionUtils.isEmpty(monitorList) ?
                new HashMap<>() :
                monitorList.stream().collect(Collectors.toMap(Monitor::getId, m -> m));

        List<SentinelRule> ruleList = sentinelRuleRepository.findAll();
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

        List<GatewayAppRoute> gatewayAppRouteRspList = gatewayAppRouteList.stream().map(r ->{
            ruleMap.get(r.getId());
            GatewayAppRouteRsp gatewayAppRouteRsp = new GatewayAppRouteRsp();
            BeanUtils.copyProperties(r, gatewayAppRouteRsp);
            gatewayAppRouteRsp.setMonitor(monitorMap.get(r.getId()));
            gatewayAppRouteRsp.setUseScript(routeScriptMap.get(r.getId()));
            SentinelRule sentinelRule = ruleMap.get(r.getId());
            if (sentinelRule != null){
                if (StringUtils.isNotBlank(sentinelRule.getFlowRule())){
//                    routeRsp.setFlowRule(JSONObject.parseObject(sentinelRule.getFlowRule(), FlowRule.class));
                }
                if (StringUtils.isNotBlank(sentinelRule.getDegradeRule())){
//                    routeRsp.setDegradeRule(JSONObject.parseObject(sentinelRule.getDegradeRule(), DegradeRule.class));
                }
            }
            return gatewayAppRouteRsp;
        }).collect(Collectors.toList());
        result.setLists(gatewayAppRouteRspList);
        return result;
    }

    /**
     * 保存网关路由服务
     * @param gatewayAppRouteDataBean
     * @param isNews
     * @return
     */
    public ResponseResult saveForm(GatewayAppRouteDataBean gatewayAppRouteDataBean, boolean isNews){
        GatewayAppRoute gatewayAppRoute = save(gatewayAppRouteDataBean, isNews);
        //推送变更事件到nacos注册与配置中心
        customNacosConfigService.publishRouteNacosConfig(gatewayAppRoute.getId());
        return ResponseResult.ok();
    }

    /**
     * 保存网关路由服务
     * @param gatewayAppRouteDataBean
     * @param isNews
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public GatewayAppRoute save(GatewayAppRouteDataBean gatewayAppRouteDataBean, boolean isNews){
        GatewayAppRoute gatewayAppRoute = gatewayAppRouteDataBean.getGatewayAppRoute();
        //清理已缓存的数据
        gatewayAppRoute.setUpdateTime(new Date());
        super.save(gatewayAppRoute);
        //保存监控配置
        saveMonitor(gatewayAppRoute.getId(), gatewayAppRouteDataBean.getMonitor(), isNews);
        //保存限流配置
        saveSetinelRule(gatewayAppRoute.getId(), gatewayAppRouteDataBean.getSentinelRule());
        return gatewayAppRoute;
    }

    /**
     * 保存监控配置
     * @param monitor
     */
    private void saveMonitor(String routeId, Monitor monitor, boolean isNews){
        if (monitor != null) {
            monitorRepository.save(monitor);
        } else {
            if (!isNews) {
                Optional<Monitor> optional = monitorRepository.findById(routeId);
                //修改时，如果前端取消选中，并且数据库中又存在记录，则需要置为禁用状态(用于下一次恢复无需再次输入)
                if (optional.isPresent()){
                    Monitor dbMonitor = optional.get();
                    dbMonitor.setStatus(Constants.NO);
                    dbMonitor.setUpdateTime(new Date());
                    monitorRepository.save(dbMonitor);
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
