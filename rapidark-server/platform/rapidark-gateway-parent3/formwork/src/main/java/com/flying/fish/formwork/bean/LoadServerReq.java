package com.flying.fish.formwork.bean;

import com.flying.fish.formwork.entity.LoadServer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/06/28
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class LoadServerReq extends LoadServer implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
