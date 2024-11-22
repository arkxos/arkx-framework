package com.rapidark.cloud.platform.gateway.demo;

import com.alibaba.csp.sentinel.command.handler.ModifyRulesCommandHandler;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.transport.command.SimpleHttpCommandCenter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.Listener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @Description
 * @Author JL
 * @Date 2022/12/09
 * @Version V1.0
 */
public class NacosToSentinelListenerTest {
    // nacos server ip
    private static final String serverAddr = "localhost:8848";
    // nacos group
    private static final String group = "DEFAULT_GROUP";
    // nacos dataId
    private static final String dataId = "flying-fish-gateway.sentinel.flow";

    public static void main(String[] args) {
        loadRules();
        pushRules();
        while(true){
            try{
                Thread.sleep(1000);
            }catch (Exception e){

            }
        }
    }

    private static void addListener(){
        try {
            ConfigService configService = NacosFactory.createConfigService(serverAddr);
            String configInfo = configService.getConfig(dataId, group, 5000L);
            if (StringUtils.isNotBlank(configInfo)){
                //初始化加载Sentinel限流规则配置
                setSentinelFlowConfig(configInfo);
            }
            configService.addListener(dataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    System.out.println("------------listener news context------------\n" + configInfo);
                    //当nacos获取到配置变更监听后，更新Sentinel限流规则配置
                    setSentinelFlowConfig(configInfo);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void pushRules(){
        // 对Sentinel控制台向客户端发起setRules流控规则的调用结果，做二次处理（用于将规则配置同步到nacos配置中心）
        // 默认setRules修改流控制规则由ModifyRulesCommandHandler类处理
        SimpleHttpCommandCenter.registerCommand("setRules", new ModifyRulesCommandHandler());
    }

    private static void loadRules() {
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(serverAddr, group, dataId,
                source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                }));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }

    /**
     * 修改流控规则
     * @param source
     */
    public static void setSentinelFlowConfig(String source){
        FlowRuleManager.loadRules(JSON.parseObject(source, new TypeReference<List<FlowRule>>(){}));
        System.out.println("------------update SentinelConfig flowRule -----------");
    }
}
