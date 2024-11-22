package com.rapidark.cloud.platform.gateway.framework.bean;

import com.rapidark.cloud.platform.gateway.framework.entity.LoadServer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author JL
 * @Date 2020/06/28
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class LoadServerReq extends LoadServer implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
