package com.rapidark.cloud.platform.gateway.vo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description 路由断言模型
 * @Author JL
 * @Date 2020/05/11
 * @Version V1.0
 */
@Data
public class GatewayPredicateDefinition {
    /**
     * 断言对应的Name
     */
    private String name;
    /**
     * 配置的断言规则
     */
    private Map<String, String> args = new LinkedHashMap<>();

}
