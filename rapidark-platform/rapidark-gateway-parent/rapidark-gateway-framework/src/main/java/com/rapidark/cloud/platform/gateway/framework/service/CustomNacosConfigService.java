package com.rapidark.cloud.platform.gateway.framework.service;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.rapidark.cloud.platform.gateway.framework.bean.GatewayNacosConfigBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Properties;

/**
 * @Description 将自定义配置推送到nacos配置中心
 * @Author JL
 * @Date 2021/09/23
 * @Version V1.0
 */
@Slf4j
@Service
public class CustomNacosConfigService {
	@Resource
	private NacosConfigProperties configProperties;

	/**
	 * 将网关负载均衡配置推送到nacos中
	 * @param balancedId
	 */
	public void publishBalancedNacosConfig(final String balancedId){
		GatewayNacosConfigBean gatewayNacosConfig = new GatewayNacosConfigBean();
		gatewayNacosConfig.setBalancedId(balancedId);
		publishConfigToNacos(gatewayNacosConfig);
	}

	/**
	 * 将网关路由配置推送到nacos中
	 * @param routeId
	 */
	public void publishRouteNacosConfig(final String routeId){
		GatewayNacosConfigBean gatewayNacosConfig = new GatewayNacosConfigBean();
		gatewayNacosConfig.setRouteId(routeId);
		publishConfigToNacos(gatewayNacosConfig);
	}

	/**
	 * 将注册到网关路由的客户端服务配置推送到nacos中
	 * @param regServerId
	 */
	public void publishRegServerNacosConfig(final Long regServerId){
		GatewayNacosConfigBean gatewayNacosConfig = new GatewayNacosConfigBean();
		gatewayNacosConfig.setRegServerId(regServerId);
		publishConfigToNacos(gatewayNacosConfig);
	}

	/**
	 * 将网关客户端ID推送到nacos中
	 * @param clientId
	 */
	public void publishClientNacosConfig(final String clientId){
		GatewayNacosConfigBean gatewayNacosConfig = new GatewayNacosConfigBean();
		gatewayNacosConfig.setClientId(clientId);
		publishConfigToNacos(gatewayNacosConfig);
	}

	/**
	 * 将IP鉴权配置推送到nacos中
	 * @param ip
	 */
	public void publishIpNacosConfig(final String ip){
		GatewayNacosConfigBean gatewayNacosConfig = new GatewayNacosConfigBean();
		gatewayNacosConfig.setIp(ip);
		publishConfigToNacos(gatewayNacosConfig);
	}

	/**
	 * 将groovyScript规则引擎动态脚本ID配置推送到nacos中
	 * @param groovyScriptId
	 */
	public void publishGroovyScriptNacosConfig(final Long groovyScriptId){
		GatewayNacosConfigBean gatewayNacosConfig = new GatewayNacosConfigBean();
		gatewayNacosConfig.setGroovyScriptId(groovyScriptId);
		publishConfigToNacos(gatewayNacosConfig);
	}

	/**
	 * 将网关配置推送到nacos中, dataId 的完整格式如下：
	 * {prefix}-${spring.profiles.active}.${file-extension}
	 * @param gatewayNacosConfig
	 */
	public void publishConfigToNacos(GatewayNacosConfigBean gatewayNacosConfig) {
		String dataId = "gateway-route-changed.yml";
//		String dataId = configProperties.getPrefix() + "." + configProperties.getFileExtension();
		publishConfig(dataId, configProperties.getGroup(), gatewayNacosConfig.getGatewayConfig());
	}

	/**
	 * 推送配置到nacos指定dataId的group
	 * @param dataId
	 * @param group
	 * @param content
	 */
	public void publishConfig(String dataId, String group, String content){
//		try {
//			configProperties.configServiceInstance().publishConfig(dataId, group, content);
//		} catch(NacosException e){
//			log.error("推送配置到Nacos异常！" + e.getErrMsg(), e);
//		}
		try {
			ConfigService configService = new NacosConfigManager(configProperties).getConfigService();
			String oldContext = configService.getConfig(dataId, group, 3000L);
			if (!StringUtils.equals(content, oldContext)) {
				configService.publishConfig(dataId, group, content);
				log.info("成功推送到配置到Nacos配置中心！, content: {}", content);
			} else {
				log.info("Nacos配置中心配置无需变更！");
			}
		} catch (NacosException e) {
			log.error("推送配置到Nacos异常！" + e.getErrMsg(), e);
		}
	}

	/**
	 * 获取nacos服务地址
	 * @return
	 */
	public String getServerAddr() {
		return configProperties.getServerAddr();
	}

	/**
	 * 全部字符小写。只允许英文字符和 4 种特殊字符（"."、":"、"-"、"_"），不超过 256 字节
	 * dataId 的完整格式如下：
	 * {prefix}-${spring.profiles.active}.${file-extension}
	 * @return
	 */
	public String getDataId(){
		return getPrefix() + "." + configProperties.getFileExtension();
	}

	/**
	 * 获取nacos的DataId的前缀
	 * @return
	 */
	public String getPrefix(){
		return configProperties.getPrefix();
	}

	/**
	 * 获取nacos的分组名称（默认DEFAULT_GROUP）
	 * @return
	 */
	public String getGroup(){
		return configProperties.getGroup();
	}

	/**
	 * 获取nacos服务配置属性
	 * @return
	 */
	public Properties getProperties(){
		return configProperties.assembleConfigServiceProperties();
	}


}