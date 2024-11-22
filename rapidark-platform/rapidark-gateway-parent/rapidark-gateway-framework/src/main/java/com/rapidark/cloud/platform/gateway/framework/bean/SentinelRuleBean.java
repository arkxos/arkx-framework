package com.rapidark.cloud.platform.gateway.framework.bean;

import lombok.Data;

/**
 * @Description
 * @Author JL
 * @Date 2022/11/23
 * @Version V1.0
 */
@Data
public class SentinelRuleBean {

    private Boolean flowChecked;
    private Boolean degradChecked;
    private Boolean paramFlowChecked;

}
