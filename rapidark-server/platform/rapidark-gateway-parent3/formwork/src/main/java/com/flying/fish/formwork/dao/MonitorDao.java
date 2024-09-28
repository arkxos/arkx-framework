package com.flying.fish.formwork.dao;

import com.flying.fish.formwork.entity.Monitor;
import com.flying.fish.formwork.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Description 告警监控数据层操作接口
 * @Author JL
 * @Date 2021/04/14
 * @Version V1.0
 */
public interface MonitorDao extends JpaRepository<Monitor, String> {
    /**
     * 获取监控配置，告警状态：0启用，1禁用，2告警
     * @return
     */
    @Query(value ="SELECT m FROM Monitor m WHERE m.status IN ('0','2')")
    List<Monitor> validMonitorList();

    /**
     * 获取0正常状态的网关路由服务监控配置，告警状态：0启用，1禁用，2告警
     * @return
     */
    @Query(value ="SELECT m FROM Monitor m WHERE m.status IN ('0','2') AND m.id IN (SELECT r.id FROM Route r WHERE r.status='0')")
    List<Monitor> validRouteMonitorList();
}