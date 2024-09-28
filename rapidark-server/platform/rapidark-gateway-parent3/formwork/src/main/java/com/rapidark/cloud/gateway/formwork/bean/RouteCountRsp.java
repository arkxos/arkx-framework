package com.rapidark.cloud.gateway.formwork.bean;

import com.rapidark.cloud.gateway.formwork.entity.Route;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author JL
 * @Date 2020/12/30
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class RouteCountRsp extends Route implements java.io.Serializable {
    /**
     * 统计量
     */
    private Integer count;

}
