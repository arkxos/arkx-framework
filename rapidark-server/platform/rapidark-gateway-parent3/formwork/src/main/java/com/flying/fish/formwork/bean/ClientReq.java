package com.flying.fish.formwork.bean;

import com.flying.fish.formwork.entity.Client;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/05/16
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ClientReq extends Client implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
