package com.rapidark.cloud.platform.gateway.framework.bean;

import lombok.Data;

/**
 * @Description 鉴权器开关
 * @Author JL
 * @Date 2020/05/14
 * @Version V1.0
 */
@Data
public class GatewayAppRouteAccessBean {
    private Boolean headerChecked;
    private Boolean ipChecked;
    private Boolean parameterChecked;
    private Boolean timeChecked;
    private Boolean cookieChecked;
}
