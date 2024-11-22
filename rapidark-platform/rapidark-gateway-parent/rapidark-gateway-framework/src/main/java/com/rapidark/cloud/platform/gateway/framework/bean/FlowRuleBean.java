package com.rapidark.cloud.platform.gateway.framework.bean;

import lombok.Data;

/**
 * @Description 启用限流
 * @Author JL
 * @Date 2022/12/04
 * @Version V1.0
 */
@Data
public class FlowRuleBean {

    private Boolean defaultChecked;
    private Boolean warmUpChecked;
    private Boolean rateLimiterChecked;

}