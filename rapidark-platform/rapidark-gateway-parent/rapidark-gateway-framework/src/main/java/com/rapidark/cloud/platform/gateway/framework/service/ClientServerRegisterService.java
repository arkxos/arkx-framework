package com.rapidark.cloud.platform.gateway.framework.service;

import com.rapidark.cloud.platform.gateway.framework.base.BaseService;
import com.rapidark.cloud.platform.gateway.framework.bean.GatewayAppRouteRegServer;
import com.rapidark.cloud.platform.gateway.framework.entity.Client;
import com.rapidark.cloud.platform.gateway.framework.entity.GatewayAppRoute;
import com.rapidark.cloud.platform.gateway.framework.repository.ClientServerRegisterRepository;
import com.rapidark.cloud.platform.gateway.framework.entity.ClientServerRegister;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import com.rapidark.framework.common.utils.PageResult;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @Description 注册服务业务管理类
 * @Author jianglong
 * @Date 2020/05/16
 * @Version V1.0
 */
@Service
public class ClientServerRegisterService extends BaseService<ClientServerRegister, Long, ClientServerRegisterRepository> {

	@Autowired
	private ClientServerRegisterRepository clientServerRegisterRepository;

	private static final String IS_TIMEOUT = "isTimeout";
	private static final String TOKEN_EFFECTIVE_TIME = "tokenEffectiveTime";

	/**
	 * 停止客户端下所有路由服务的访问（状态置为1，禁止通行）
	 * @param clientId
	 */
	public void stopClientAllRoute(String clientId){
		clientServerRegisterRepository.setClientAllRouteStatus(clientId,Constants.YES,Constants.NO);
	}

	/**
	 * 启动客户端下所有路由服务的访问（状态置为0，允许通行）
	 * @param clientId
	 */
	public void startClientAllRoute(String clientId){
		clientServerRegisterRepository.setClientAllRouteStatus(clientId,Constants.NO,Constants.YES);
	}

	/**
	 * 停止路由服务下所有客户端的访问（状态置为1，禁止通行）
	 * @param routeId
	 */
	public void stopRouteAllClient(String routeId){
		clientServerRegisterRepository.setRouteAllClientStatus(routeId,Constants.YES,Constants.NO);
	}

	/**
	 * 启动路由服务下所有客户端的访问（状态置为0，允许通行）
	 * @param routeId
	 */
	public void startRouteAllClient(String routeId){
		clientServerRegisterRepository.setRouteAllClientStatus(routeId,Constants.NO,Constants.YES);
	}

	/**
	 * 查询当所有已注册的客户端
	 * @return
	 */
	public List allRegClientList(){
		return clientServerRegisterRepository.allRegClientList();
	}

	/**
	 * 查询指定客户端注册的所有网关路由服务
	 * @param clientId
	 * @return
	 */
	public List getRegClientList(String clientId){
		return clientServerRegisterRepository.getRegClientList(clientId);
	}

	/**
	 * 查询指定网关服务下的注册的所有客户端
	 * @param routeId
	 * @return
	 */
	public List<Map<String,Object>> getByRouteRegClientList(String routeId){
		return clientServerRegisterRepository.getByRouteRegClientList(routeId);
	}

	/**
	 * 查询当前网关路由服务下已注册的客户端
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Map<String,Object>> regClientList(String routeId){
		return clientServerRegisterRepository.queryRegClientList(routeId);
	}

	/**
	 * 查询当前网关路由服务下已注册的客户端
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageResult<Map<String, Object>> clientPageList(String routeId, Pageable pageable){
		Page<Map<String, Object>> pageResult = clientServerRegisterRepository.queryRegClients(routeId, pageable);
		List<Map<String, Object>> list = pageResult.getContent();
		if (!list.isEmpty()){
			long nowTime = System.currentTimeMillis();
			for (Map<String, Object> map : list){
				Object tokenEffectiveTime = map.get(TOKEN_EFFECTIVE_TIME);
				if (tokenEffectiveTime instanceof java.sql.Timestamp){
					Timestamp timestamp = (Timestamp) tokenEffectiveTime;
					map.put(TOKEN_EFFECTIVE_TIME, DateFormatUtils.format(new Date(timestamp.getTime()),Constants.YYYY_MM_DD_HH_MM_SS));
					if (timestamp.getTime() < nowTime){
						map.put(IS_TIMEOUT, Constants.NO);
						continue;
					}
				}
				map.put(IS_TIMEOUT, Constants.YES);
			}
		}
		return PageResult.of(pageResult);
	}

	/**
	 * 查询当前网关路由服务下没有注册的客户端
	 * @param routeId
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<Client> notRegClientPageList(String routeId, Pageable pageable){
		return clientServerRegisterRepository.queryNotRegClients(routeId, pageable);
	}

	/**
	 * 查询当前客户端已注册的网关路由服务
	 * @param clientId
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageResult<GatewayAppRouteRegServer> serverPageList(String clientId, Pageable pageable){
		return PageResult.of(clientServerRegisterRepository.queryRegServers(clientId, pageable));
	}

	/**
	 * 查询当前客户端没有注册的网关路由服务
	 * @param clientId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<GatewayAppRoute> notRegServerPageList(String clientId, Pageable pageable){
		return clientServerRegisterRepository.queryNotRegServers(clientId, pageable);
	}

	/**
	 * 查询客户端注册的所有应用
	 * @author darkness
	 * @date 2022/6/6 13:40
	 * @version 1.0
	 * @param clientId
	 * @return java.util.List<com.rapidark.cloud.gateway.manage.service.dto.GatewayAppRouteRegServer>
	 */
	public List<GatewayAppRouteRegServer> queryClientRegisterAppsByAppId(String clientId) {
		return clientServerRegisterRepository.queryClientRegisterAppsByAppId(clientId);
	}

}
