package com.rapidark.cloud.platform.gateway.framework.bean;

import com.rapidark.cloud.platform.gateway.framework.entity.RouteConfig;
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
public class RouteCountRsp extends RouteConfig implements java.io.Serializable {
    /**
     * 统计量
     */
    private Integer count;

}
