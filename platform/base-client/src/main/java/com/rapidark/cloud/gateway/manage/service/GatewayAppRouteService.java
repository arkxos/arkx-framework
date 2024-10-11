package com.rapidark.cloud.gateway.manage.service;

import com.rapidark.cloud.gateway.formwork.bean.GatewayAppRouteRsp;
import com.rapidark.cloud.gateway.formwork.entity.ClientServerRegister;
import com.rapidark.cloud.gateway.formwork.entity.GatewayAppRoute;
import com.rapidark.cloud.gateway.manage.repository.MonitorRepository;
import com.rapidark.common.utils.PageData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.rapidark.cloud.gateway.formwork.base.BaseService;
import com.rapidark.cloud.gateway.manage.repository.GatewayAppRouteRepository;
import com.rapidark.cloud.gateway.formwork.entity.Monitor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description 路由管理业务类
 * @Author jianglong
 * @Date 2020/05/14
 * @Version V1.0
 */
@Service
public class GatewayAppRouteService extends BaseService<GatewayAppRoute,String, GatewayAppRouteRepository> {

    @Resource
    private ClientServerRegisterService clientServerRegisterService;

    @Resource
    private MonitorRepository monitorRepository;

    @Resource
    private GatewayAppRouteRepository gatewayAppRouteRepository;

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
            Optional<Monitor> monitorOptional = monitorRepository.findById(id);
            if (monitorOptional.isPresent()){
                monitorRepository.deleteById(id);
            }
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
    public PageData<GatewayAppRoute> pageList(GatewayAppRoute gatewayAppRoute, int currentPage, int pageSize){
        //构造条件查询方式
        ExampleMatcher matcher = ExampleMatcher.matching();
        if (StringUtils.isNotBlank(gatewayAppRoute.getName())) {
            //支持模糊条件查询
            matcher = matcher.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        }
        PageData<GatewayAppRoute> result = this.pageList(gatewayAppRoute, matcher, currentPage, pageSize);
        List<GatewayAppRoute> gatewayAppRouteList = result.getContent();
        if (CollectionUtils.isEmpty(gatewayAppRouteList)){
            return result;
        }
        //获取所有监控配置
        List<Monitor> monitorList = monitorRepository.findAll();
        if (CollectionUtils.isEmpty(monitorList)){
            return result;
        }
        //将监控配置重新封装到数据集合中
        Map<String,Monitor> monitorMap = monitorList.stream().collect(Collectors.toMap(Monitor::getId, m -> m));
        List<GatewayAppRoute> gatewayAppRouteRspList = new ArrayList<>(gatewayAppRouteList.size());
        for (GatewayAppRoute gatewayAppRoute1 : gatewayAppRouteList){
            GatewayAppRouteRsp routeRsp = new GatewayAppRouteRsp();
            BeanUtils.copyProperties(gatewayAppRoute1, routeRsp);
            Monitor monitor = monitorMap.get(gatewayAppRoute1.getId());
            if (monitor != null){
                routeRsp.setMonitor(monitor);
            }
            gatewayAppRouteRspList.add(routeRsp);
        }
        result.setContent(gatewayAppRouteRspList);
        return result;
    }
}
