package com.rapidark.cloud.platform.gateway.framework.service;

//import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
//import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.cloud.platform.gateway.framework.repository.SentinelRuleRepository;
import com.rapidark.cloud.platform.gateway.framework.entity.SentinelRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.Date;
import java.util.List;

/**
 * 限流管理业务处理类
 */
@Slf4j
@Service
public class SentinelRuleService extends BaseService<SentinelRule, String, SentinelRuleRepository> {

    @Resource
    private CustomNacosConfigService customNacosConfigService;

    @Override
    public void save(SentinelRule sentinelRule){
        sentinelRule.setUpdateTime(new Date());
        super.save(sentinelRule);
        loadSentinelRules();
    }

    @Override
    public void deleteById(String id){
        SentinelRule dbSentinelRule = findById(id);
        if (dbSentinelRule != null){
            super.deleteById(id);
            loadSentinelRules();
        }
    }

    /**
     * 加载限流组件各种规则配置，并推送到nacso注册中，通过nacos同步到各应用端
     */
    public void loadSentinelRules(){
        List<SentinelRule> rules = this.findAll();
//        List<FlowRule> flowRules = new ArrayList<>();
//        List<DegradeRule> degradeRules = new ArrayList<>();
//        for (SentinelRule sentinelRule : rules){
//            if (StringUtils.isNotBlank(sentinelRule.getFlowRule())) {
//                FlowRule flowRule = JSONObject.parseObject(sentinelRule.getFlowRule(), FlowRule.class);
//                flowRule.setResource(sentinelRule.getId());
//                flowRules.add(flowRule);
//            }
//            if (StringUtils.isNotBlank(sentinelRule.getDegradeRule())) {
//                DegradeRule degradeRule = JSONObject.parseObject(sentinelRule.getDegradeRule(), DegradeRule.class);
//                degradeRule.setResource(sentinelRule.getId());
//                degradeRules.add(degradeRule);
//            }
//        }

        //加载到应用组件
        //FlowRuleManager.loadRules(flowRules);
        //DegradeRuleManager.loadRules(degradeRules);

        //直接推送到nacos，再通过register2Property方法注册naocs配置监听自动同步到应用组件中
//        customNacosConfigService.publishConfig(
//                customNacosConfigService.getPrefix() + ".sentinel.flow",
//                customNacosConfigService.getGroup(),
//                JSON.toJSONString(flowRules));
//        customNacosConfigService.publishConfig(
//                customNacosConfigService.getPrefix() + ".sentinel.degrade",
//                customNacosConfigService.getGroup(),
//                JSON.toJSONString(degradeRules));
    }

}
