package com.rapidark.cloud.platform.gateway.manage.rest;

//import com.alibaba.csp.sentinel.slots.block.RuleConstant;
//import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.gateway.framework.base.BaseRest;
import com.rapidark.cloud.platform.gateway.framework.bean.*;
import com.rapidark.cloud.platform.gateway.framework.entity.Monitor;
import com.rapidark.cloud.platform.gateway.framework.entity.GatewayAppRoute;
import com.rapidark.cloud.platform.gateway.framework.entity.SentinelRule;
import com.rapidark.cloud.platform.gateway.framework.service.CustomNacosConfigService;
import com.rapidark.cloud.platform.gateway.framework.service.MonitorService;
import com.rapidark.cloud.platform.gateway.framework.service.GatewayAppRouteService;
import com.rapidark.cloud.platform.gateway.framework.service.SentinelRuleService;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import com.rapidark.cloud.platform.gateway.framework.util.RouteConstants;
import com.rapidark.cloud.platform.gateway.manage.rest.cmd.StringIdCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
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
@Schema(title = "网关路由")
@Slf4j
@RestController
@RequestMapping("/route")
public class GatewayAppRouteRest extends BaseRest {

    @Resource
    private GatewayAppRouteService gatewayAppRouteService;

	@Resource
	private RedisTemplate redisTemplate;

    @Resource
    private MonitorService monitorService;

    @Resource
    private SentinelRuleService sentinelRuleService;

    @Resource
    private CustomNacosConfigService customNacosConfigService;

//	@Autowired
//	private OpenRestTemplate openRestTemplate;

    /**
     * 添加网关路由
     * @param gatewayAppRouteReq
     * @return
     */
    @PostMapping(value = "/add")
    public ResponseResult add(@RequestBody GatewayAppRouteReq gatewayAppRouteReq){
        Assert.notNull(gatewayAppRouteReq, "未获取到对象");
        GatewayAppRouteDataBean gatewayAppRouteDataBean = toRoute(gatewayAppRouteReq);
        GatewayAppRoute gatewayAppRoute = gatewayAppRouteDataBean.getGatewayAppRoute();
        gatewayAppRoute.setCreateTime(new Date());
        this.validate(gatewayAppRoute);
        GatewayAppRoute dbGatewayAppRoute = new GatewayAppRoute();
        dbGatewayAppRoute.setId(gatewayAppRoute.getId());
        long count = gatewayAppRouteService.count(dbGatewayAppRoute);
        Assert.isTrue(count <= 0, "RouteId已存在，不能重复");
        return gatewayAppRouteService.saveForm(gatewayAppRouteDataBean, true);
		// return this.save(gatewayAppRoute, toMonitor(routeReq), true);
    }

    /**
     * 删除网关路由
     * @param command
     * @return
     */
    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestBody StringIdCommand command){
		String id = command.getId();
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        gatewayAppRouteService.delete(id);
        customNacosConfigService.publishRouteNacosConfig(id);

		// 刷新网关
//		// openRestTemplate.refreshGateway();

        return ResponseResult.ok();
    }

    /**
     * 更新网关路由
     * @param gatewayAppRouteReq
     * @return
     */
	@Schema(title = "编辑路由", name = "编辑路由")
    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody GatewayAppRouteReq gatewayAppRouteReq){
        Assert.notNull(gatewayAppRouteReq, "未获取到对象");
        GatewayAppRouteDataBean gatewayAppRouteDataBean = toRoute(gatewayAppRouteReq);
        GatewayAppRoute gatewayAppRoute = gatewayAppRouteDataBean.getGatewayAppRoute();
        this.validate(gatewayAppRoute);
        Assert.isTrue(StringUtils.isNotBlank(gatewayAppRoute.getId()), "未获取到对象ID");
        return gatewayAppRouteService.saveForm(gatewayAppRouteDataBean, false);
		//  return this.save(gatewayAppRoute, toMonitor(routeReq), false);
    }

    @RequestMapping(value = "/findById", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult findById(@RequestParam String id){
        Assert.notNull(id, "未获取到对象ID");
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        return ResponseResult.ok(gatewayAppRouteService.findById(id));
    }

    @PostMapping(value = "/list")
    public ResponseResult list(@RequestBody GatewayAppRouteReq gatewayAppRouteReq){
        Assert.notNull(gatewayAppRouteReq, "未获取到对象");
        GatewayAppRouteDataBean gatewayAppRouteDataBean = toRoute(gatewayAppRouteReq);
        GatewayAppRoute gatewayAppRoute = gatewayAppRouteDataBean.getGatewayAppRoute();
        return ResponseResult.ok(gatewayAppRouteService.list(gatewayAppRoute));
    }

    @PostMapping(value = "/pageList")
    public ResponseResult pageList(@RequestBody GatewayAppRouteReq gatewayAppRouteReq, Pageable pageable){
        Assert.notNull(gatewayAppRouteReq, "未获取到对象");
        int currentPage = getCurrentPage(gatewayAppRouteReq.getCurrentPage());
        int pageSize = getPageSize(gatewayAppRouteReq.getPageSize());
        GatewayAppRouteDataBean gatewayAppRouteDataBean = toRoute(gatewayAppRouteReq);
        GatewayAppRoute gatewayAppRoute = gatewayAppRouteDataBean.getGatewayAppRoute();
        if (StringUtils.isBlank(gatewayAppRoute.getName())){
            gatewayAppRoute.setName(null);
        }
        if (StringUtils.isBlank(gatewayAppRoute.getStatus())){
            gatewayAppRoute.setStatus(null);
        }
        return ResponseResult.ok(gatewayAppRouteService.pageList(gatewayAppRoute,currentPage, pageSize));
    }

    /**
     * 启用网关路由服务
     * @param command
     * @return
     */
    @PostMapping(value = "/start")
    public ResponseResult start(@RequestBody StringIdCommand command){
		String id = command.getId();
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        GatewayAppRoute dbGatewayAppRoute = gatewayAppRouteService.findById(id);
        if (!Constants.YES.equals(dbGatewayAppRoute.getStatus())) {
            dbGatewayAppRoute.setStatus(Constants.YES);
            gatewayAppRouteService.update(dbGatewayAppRoute);
        }
        //可以通过反复启用，刷新路由，防止发布失败或配置变更未生效
        customNacosConfigService.publishRouteNacosConfig(id);
        return ResponseResult.ok();
    }

    /**
     * 停止网关路由服务
     * @param command
     * @return
     */
    @PostMapping(value = "/stop")
    public ResponseResult stop(@RequestBody StringIdCommand command){
		String id = command.getId();
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        GatewayAppRoute dbGatewayAppRoute = gatewayAppRouteService.findById(id);
        if (!Constants.NO.equals(dbGatewayAppRoute.getStatus())) {
            dbGatewayAppRoute.setStatus(Constants.NO);
            gatewayAppRouteService.update(dbGatewayAppRoute);
            customNacosConfigService.publishRouteNacosConfig(id);
        }
        return ResponseResult.ok();
    }

    /**
     * 保存网关路由服务
     * @param gatewayAppRoute
     * @param gatewayAppRouteReq
     * @param isNews
     * @return
     */
    @Deprecated
    private ResponseResult saveForm(GatewayAppRoute gatewayAppRoute, GatewayAppRouteReq gatewayAppRouteReq, boolean isNews){
        Monitor monitor = toMonitor(gatewayAppRouteReq);

		gatewayAppRoute.setUpdateTime(new Date());
		switch (gatewayAppRoute.getType()) {
			case BaseConstants.ROUTE_TYPE_URL:
//                gatewayAppRoute.setServiceId(null);
				gatewayAppRoute.setUri(gatewayAppRoute.getUri().trim());
				break;
			default:
//                gatewayAppRoute.setServiceId(gatewayAppRoute.getServiceId());
				gatewayAppRoute.setUri(null);
		}

        gatewayAppRouteService.save(gatewayAppRoute);

		//this.setRouteCacheVersion();
        customNacosConfigService.publishRouteNacosConfig(gatewayAppRoute.getId());

//        SentinelRule sentinelRule = toSentinelRule(routeReq);
//        if (sentinelRule != null){
//            sentinelRule.setId(routeConfig.getId());
//            sentinelRuleService.save(sentinelRule);
//        } else {
//            sentinelRuleService.deleteById(routeConfig.getId());
//        }

        //保存监控配置
        if (monitor != null) {
            monitor.setId(gatewayAppRoute.getId());
            monitor.setUpdateTime(new Date());
            this.validate(monitor);
            monitorService.save(monitor);
        } else {
            if (!isNews) {
                Monitor dbMonitor = monitorService.findById(gatewayAppRoute.getId());
                //修改时，如果前端取消选中，并且数据库中又存在记录，则需要置为禁用状态(用于下一次恢复无需再次输入)
                if (dbMonitor != null){
                    dbMonitor.setStatus(Constants.NO);
                    dbMonitor.setUpdateTime(new Date());
                    monitorService.update(dbMonitor);
                }
            }
        }

		// 刷新网关
//		// openRestTemplate.refreshGateway();

        return ResponseResult.ok();
    }


    /**
     * 将请求对象转换为数据库实体对象
     * @param gatewayAppRouteReq  前端对象
     * @return Route
     */
    private GatewayAppRouteDataBean toRoute(GatewayAppRouteReq gatewayAppRouteReq){
        GatewayAppRouteDataBean routeData = new GatewayAppRouteDataBean();

        RouteFormBean form = gatewayAppRouteReq.getForm();
        if (form == null){
            return routeData;
        }

        GatewayAppRoute gatewayAppRoute = new GatewayAppRoute();
        BeanUtils.copyProperties(form, gatewayAppRoute);

        GatewayAppRouteFilterBean filter = gatewayAppRouteReq.getFilter();
        GatewayAppRouteAccessBean access = gatewayAppRouteReq.getAccess();
		RouteLimiterBean limiter = gatewayAppRouteReq.getLimiter();
        MonitorBean routeMonitor = gatewayAppRouteReq.getMonitor();
        FlowRuleBean flowRule = gatewayAppRouteReq.getFlowRule();
        DegradeRuleBean degradeRule = gatewayAppRouteReq.getDegradeRule();
        CacheResultBean cacheResult = gatewayAppRouteReq.getCacheResult();
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
            gatewayAppRoute.setFilterGatewayName(StringUtils.join(routeFilterList.toArray(), Constants.SEPARATOR_SIGN));
        }

		//添加熔断器
//		if (hystrix != null) {
//			if (hystrix.getDefaultChecked()) {
//				gatewayAppRoute.setFilterHystrixName(RouteConstants.Hystrix.DEFAULT);
//			} else if (hystrix.getCustomChecked()) {
//				gatewayAppRoute.setFilterHystrixName(RouteConstants.Hystrix.CUSTOM);
//			}
//		}
		//添加限流器
//		if (limiter != null) {
//			gatewayAppRoute.setFilterRateLimiterName(null);
//			if (limiter.getIdChecked()) {
//				gatewayAppRoute.setFilterRateLimiterName(RouteConstants.REQUEST_ID);
//			}else if (limiter.getIpChecked()) {
//				gatewayAppRoute.setFilterRateLimiterName(RouteConstants.IP);
//			}else if (limiter.getUriChecked()) {
//				gatewayAppRoute.setFilterRateLimiterName(RouteConstants.URI);
//			}
//		}

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
            gatewayAppRoute.setFilterAuthorizeName(StringUtils.join(routeAccessList.toArray(), Constants.SEPARATOR_SIGN));
        }

        //添加监控
        if (routeMonitor != null){
            // checked为true，则表示启用监控配置
            if (routeMonitor.getChecked()){
                monitor = form.getMonitor();
                monitor.setId(gatewayAppRoute.getId());
                monitor.setStatus(Constants.YES);
                monitor.setUpdateTime(new Date());
                this.validate(monitor);
            }
        }

        // Sentinel限流
        if (flowRule != null){
            //直接拒绝（默认模式）
            if (flowRule.getDefaultChecked()){
                gatewayAppRoute.setFlowRuleName(RouteConstants.Sentinel.DEFAULT);
            }
            //冷启动模式
            else if (flowRule.getWarmUpChecked()){
                gatewayAppRoute.setFlowRuleName(RouteConstants.Sentinel.WARM_UP);
            }
            //均速模式
            else if (flowRule.getRateLimiterChecked()){
                gatewayAppRoute.setFlowRuleName(RouteConstants.Sentinel.RATE_LIMITER);
            } else {
                gatewayAppRoute.setFlowRuleName(null);
            }
//            if (StringUtils.isNotBlank(routeConfig.getFlowRuleName()) && form.getFlowRule() != null){
//                sentinelRule = new SentinelRule();
//                sentinelRule.setFlowRule(JSONObject.toJSONString(form.getFlowRule()));
//            }
        }

        // Sentinel熔断
//        if (degradeRule != null){
//            if (degradeRule.getChecked()){
//                routeConfig.setDegradeRuleName(RouteConstants.Sentinel.DEFAULT);
//            } else {
//                routeConfig.setDegradeRuleName(null);
//            }
//            if (StringUtils.isNotBlank(routeConfig.getDegradeRuleName()) && form.getDegradeRule() != null) {
//                sentinelRule = sentinelRule == null ? new SentinelRule() : sentinelRule;
//                sentinelRule.setDegradeRule(JSONObject.toJSONString(form.getDegradeRule()));
//            }
//        }

        if (cacheResult == null || Boolean.FALSE.equals(cacheResult.getChecked())) {
            gatewayAppRoute.setCacheTtl(null);
        }

        routeData.setGatewayAppRoute(gatewayAppRoute);
        routeData.setMonitor(monitor);
        if (sentinelRule != null) {
            sentinelRule.setId(gatewayAppRoute.getId());
            routeData.setSentinelRule(sentinelRule);
        }
        return routeData;
    }

    /**
     * 获取监控配置
     * @param gatewayAppRouteReq
     * @return
     */
    @Deprecated
    private Monitor toMonitor(GatewayAppRouteReq gatewayAppRouteReq){
        MonitorBean bean = gatewayAppRouteReq.getMonitor();
        if (bean != null){
            // checked为true，则表示启用监控配置
            if (bean.getChecked()){
                RouteFormBean form = gatewayAppRouteReq.getForm();
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
//    @Deprecated
//    private SentinelRule toSentinelRule(RouteReq routeReq){
//        SentinelRule sentinelRule = null;
//        FlowRuleBean flowRuleBean = routeReq.getFlowRule();
//        DegradeRuleBean degradeRuleBean = routeReq.getDegradeRule();
//        RouteFormBean form = routeReq.getForm();
//        // 限流
//        if (flowRuleBean != null){
//            FlowRule flowRule = null;
//            sentinelRule = new SentinelRule();
//            //直接拒绝（默认模式）
//            if (flowRuleBean.getDefaultChecked()){
//                flowRule = form.getFlowRule();
//                flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
//            }
//            //冷启动模式
//            if (flowRuleBean.getWarmUpChecked()){
//                flowRule = form.getFlowRule();
//                flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP);
//            }
//            //均速模式
//            if (flowRuleBean.getRateLimiterChecked()){
//                flowRule = form.getFlowRule();
//                flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
//            }
//            if (flowRule != null){
//                sentinelRule.setFlowRule(JSONObject.toJSONString(flowRule));
//            }
//        }
//        // 熔断
//        if (degradeRuleBean != null && degradeRuleBean.getChecked()){
//            sentinelRule = sentinelRule == null ? new SentinelRule() : sentinelRule;
//            sentinelRule.setDegradeRule(JSONObject.toJSONString(form.getDegradeRule()));
//        }
//        return sentinelRule;
//    }

	/**
	 * 对路由数据进行变更后，设置redis中缓存的版本号
	 */
	@Deprecated
	private void setRouteCacheVersion(){
		redisTemplate.opsForHash().put(RouteConstants.SYNC_VERSION_KEY, RouteConstants.ROUTE, String.valueOf(System.currentTimeMillis()));
	}

}
