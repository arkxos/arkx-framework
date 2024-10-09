package com.rapidark.cloud.gateway.manage.rest;

import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.gateway.formwork.entity.GatewayAppRoute;
import com.rapidark.common.model.ResultBody;
import com.rapidark.common.security.http.OpenRestTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import com.rapidark.cloud.gateway.formwork.base.BaseRest;
import com.rapidark.cloud.gateway.formwork.bean.*;
import com.rapidark.cloud.gateway.formwork.entity.Monitor;
import com.rapidark.cloud.gateway.formwork.service.CustomNacosConfigService;
import com.rapidark.cloud.gateway.formwork.service.MonitorService;
import com.rapidark.cloud.gateway.formwork.service.GatewayAppRouteService;
import com.rapidark.common.utils.Constants;
import com.rapidark.cloud.gateway.formwork.util.RouteConstants;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description 路由管理
 * @Author JL
 * @Date 2020/05/14
 * @Version V1.0
 */
@Api(tags = "网关路由")
@Slf4j
@RestController
@RequestMapping("/gatewayAppRoute")
public class GatewayAppRouteRest extends BaseRest {

    @Resource
    private GatewayAppRouteService gatewayAppRouteService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private MonitorService monitorService;

    @Resource
    private CustomNacosConfigService customNacosConfigService;

    @Autowired
    private OpenRestTemplate openRestTemplate;
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody list(@RequestBody RouteReq routeReq){
        Assert.notNull(routeReq, "未获取到对象");
        return ResultBody.ok().data(gatewayAppRouteService.list(toRoute(routeReq)));
    }

    /**
     * 获取分页路由列表
     *
     * @return
     */
    @ApiOperation(value = "获取分页路由列表", notes = "获取分页路由列表")
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody pageList(@RequestBody RouteReq routeReq){
        Assert.notNull(routeReq, "未获取到对象");
        int currentPage = getCurrentPage(routeReq.getCurrentPage());
        int pageSize = getPageSize(routeReq.getPageSize());
        GatewayAppRoute gatewayAppRoute = toRoute(routeReq);
        if (StringUtils.isBlank(gatewayAppRoute.getName())){
            gatewayAppRoute.setName(null);
        }
        if (StringUtils.isBlank(gatewayAppRoute.getStatus())){
            gatewayAppRoute.setStatus(null);
        }
        return ResultBody.ok().data(gatewayAppRouteService.pageList(gatewayAppRoute, currentPage, pageSize));
    }

    /**
     * 获取路由
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取路由", notes = "获取路由")
    @RequestMapping(value = "/findById", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody findById(@RequestParam String id){
        Assert.notNull(id, "未获取到对象ID");
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        return ResultBody.ok().data(gatewayAppRouteService.findById(id));
    }

    /**
     * 添加网关路由
     * @param routeReq
     * @return
     */
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public ResultBody add(@RequestBody RouteReq routeReq){
        Assert.notNull(routeReq, "未获取到对象");
        GatewayAppRoute gatewayAppRoute = toRoute(routeReq);
        gatewayAppRoute.setCreateTime(new Date());
        this.validate(gatewayAppRoute);
        GatewayAppRoute dbGatewayAppRoute = new GatewayAppRoute();
        dbGatewayAppRoute.setId(gatewayAppRoute.getId());
        long count = gatewayAppRouteService.count(dbGatewayAppRoute);
        Assert.isTrue(count <= 0, "RouteId已存在，不能重复");
        return this.save(gatewayAppRoute, toMonitor(routeReq), true);
    }

    /**
     * 删除网关路由
     * @param id
     * @return
     */
    @ApiOperation(value = "移除路由", notes = "移除路由")
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody delete(@RequestParam String id){
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        gatewayAppRouteService.delete(id);

        //this.setRouteCacheVersion();
        customNacosConfigService.publishRouteNacosConfig(id);

        // 刷新网关
        openRestTemplate.refreshGateway();

        return ResultBody.ok();
    }

    /**
     * 更新网关路由
     * @param routeReq
     * @return
     */
    @ApiOperation(value = "编辑路由", notes = "编辑路由")
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public ResultBody update(@RequestBody RouteReq routeReq){
        Assert.notNull(routeReq, "未获取到对象");
        GatewayAppRoute gatewayAppRoute = toRoute(routeReq);
        this.validate(gatewayAppRoute);
        Assert.isTrue(StringUtils.isNotBlank(gatewayAppRoute.getId()), "未获取到对象ID");
        return this.save(gatewayAppRoute, toMonitor(routeReq), false);
    }

    /**
     * 启用网关路由服务
     * @param id
     * @return
     */
    @RequestMapping(value = "/start", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody start(@RequestParam String id){
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        GatewayAppRoute dbGatewayAppRoute = gatewayAppRouteService.findById(id);
        if (!Constants.YES.equals(dbGatewayAppRoute.getStatus())) {
            dbGatewayAppRoute.setStatus(Constants.YES);
            gatewayAppRouteService.update(dbGatewayAppRoute);
        }
        //this.setRouteCacheVersion();
        //可以通过反复启用，刷新路由，防止发布失败或配置变更未生效
        customNacosConfigService.publishRouteNacosConfig(id);
        return ResultBody.ok();
    }

    /**
     * 停止网关路由服务
     * @param id
     * @return
     */
    @RequestMapping(value = "/stop", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody stop(@RequestParam String id){
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        GatewayAppRoute dbGatewayAppRoute = gatewayAppRouteService.findById(id);
        if (!Constants.NO.equals(dbGatewayAppRoute.getStatus())) {
            dbGatewayAppRoute.setStatus(Constants.NO);
            gatewayAppRouteService.update(dbGatewayAppRoute);
            //this.setRouteCacheVersion();
            customNacosConfigService.publishRouteNacosConfig(id);
        }
        return ResultBody.ok();
    }

    /**
     * 保存网关路由服务
     * @param gatewayAppRoute
     * @param monitor
     * @param isNews
     * @return
     */
    private ResultBody save(GatewayAppRoute gatewayAppRoute, Monitor monitor, boolean isNews){
        gatewayAppRoute.setUpdateTime(new Date());

        switch (gatewayAppRoute.getType()) {
            case BaseConstants.ROUTE_TYPE_URL:
                gatewayAppRoute.setServiceId(null);
                gatewayAppRoute.setUri(gatewayAppRoute.getUri().trim());
                break;
            default:
                gatewayAppRoute.setServiceId(gatewayAppRoute.getServiceId().trim());
                gatewayAppRoute.setUri(null);
        }

        gatewayAppRouteService.save(gatewayAppRoute);

        //this.setRouteCacheVersion();
        customNacosConfigService.publishRouteNacosConfig(gatewayAppRoute.getId());

        //保存监控配置
        if (monitor != null) {
            monitor.setId(gatewayAppRoute.getId());
            monitor.setUpdateTime(new Date());
            this.validate(monitor);
            monitorService.save(monitor);
        } else {
            if (!isNews) {
                Monitor dbMonitor = monitorService.findById(gatewayAppRoute.getId());
                //修改时，如果前端取消选中，并且数据库中又存在记录，则需要置为禁用状态
                if (dbMonitor != null){
                    dbMonitor.setStatus(Constants.NO);
                    dbMonitor.setUpdateTime(new Date());
                    monitorService.update(dbMonitor);
                }
            }
        }

        // 刷新网关
        openRestTemplate.refreshGateway();

        return ResultBody.ok();
    }

    /**
     * 将请求对象转换为数据库实体对象
     * @param routeReq  前端对象
     * @return GatewayAppRoute
     */
    private GatewayAppRoute toRoute(RouteReq routeReq){
        GatewayAppRoute gatewayAppRoute = new GatewayAppRoute();
        GatewayAppRouteFormBean form = routeReq.getForm();
        if (form == null){
            return gatewayAppRoute;
        }
        BeanUtils.copyProperties(form, gatewayAppRoute);
        RouteFilterBean filter = routeReq.getFilter();
        RouteHystrixBean hystrix = routeReq.getHystrix();
        RouteLimiterBean limiter = routeReq.getLimiter();
        RouteAccessBean access = routeReq.getAccess();
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
            gatewayAppRoute.setFilterGatewaName(StringUtils.join(routeFilterList.toArray(), Constants.SEPARATOR_SIGN));
        }

        //添加熔断器
        if (hystrix != null) {
            if (hystrix.getDefaultChecked()) {
                gatewayAppRoute.setFilterHystrixName(RouteConstants.Hystrix.DEFAULT);
            } else if (hystrix.getCustomChecked()) {
                gatewayAppRoute.setFilterHystrixName(RouteConstants.Hystrix.CUSTOM);
            }
        }
        //添加限流器
        if (limiter != null) {
            gatewayAppRoute.setFilterRateLimiterName(null);
            if (limiter.getIdChecked()) {
                gatewayAppRoute.setFilterRateLimiterName(RouteConstants.REQUEST_ID);
            }else if (limiter.getIpChecked()) {
                gatewayAppRoute.setFilterRateLimiterName(RouteConstants.IP);
            }else if (limiter.getUriChecked()) {
                gatewayAppRoute.setFilterRateLimiterName(RouteConstants.URI);
            }
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
            gatewayAppRoute.setFilterAuthorizeName(StringUtils.join(routeAccessList.toArray(), Constants.SEPARATOR_SIGN));
        }
        return gatewayAppRoute;
    }

    /**
     * 获取监控配置
     * @param routeReq
     * @return
     */
    private Monitor toMonitor(RouteReq routeReq){
        MonitorBean bean = routeReq.getMonitor();
        if (bean != null){
            // checked为true，则表示启用监控配置
            if (bean.getChecked()){
                GatewayAppRouteFormBean form = routeReq.getForm();
                Monitor monitor = new Monitor();
                BeanUtils.copyProperties(form.getMonitor(), monitor);
                monitor.setStatus(Constants.YES);
                return monitor;
            }
        }
        return null;
    }

    /**
     * 对路由数据进行变更后，设置redis中缓存的版本号
     */
    @Deprecated
    private void setRouteCacheVersion(){
        redisTemplate.opsForHash().put(RouteConstants.SYNC_VERSION_KEY, RouteConstants.ROUTE, String.valueOf(System.currentTimeMillis()));
    }

}
