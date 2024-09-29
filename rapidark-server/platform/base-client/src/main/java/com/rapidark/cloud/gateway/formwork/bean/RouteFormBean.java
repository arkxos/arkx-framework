package com.rapidark.cloud.gateway.formwork.bean;

import com.rapidark.cloud.gateway.formwork.entity.Monitor;
import com.rapidark.cloud.gateway.formwork.entity.Route;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/05/14
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RouteFormBean extends Route implements java.io.Serializable {
    private Monitor monitor;
}
