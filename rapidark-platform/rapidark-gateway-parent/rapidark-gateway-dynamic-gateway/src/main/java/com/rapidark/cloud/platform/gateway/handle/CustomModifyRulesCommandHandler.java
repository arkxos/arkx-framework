package com.rapidark.cloud.platform.gateway.handle;

import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;
import com.alibaba.csp.sentinel.command.handler.ModifyRulesCommandHandler;
import com.rapidark.cloud.platform.gateway.framework.service.CustomNacosConfigService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 此类自动同步将Sentinel控制台中修改的配置加载到本地应用中，并同时推送到nacos中（注意：Sentinel控制台修改的配置并未做入库处理）
 * @Author JL
 * @Date 2022/12/22
 * @Version V1.0
 */
@Slf4j
public class CustomModifyRulesCommandHandler extends ModifyRulesCommandHandler {

    private CustomNacosConfigService customNacosConfigService;

    private static final String FLOW_RULE_TYPE = "flow";
    private static final String DEGRADE_RULE_TYPE = "degrade";

    public CustomModifyRulesCommandHandler(CustomNacosConfigService customNacosConfigService) {
        this.customNacosConfigService = customNacosConfigService;
    }

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        // 执行父类方法
        CommandResponse<String> response = super.handle(request);
        // 执行成功，则同步到nacos配置中心
        if (response.isSuccess()) {
            String type = request.getParam("type");
            String data = request.getParam("data");
            String dataId ;
            String group = customNacosConfigService.getGroup();
            if (FLOW_RULE_TYPE.equalsIgnoreCase(type)) {
                dataId = customNacosConfigService.getPrefix()+ ".sentinel." + FLOW_RULE_TYPE;
            } else if (DEGRADE_RULE_TYPE.equalsIgnoreCase(type)) {
                dataId = customNacosConfigService.getPrefix()+ ".sentinel." + DEGRADE_RULE_TYPE;
            } else {
                log.info("sentinel控制台配置只同步“流控规则”和“熔断规则”，其它限流规则暂不支持...");
                return response;
            }
            customNacosConfigService.publishConfig(dataId, group,  data);
        }
        return response;
    }
}