package com.flying.fish.formwork.bean;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/07/07
 * @Version V1.0
 */
@Data
public class CountRsp implements java.io.Serializable {

    private List<String> dates;
    private List<CountData> datas;
    private List<Integer> counts;

    @Data
    public static class CountData{
        private String name;
        private String routeId;
        private Integer [] counts;
    }
}
