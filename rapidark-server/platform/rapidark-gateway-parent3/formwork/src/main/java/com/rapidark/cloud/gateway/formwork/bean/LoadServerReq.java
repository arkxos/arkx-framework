package com.rapidark.cloud.gateway.formwork.bean;

import com.rapidark.cloud.gateway.formwork.entity.LoadServer;
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
