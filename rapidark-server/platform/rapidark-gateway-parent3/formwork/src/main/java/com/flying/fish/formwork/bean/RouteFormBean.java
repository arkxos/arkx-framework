package com.flying.fish.formwork.bean;

import com.flying.fish.formwork.entity.Monitor;
import com.flying.fish.formwork.entity.Route;
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
