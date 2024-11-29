package com.rapidark.cloud.platform.gateway.framework.repository;

import com.rapidark.cloud.platform.gateway.framework.entity.GatewayAppRoute;
import com.rapidark.framework.data.jpa.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Description 网关服务Dao数据层操作接口
 * @Author JL
 * @Date 2020/05/14
 * @Version V1.0
 */
public interface GatewayAppRouteRepository extends BaseRepository<GatewayAppRoute, String> {
    /**
     * 查询开启监控的网关路由服务,条件：网关状态为0正常，监控状态为：0正常或(2告警+0可重试)
     * @return
     */
    @Query(value ="SELECT r FROM GatewayAppRoute r WHERE r.status='0' AND r.id in (select m.id from Monitor m where m.status='0' or (m.status='2' and m.recover='0'))")
    List<GatewayAppRoute> monitorRouteList();
}
