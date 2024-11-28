//package com.rapidark.cloud.platform.gateway.config;
//
//import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
//import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
//import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
//import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
//import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
//import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
//import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
//import com.alibaba.csp.sentinel.transport.command.SimpleHttpCommandCenter;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.TypeReference;
//import com.rapidark.cloud.platform.gateway.framework.service.CustomNacosConfigService;
//import com.rapidark.cloud.platform.gateway.filter.global.CustomSentinelGatewayFilter;
//import com.rapidark.cloud.platform.gateway.handle.CustomBlockRequestHandler;
//import com.rapidark.cloud.platform.gateway.handle.CustomModifyRulesCommandHandler;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.Resource;
//import java.util.List;
//
///**
// * @Description 自定义Sentinel限流组件配置：注册nacos与Sentinel控制台事件
// * @Author JL
// * @Date 2022/12/21
// * @Version V1.0
// */
////@Configuration
//public class SentinelGatewayConfig {
//
//    @Resource
//    private CustomNacosConfigService customNacosConfigService;
//
//    /**
//     * 继承父类SentinelGatewayFilter过滤器，用于处理网关路由DegradeRule规则
//     * @return
//     */
////    @Bean
////    @Order(Ordered.HIGHEST_PRECEDENCE)
////    public GlobalFilter customSentinelGatewayFilter() {
////        return new CustomSentinelGatewayFilter();
////    }
//
//    @PostConstruct
//    public void doInit() {
//        //自定义限流组件异常处理器，对Sentinel规则异常异常进行统一包装
//        GatewayCallbackManager.setBlockHandler(new CustomBlockRequestHandler());
//        // 加载自定义规则
//        registerNacosToSentinelProperty();
//        // 注册事件，当Sentinel配置发生变更则触发此事件，并更新本地组件规则
//        registerModifyRulesCommand();
//    }
//
//    /**
//     * 将nacos的配置加载到Sentinel组件的Rule监听管理器中，当nacos数据发生变化，将自动推送更新到组件中
//     */
//    public void registerNacosToSentinelProperty(){
//        // 因Sentinel已支持gateway网关路由，通过source名称映射routeId，实现自动识别路由断言地址
//        //添加流控规则
//        registerSentinelFlowRule();
//        //添加熔断规则
//        registerSentinelDegradeRule();
//    }
//
//    /**
//     * 限流
//     */
//    public void registerSentinelFlowRule(){
//        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(
//                customNacosConfigService.getProperties(),
//                customNacosConfigService.getGroup(),
//                customNacosConfigService.getPrefix() + ".sentinel.flow",
//                source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
//                }));
//        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
//    }
//
//    /**
//     * 熔断，注意Setinel目前熔断只支持RT慢请求降级，对于异常比例和异常数降级暂时无支持，参见：https://github.com/alibaba/Sentinel/issues/1842
//     */
//    public void registerSentinelDegradeRule(){
//        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new NacosDataSource<>(
//                customNacosConfigService.getProperties(),
//                customNacosConfigService.getGroup(),
//                customNacosConfigService.getPrefix() + ".sentinel.degrade",
//                source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {
//                }));
//        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
//    }
//
//    /**
//     * 将Sentinel控制台的Rule配置变更，设置到应用组件中，并使应用Sentinel组件立即生效(同时将修改配置推送到nacos中)
//     */
//    public void registerModifyRulesCommand(){
//        SimpleHttpCommandCenter.registerCommand("setRules", new CustomModifyRulesCommandHandler(customNacosConfigService));
//    }
//}
