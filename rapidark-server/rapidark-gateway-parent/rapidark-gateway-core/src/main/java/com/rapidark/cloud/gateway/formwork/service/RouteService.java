package com.rapidark.cloud.gateway.formwork.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.rapidark.cloud.gateway.formwork.base.BaseService;
import com.rapidark.cloud.gateway.formwork.bean.RouteReq;
import com.rapidark.cloud.gateway.formwork.bean.RouteRsp;
import com.rapidark.cloud.gateway.formwork.dao.RouteDao;
import com.rapidark.cloud.gateway.formwork.entity.Client;
import com.rapidark.cloud.gateway.formwork.entity.Monitor;
import com.rapidark.cloud.gateway.formwork.entity.RegServer;
import com.rapidark.cloud.gateway.formwork.entity.Route;
import com.rapidark.cloud.gateway.formwork.util.ApiResult;
import com.rapidark.cloud.gateway.formwork.util.PageResult;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description 路由管理业务类
 * @Author jianglong
 * @Date 2020/05/14
 * @Version V1.0
 */
@Service
public class RouteService extends BaseService<Route,String,RouteDao> {

    @Resource
    private RegServerService regServerService;

    @Resource
    private MonitorService monitorService;

    @Resource
    private RouteDao routeDao;

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
            Monitor monitor = monitorService.findById(id);
            if (monitor != null){
                monitorService.deleteById(id);
            }
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
        List<Monitor> monitorList = monitorService.findAll();
        if (CollectionUtils.isEmpty(monitorList)){
            return result;
        }
        //将监控配置重新封装到数据集合中
        Map<String,Monitor> monitorMap = monitorList.stream().collect(Collectors.toMap(Monitor::getId, m -> m));
        List<Route> routeRspList = new ArrayList<>(routeList.size());
        for (Route route1 : routeList){
            RouteRsp routeRsp = new RouteRsp();
            BeanUtils.copyProperties(route1, routeRsp);
            Monitor monitor = monitorMap.get(route1.getId());
            if (monitor != null){
                routeRsp.setMonitor(monitor);
            }
            routeRspList.add(routeRsp);
        }
        result.setLists(routeRspList);
        return result;
    }
}
