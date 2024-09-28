package com.flying.fish.formwork.bean;

import lombok.Data;

/**
 * @Description 封装按天和按小时的统计维度数据
 * @Author JL
 * @Date 2020/12/30
 * @Version V1.0
 */
@Data
public class CountTotalRsp implements java.io.Serializable {

    private CountRsp dayCounts;
    private CountRsp hourCounts;
    private CountRsp minCounts;

}
