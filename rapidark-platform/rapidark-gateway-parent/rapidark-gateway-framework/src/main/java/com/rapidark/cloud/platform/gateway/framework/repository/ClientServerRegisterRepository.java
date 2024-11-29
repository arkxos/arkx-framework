package com.rapidark.cloud.platform.gateway.framework.repository;

import com.rapidark.cloud.platform.gateway.framework.bean.GatewayAppRouteRegServer;
import com.rapidark.cloud.platform.gateway.framework.entity.Client;
import com.rapidark.cloud.platform.gateway.framework.entity.ClientServerRegister;
import com.rapidark.cloud.platform.gateway.framework.entity.GatewayAppRoute;
import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 注册网关服务数据层操作接口
 * @author darkness
 * @date 2022/5/30 14:35
 * @version 1.0
 */
@Repository
public interface ClientServerRegisterRepository extends BaseRepository<ClientServerRegister, Long> {

    /**
     * 修改客户端下所有已注册网关服务的状态
     * @param clientId
     * @param status
     * @param newStatus
     */
	@Modifying
	@Query(value = "update ClientServerRegister set status=?3 where clientId=?1 and status=?2")
    @Transactional(rollbackFor = {Throwable.class})
    int setClientAllRouteStatus(String clientId, String status, String newStatus);

    /**
     * 修改网关服务下所有已注册客户端的状态
     * @param routeId
     * @param status
     * @param newStatus
     */
    @Query(value = "update ClientServerRegister set status=?3 where routeId=?1 and status=?2")
    @Modifying
    @Transactional(rollbackFor = {Throwable.class})
    int setRouteAllClientStatus(String routeId, String status, String newStatus);

    /**
     * 查询指定网关服务下的注册的,并且是状态为0允许通行的
     * @return
     */
    @Query(value ="SELECT s.routeId,c.id,c.ip,s.token,s.secretKey FROM Client c, ClientServerRegister s, GatewayAppRoute r WHERE c.id = s.clientId AND r.id = s.routeId AND c.status='0' AND s.status='0' AND r.status='0'")
    List allRegClientList();

    /**
     * 查询指定客户端注册的所有网关路由服务
     * @return
     */
    @Query(value ="SELECT s.routeId,c.id,c.ip,s.token,s.secretKey,s.status FROM Client c, ClientServerRegister s WHERE c.id = s.clientId AND s.clientId=?1")
    List getRegClientList(String clientId);

    /**
     * 查询指定网关服务下的注册的所有客户端
     * @return
     */
    @Query(value ="SELECT s.routeId,c.id,c.ip,s.token,s.secretKey,s.status FROM Client c, ClientServerRegister s WHERE c.id = s.clientId AND s.routeId=?1")
    List getByRouteRegClientList(String routeId);

	// 查询当前网关路由服务下没有注册的客户端
	@SqlToyQuery
	@Transactional(readOnly = true)
	List<Map<String, Object>> queryRegClientList(@Param("routeId") String routeId);

	// 查询当前网关路由服务下没有注册的客户端
	@SqlToyQuery
	@Transactional(readOnly = true)
	Page<Map<String, Object>> queryRegClients(@Param("routeId") String routeId, Pageable pageable);

	// 查询当前网关路由服务下没有注册的客户端
	@SqlToyQuery
	@Transactional(readOnly = true)
	Page<Client> queryNotRegClients(@Param("routeId") String routeId, Pageable pageable);

	// 查询指定客户端注册的所有应用
	@SqlToyQuery
	List<GatewayAppRouteRegServer> queryClientRegisterAppsByAppId(@Param("clientId") String clientId);

	// 查询当前客户端所有注册的网关路由服务
	@SqlToyQuery
	@Transactional(readOnly = true)
	Page<GatewayAppRouteRegServer> queryRegServers(@Param("clientId") String clientId, Pageable pageable);

	// 查询当前客户端没有注册的网关路由服务
	@SqlToyQuery
	@Transactional(readOnly = true)
	Page<GatewayAppRoute> queryNotRegServers(@Param("clientId") String clientId, Pageable pageable);

}
