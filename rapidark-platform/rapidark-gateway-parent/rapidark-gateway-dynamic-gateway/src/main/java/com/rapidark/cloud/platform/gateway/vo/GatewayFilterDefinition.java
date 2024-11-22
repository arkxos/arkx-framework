package com.rapidark.cloud.platform.gateway.vo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description 创建过滤器模型
 * @Author JL
 * @Date 2020/05/11
 * @Version V1.0
 */
@Data
public class GatewayFilterDefinition {
    /**
     * Filter Name
     */
    private String name;
    /**
     * 对应的路由规则
     */
    private Map<String, String> args = new LinkedHashMap<>();

}
