package com.flying.fish.formwork.bean;

import com.flying.fish.formwork.entity.Monitor;
import com.flying.fish.formwork.entity.Route;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author JL
 * @Date 2021/04/16
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RouteRsp extends Route {
    private Monitor monitor;
}
