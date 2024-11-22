package com.rapidark.cloud.platform.gateway.framework.bean;

import com.rapidark.cloud.platform.gateway.framework.entity.Balanced;
import com.rapidark.cloud.platform.gateway.framework.entity.LoadServer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description
 * @Author JL
 * @Date 2020/06/28
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class BalancedReq extends Balanced implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
    private List<LoadServer> serverList;
}
