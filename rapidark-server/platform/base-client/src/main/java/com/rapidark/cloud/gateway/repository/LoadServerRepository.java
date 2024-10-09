package com.rapidark.cloud.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rapidark.cloud.gateway.formwork.entity.LoadServer;

import java.util.List;
import java.util.Map;

/**
 * @Description 负载服务数据层操作接口
 * @Author jianglong
 * @Date 2020/06/28
 * @Version V1.0
 */
public interface LoadServerRepository extends JpaRepository<LoadServer, Long> {

    /**
     * 删除负载下所有的路由服务
     * @param balancedId
     */
    void deleteAllByBalancedId(String balancedId);

    /**
     * 查询指定负载下所有路由服务
     * @param balancedId
     * @return
     */
    List<LoadServer> queryByBalancedId(String balancedId);

    /**
     * 查询指定路由关联的负载服务
     * @param routeId
     * @return
     */
    List<LoadServer> queryByRouteId(String routeId);

    @Query(value = "SELECT r.name,r.group_Code,r.uri,r.path,r.method,r.status,l.id,l.route_Id,l.weight FROM gateway_app_route r " +
            "INNER JOIN loadserver l ON r.id=l.route_Id WHERE l.balanced_Id=?1",
            nativeQuery = true)
    List<Map> queryLoadServerList(String balancedId);


}
