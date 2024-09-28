package com.flying.fish.formwork.bean;

import com.flying.fish.formwork.entity.Balanced;
import com.flying.fish.formwork.entity.LoadServer;
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
