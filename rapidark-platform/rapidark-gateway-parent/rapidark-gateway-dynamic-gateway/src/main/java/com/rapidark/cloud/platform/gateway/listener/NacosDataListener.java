package com.rapidark.cloud.platform.gateway.listener;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.listener.impl.PropertiesListener;
import com.rapidark.cloud.platform.gateway.service.ConfigRefreshService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import java.util.Properties;

/**
 * @Description nacos动态监听器，当dataId的内容发生变更后，自动刷新配置
 * @Author JL
 * @Date 2022/11/1
 * @Version V1.0
 */
@Slf4j
@Component
public class NacosDataListener implements ApplicationRunner {

    @Resource
    private NacosConfigManager nacosConfigManager;

    @Resource
    private ConfigRefreshService configRefreshService;

    @Override
    public void run(ApplicationArguments args) {
        NacosConfigProperties nacosConfigProperties = nacosConfigManager.getNacosConfigProperties();
        String serverAddr = nacosConfigProperties.getServerAddr();
        String dataId = nacosConfigProperties.getPrefix() + "." + nacosConfigProperties.getFileExtension();
        String group = nacosConfigProperties.getGroup();
        log.info("run nacos listener server, serverAddr{}: dataId:{}, group:{}, type:{}, namespace:{}", serverAddr, dataId,
                group, nacosConfigProperties.getFileExtension(), nacosConfigProperties.getNamespace());
        try {
			String listenTempGatewayRouteChangeConfigDataId = "gateway-route-changed.yml";
            nacosConfigManager.getConfigService().addListener(listenTempGatewayRouteChangeConfigDataId, group, new PropertiesListener() {

                @Override
                public void innerReceive(Properties properties) {
                    log.info("listener news context：\n{}", properties);
                    //当nacos获取到配置变更监听后，更新gateway网关
                    String gatewayConfig = (String) properties.get("gateway");
                    // 更新网关路由
                    if (StringUtils.isNotBlank(gatewayConfig)){
                        log.info("Refresh gatewayConfig changed: " + gatewayConfig);
                        configRefreshService.setGatewayConfig(gatewayConfig);
                        log.info("Refresh gatewayConfig changed success!");
                    }
                }
            });
        }catch(Exception e){
            log.error("load nacos listener data error, msg:{}" + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void shutDown() {
        try{
            nacosConfigManager.getConfigService().shutDown();
            log.info("shut down nacos listener server, status: " + nacosConfigManager.getConfigService().getServerStatus());
        }catch (NacosException e) {
            log.error("shut down nacos listener server error, msg:{}" + e.getMessage(), e);
        }
    }

}
