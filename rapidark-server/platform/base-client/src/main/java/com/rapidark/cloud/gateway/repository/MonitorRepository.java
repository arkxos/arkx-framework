package com.rapidark.cloud.gateway.repository;

import com.rapidark.cloud.base.server.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rapidark.cloud.gateway.formwork.entity.Monitor;

import java.util.List;

/**
 * @Description 告警监控数据层操作接口
 * @Author JL
 * @Date 2021/04/14
 * @Version V1.0
 */
public interface MonitorRepository extends BaseRepository<Monitor, String> {
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
    @Query(value ="SELECT m FROM Monitor m WHERE m.status IN ('0','2') AND m.id IN (SELECT r.id FROM GatewayAppRoute r WHERE r.status='0')")
    List<Monitor> validRouteMonitorList();
}