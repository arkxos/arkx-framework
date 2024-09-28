package com.flying.fish.formwork.bean;

import lombok.Data;

/**
 * @Description 鉴权器开关
 * @Author jianglong
 * @Date 2020/05/14
 * @Version V1.0
 */
@Data
public class RouteAccessBean {
    private Boolean headerChecked;
    private Boolean ipChecked;
    private Boolean parameterChecked;
    private Boolean timeChecked;
    private Boolean cookieChecked;
}
