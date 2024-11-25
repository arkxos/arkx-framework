package com.rapidark.cloud.platform.gateway.manage.rest;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.fastjson.JSONObject;
import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.gateway.framework.base.BaseRest;
import com.rapidark.cloud.platform.gateway.framework.bean.*;
import com.rapidark.cloud.platform.gateway.framework.entity.Monitor;
import com.rapidark.cloud.platform.gateway.framework.entity.RouteConfig;
import com.rapidark.cloud.platform.gateway.framework.entity.SentinelRule;
import com.rapidark.cloud.platform.gateway.framework.service.CustomNacosConfigService;
import com.rapidark.cloud.platform.gateway.framework.service.MonitorService;
import com.rapidark.cloud.platform.gateway.framework.service.RouteConfigService;
import com.rapidark.cloud.platform.gateway.framework.service.SentinelRuleService;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import com.rapidark.cloud.platform.gateway.framework.util.RouteConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description 路由管理
 * @Author JL
 * @Date 2020/05/14
 * @Version V1.0
 */
@Slf4j
@RestController
@RequestMapping("/route")
public class RouteConfigRest extends BaseRest {

    @Resource
    private RouteConfigService routeConfigService;

    @Resource
    private MonitorService monitorService;

    @Resource
    private SentinelRuleService sentinelRuleService;

    @Resource
    private CustomNacosConfigService customNacosConfigService;

    /**
     * 添加网关路由
     * @param routeReq
     * @return
     */
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public ResponseResult add(@RequestBody RouteReq routeReq){
        Assert.notNull(routeReq, "未获取到对象");
        RouteDataBean routeDataBean = toRoute(routeReq);
        RouteConfig routeConfig = routeDataBean.getRouteConfig();
        routeConfig.setCreateTime(new Date());
        this.validate(routeConfig);
        RouteConfig dbRouteConfig = new RouteConfig();
        dbRouteConfig.setId(routeConfig.getId());
        long count = routeConfigService.count(dbRouteConfig);
        Assert.isTrue(count <= 0, "RouteId已存在，不能重复");
        return routeConfigService.saveForm(routeDataBean, true);
    }

    /**
     * 删除网关路由
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult delete(@RequestParam String id){
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        routeConfigService.delete(id);
        customNacosConfigService.publishRouteNacosConfig(id);
        return ResponseResult.ok();
    }

    /**
     * 更新网关路由
     * @param routeReq
     * @return
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public ResponseResult update(@RequestBody RouteReq routeReq){
        Assert.notNull(routeReq, "未获取到对象");
        RouteDataBean routeDataBean = toRoute(routeReq);
        RouteConfig routeConfig = routeDataBean.getRouteConfig();
        this.validate(routeConfig);
        Assert.isTrue(StringUtils.isNotBlank(routeConfig.getId()), "未获取到对象ID");
        return routeConfigService.saveForm(routeDataBean, false);
    }

    @RequestMapping(value = "/findById", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult findById(@RequestParam String id){
        Assert.notNull(id, "未获取到对象ID");
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        return ResponseResult.ok(routeConfigService.findById(id));
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult list(@RequestBody RouteReq routeReq){
        Assert.notNull(routeReq, "未获取到对象");
        RouteDataBean routeDataBean = toRoute(routeReq);
        RouteConfig routeConfig = routeDataBean.getRouteConfig();
        return ResponseResult.ok(routeConfigService.list(routeConfig));
    }

    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult pageList(@RequestBody RouteReq routeReq){
        Assert.notNull(routeReq, "未获取到对象");
        int currentPage = getCurrentPage(routeReq.getCurrentPage());
        int pageSize = getPageSize(routeReq.getPageSize());
        RouteDataBean routeDataBean = toRoute(routeReq);
        RouteConfig routeConfig = routeDataBean.getRouteConfig();
        if (StringUtils.isBlank(routeConfig.getName())){
            routeConfig.setName(null);
        }
        if (StringUtils.isBlank(routeConfig.getStatus())){
            routeConfig.setStatus(null);
        }
        return ResponseResult.ok(routeConfigService.pageList(routeConfig,currentPage, pageSize));
    }

    /**
     * 启用网关路由服务
     * @param id
     * @return
     */
    @RequestMapping(value = "/start", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult start(@RequestParam String id){
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        RouteConfig dbRouteConfig = routeConfigService.findById(id);
        if (!Constants.YES.equals(dbRouteConfig.getStatus())) {
            dbRouteConfig.setStatus(Constants.YES);
            routeConfigService.update(dbRouteConfig);
        }
        //可以通过反复启用，刷新路由，防止发布失败或配置变更未生效
        customNacosConfigService.publishRouteNacosConfig(id);
        return ResponseResult.ok();
    }

    /**
     * 停止网关路由服务
     * @param id
     * @return
     */
    @RequestMapping(value = "/stop", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult stop(@RequestParam String id){
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        RouteConfig dbRouteConfig = routeConfigService.findById(id);
        if (!Constants.NO.equals(dbRouteConfig.getStatus())) {
            dbRouteConfig.setStatus(Constants.NO);
            routeConfigService.update(dbRouteConfig);
            customNacosConfigService.publishRouteNacosConfig(id);
        }
        return ResponseResult.ok();
    }

    /**
     * 保存网关路由服务
     * @param routeConfig
     * @param routeReq
     * @param isNews
     * @return
     */
    @Deprecated
    private ResponseResult saveForm(RouteConfig routeConfig, RouteReq routeReq, boolean isNews){
        Monitor monitor = toMonitor(routeReq);
        routeConfig.setUpdateTime(new Date());
        routeConfigService.save(routeConfig);
        customNacosConfigService.publishRouteNacosConfig(routeConfig.getId());

        SentinelRule sentinelRule = toSentinelRule(routeReq);
        if (sentinelRule != null){
            sentinelRule.setId(routeConfig.getId());
            sentinelRuleService.save(sentinelRule);
        } else {
            sentinelRuleService.deleteById(routeConfig.getId());
        }

        //保存监控配置
        if (monitor != null) {
            monitor.setId(routeConfig.getId());
            monitor.setUpdateTime(new Date());
            this.validate(monitor);
            monitorService.save(monitor);
        } else {
            if (!isNews) {
                Monitor dbMonitor = monitorService.findById(routeConfig.getId());
                //修改时，如果前端取消选中，并且数据库中又存在记录，则需要置为禁用状态(用于下一次恢复无需再次输入)
                if (dbMonitor != null){
                    dbMonitor.setStatus(Constants.NO);
                    dbMonitor.setUpdateTime(new Date());
                    monitorService.update(dbMonitor);
                }
            }
        }
        return ResponseResult.ok();
    }


    /**
     * 将请求对象转换为数据库实体对象
     * @param routeReq  前端对象
     * @return Route
     */
    private RouteDataBean toRoute(RouteReq routeReq){
        RouteDataBean routeData = new RouteDataBean();

        RouteFormBean form = routeReq.getForm();
        if (form == null){
            return routeData;
        }

        RouteConfig routeConfig = new RouteConfig();
        BeanUtils.copyProperties(form, routeConfig);

        RouteFilterBean filter = routeReq.getFilter();
        RouteAccessBean access = routeReq.getAccess();
        MonitorBean routeMonitor = routeReq.getMonitor();
        FlowRuleBean flowRule = routeReq.getFlowRule();
        DegradeRuleBean degradeRule = routeReq.getDegradeRule();
        CacheResultBean cacheResult = routeReq.getCacheResult();
        SentinelRule sentinelRule = null;
        Monitor monitor = null;

        //添加过滤器
        if (filter != null) {
            List<String> routeFilterList = new ArrayList<>();
            if (filter.getIdChecked()) {
                routeFilterList.add(RouteConstants.ID);
            }
            if (filter.getIpChecked()) {
                routeFilterList.add(RouteConstants.IP);
            }
            if (filter.getTokenChecked()) {
                routeFilterList.add(RouteConstants.TOKEN);
            }
            routeConfig.setFilterGatewayName(StringUtils.join(routeFilterList.toArray(), Constants.SEPARATOR_SIGN));
        }

        //添加鉴权器
        if (access != null) {
            List<String> routeAccessList = new ArrayList<>();
            if (access.getHeaderChecked()) {
                routeAccessList.add(RouteConstants.Access.HEADER);
            }
            if (access.getIpChecked()) {
                routeAccessList.add(RouteConstants.Access.IP);
            }
            if (access.getParameterChecked()) {
                routeAccessList.add(RouteConstants.Access.PARAMETER);
            }
            if (access.getTimeChecked()) {
                routeAccessList.add(RouteConstants.Access.TIME);
            }
            if (access.getCookieChecked()) {
                routeAccessList.add(RouteConstants.Access.COOKIE);
            }
            routeConfig.setFilterAuthorizeName(StringUtils.join(routeAccessList.toArray(), Constants.SEPARATOR_SIGN));
        }

        //添加监控
        if (routeMonitor != null){
            // checked为true，则表示启用监控配置
            if (routeMonitor.getChecked()){
                monitor = form.getMonitor();
                monitor.setId(routeConfig.getId());
                monitor.setStatus(Constants.YES);
                monitor.setUpdateTime(new Date());
                this.validate(monitor);
            }
        }

        // Sentinel限流
        if (flowRule != null){
            //直接拒绝（默认模式）
            if (flowRule.getDefaultChecked()){
                routeConfig.setFlowRuleName(RouteConstants.Sentinel.DEFAULT);
            }
            //冷启动模式
            else if (flowRule.getWarmUpChecked()){
                routeConfig.setFlowRuleName(RouteConstants.Sentinel.WARM_UP);
            }
            //均速模式
            else if (flowRule.getRateLimiterChecked()){
                routeConfig.setFlowRuleName(RouteConstants.Sentinel.RATE_LIMITER);
            } else {
                routeConfig.setFlowRuleName(null);
            }
            if (StringUtils.isNotBlank(routeConfig.getFlowRuleName()) && form.getFlowRule() != null){
                sentinelRule = new SentinelRule();
                sentinelRule.setFlowRule(JSONObject.toJSONString(form.getFlowRule()));
            }
        }

        // Sentinel熔断
        if (degradeRule != null){
            if (degradeRule.getChecked()){
                routeConfig.setDegradeRuleName(RouteConstants.Sentinel.DEFAULT);
            } else {
                routeConfig.setDegradeRuleName(null);
            }
            if (StringUtils.isNotBlank(routeConfig.getDegradeRuleName()) && form.getDegradeRule() != null) {
                sentinelRule = sentinelRule == null ? new SentinelRule() : sentinelRule;
                sentinelRule.setDegradeRule(JSONObject.toJSONString(form.getDegradeRule()));
            }
        }

        if (cacheResult == null || Boolean.FALSE.equals(cacheResult.getChecked())) {
            routeConfig.setCacheTtl(null);
        }

        routeData.setRouteConfig(routeConfig);
        routeData.setMonitor(monitor);
        if (sentinelRule != null) {
            sentinelRule.setId(routeConfig.getId());
            routeData.setSentinelRule(sentinelRule);
        }
        return routeData;
    }

    /**
     * 获取监控配置
     * @param routeReq
     * @return
     */
    @Deprecated
    private Monitor toMonitor(RouteReq routeReq){
        MonitorBean bean = routeReq.getMonitor();
        if (bean != null){
            // checked为true，则表示启用监控配置
            if (bean.getChecked()){
                RouteFormBean form = routeReq.getForm();
                Monitor monitor = new Monitor();
                BeanUtils.copyProperties(form.getMonitor(), monitor);
                monitor.setStatus(Constants.YES);
                return monitor;
            }
        }
        return null;
    }

    /**
     * 获取监控配置
     * @param routeReq
     * @return
     */
    @Deprecated
    private SentinelRule toSentinelRule(RouteReq routeReq){
        SentinelRule sentinelRule = null;
        FlowRuleBean flowRuleBean = routeReq.getFlowRule();
        DegradeRuleBean degradeRuleBean = routeReq.getDegradeRule();
        RouteFormBean form = routeReq.getForm();
        // 限流
        if (flowRuleBean != null){
            FlowRule flowRule = null;
            sentinelRule = new SentinelRule();
            //直接拒绝（默认模式）
            if (flowRuleBean.getDefaultChecked()){
                flowRule = form.getFlowRule();
                flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
            }
            //冷启动模式
            if (flowRuleBean.getWarmUpChecked()){
                flowRule = form.getFlowRule();
                flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP);
            }
            //均速模式
            if (flowRuleBean.getRateLimiterChecked()){
                flowRule = form.getFlowRule();
                flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
            }
            if (flowRule != null){
                sentinelRule.setFlowRule(JSONObject.toJSONString(flowRule));
            }
        }
        // 熔断
        if (degradeRuleBean != null && degradeRuleBean.getChecked()){
            sentinelRule = sentinelRule == null ? new SentinelRule() : sentinelRule;
            sentinelRule.setDegradeRule(JSONObject.toJSONString(form.getDegradeRule()));
        }
        return sentinelRule;
    }
}
