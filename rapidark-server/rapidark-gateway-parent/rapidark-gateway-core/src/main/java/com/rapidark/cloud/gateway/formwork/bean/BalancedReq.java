package com.rapidark.cloud.gateway.formwork.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import com.rapidark.cloud.gateway.formwork.entity.Balanced;
import com.rapidark.cloud.gateway.formwork.entity.LoadServer;

/**
 * @Description
 * @Author jianglong
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
