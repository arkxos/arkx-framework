package com.rapidark.cloud.platform.gateway.framework.bean;

import com.rapidark.cloud.platform.gateway.framework.entity.Balanced;
import com.rapidark.cloud.platform.gateway.framework.entity.LoadServer;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author JL
 * @Date 2020/06/30
 * @Version V1.0
 */
@Data
public class BalancedRsp implements java.io.Serializable {
    private Balanced balanced;
    private List<LoadServer> serverList;
}
