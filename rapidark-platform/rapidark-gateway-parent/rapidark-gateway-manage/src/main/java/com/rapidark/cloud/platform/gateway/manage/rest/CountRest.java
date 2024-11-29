package com.rapidark.cloud.platform.gateway.manage.rest;

import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.gateway.framework.base.BaseRest;
import com.rapidark.cloud.platform.gateway.framework.entity.GatewayAppRoute;
import com.rapidark.cloud.platform.gateway.framework.service.CountService;
import com.rapidark.cloud.platform.gateway.framework.bean.CountReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * @Description
 * @Author JL
 * @Date 2020/07/07
 * @Version V1.0
 */
@RestController
@RequestMapping("/count")
public class CountRest extends BaseRest {

    @Resource
    private CountService countService;

    /**
     * 负载类型的请求（需要在缓存filed前加一个balanced前缀，用于区分普通路由ID）
     * @param countReq
     * @return
     */
    @RequestMapping(value = "/balanced/request", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult balancedRequest(@RequestBody CountReq countReq) {
        return countService.count(countReq, true);
    }

    /**
     * 请求
     * @param countReq
     * @return
     */
    @RequestMapping(value = "/request", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult request(@RequestBody CountReq countReq) {
        return countService.count(countReq, false);
    }

    /**
     * 查询路由集合统计结果
     * @param countReq
     * @return
     */
    @RequestMapping(value = "/route/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult routePageList(@RequestBody CountReq countReq) {
        Assert.notNull(countReq, "未获取到对象");
        int currentPage = getCurrentPage(countReq.getCurrentPage());
        int pageSize = getPageSize(countReq.getPageSize());
        GatewayAppRoute gatewayAppRoute = new GatewayAppRoute();
        if (StringUtils.isNotBlank(countReq.getName())) {
            gatewayAppRoute.setName(countReq.getName());
        }
        if (StringUtils.isNotBlank(countReq.getGroupCode())){
            gatewayAppRoute.setGroupCode(countReq.getGroupCode());
        }
        return countService.countRouteList(gatewayAppRoute, currentPage, pageSize);
    }

    /**
     * 统计按7天和按24小时维度计算的请求总量
     * @return
     */
    @RequestMapping(value = "/request/total", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult routeTotal() {
        return ResponseResult.ok(countService.countRequestTotal());
    }

    /**
     * 统计按7天和按24小时维度计算的请求总量
     * @return
     */
    @RequestMapping(value = "/request/app/total", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult routeAppTotal(@RequestParam(required=false) String id) {
        return ResponseResult.ok(countService.countRequestTotal(id));
    }

    /**
     * 流量
     * @param routeIds
     * @return
     */
    @RequestMapping(value = "/flux", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseResult flux(@RequestParam String [] routeIds ) {
        Assert.isTrue(routeIds != null, "未获取到对象ID");
        return ResponseResult.ok();
    }

}
