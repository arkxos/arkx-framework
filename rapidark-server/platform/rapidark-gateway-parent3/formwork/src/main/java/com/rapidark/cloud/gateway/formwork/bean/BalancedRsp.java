package com.rapidark.cloud.gateway.formwork.bean;

import com.rapidark.cloud.gateway.formwork.entity.Balanced;
import com.rapidark.cloud.gateway.formwork.entity.LoadServer;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/06/30
 * @Version V1.0
 */
@Data
public class BalancedRsp implements java.io.Serializable {
    private Balanced balanced;
    private List<LoadServer> serverList;
}
