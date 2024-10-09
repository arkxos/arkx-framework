package com.rapidark.cloud.gateway.formwork.service;

import com.rapidark.cloud.gateway.formwork.bean.GatewayAppRouteRsp;
import com.rapidark.cloud.gateway.formwork.entity.GatewayAppRoute;
import com.rapidark.cloud.gateway.formwork.repository.MonitorRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.rapidark.cloud.gateway.formwork.base.BaseService;
import com.rapidark.cloud.gateway.formwork.bean.MonitorReq;
import com.rapidark.cloud.gateway.formwork.entity.Monitor;
import com.rapidark.common.utils.Constants;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description 告警监控业务类
 * @Author JL
 * @Date 2021/04/14
 * @Version V1.0
 */
@Service
public class MonitorService extends BaseService<Monitor, String, MonitorRepository> {

    @Resource
    private GatewayAppRouteService gatewayAppRouteService;
    @Resource
    private MonitorRepository monitorRepository;

    /**
     * 获取监控的服务列表
     * @param monitorReq
     * @return
     */
    public List<GatewayAppRoute> list(MonitorReq monitorReq){
        GatewayAppRoute queryGatewayAppRoute = new GatewayAppRoute();
        if (monitorReq != null && monitorReq.getStatus() != null) {
            //如果前端搜索状态为2告警类型，则直查询路由网关状态的为0的记录
            //网关路由服务，只有0正常，1禁用，两种状态
            //网关路由服务监控，有0正常，1禁用，2告警三种状态
            if (Constants.ALARM.equals(monitorReq.getStatus())){
                queryGatewayAppRoute.setStatus(Constants.YES);
            }else {
                queryGatewayAppRoute.setStatus(monitorReq.getStatus());
            }
        }
        List<Monitor> monitorList = this.validMonitorList();
        //没有监控数据
        if (CollectionUtils.isEmpty(monitorList)){
            return null;
        }
        Map<String, Monitor> monitorMap = monitorList.stream().collect(Collectors.toMap(Monitor::getId, r -> r));
        List<GatewayAppRoute> resultList = new ArrayList<>(monitorMap.size());
        List<GatewayAppRoute> gatewayAppRouteList = gatewayAppRouteService.list(queryGatewayAppRoute);
        for (GatewayAppRoute gatewayAppRoute : gatewayAppRouteList){
            Monitor monitor = monitorMap.get(gatewayAppRoute.getId());
            if (monitor == null){
                continue;
            }
            //如果监控状态值不等于0和1，其它状态值则表示存在告警
            if (StringUtils.equalsAny(monitor.getStatus(), Constants.YES, Constants.NO)){
            }else {
                //如果前端搜索状态为：0正常，1禁用 的网关路由服务，则不显示告警状态的网关路由服务
                if (StringUtils.equalsAny(monitorReq.getStatus(), Constants.YES, Constants.NO)){
                    continue;
                }
                gatewayAppRoute.setStatus(monitor.getStatus());
            }
            GatewayAppRouteRsp routeRsp = new GatewayAppRouteRsp();
            BeanUtils.copyProperties(gatewayAppRoute, routeRsp);
            routeRsp.setMonitor(monitor);
            resultList.add(routeRsp);
        }
        return resultList;
    }

    /**
     * 获取监控配置，告警状态：0启用，1禁用，2告警
     * @return
     */
    public List<Monitor> validMonitorList(){
        return monitorRepository.validMonitorList();
    }

    /**
     * 获取0正常状态的网关路由服务监控配置，告警状态：0启用，1禁用，2告警
     * @return
     */
    public List<Monitor> validRouteMonitorList(){
        return monitorRepository.validRouteMonitorList();
    }

}
