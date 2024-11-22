package com.rapidark.cloud.platform.gateway.framework.bean;

import com.rapidark.cloud.platform.gateway.framework.entity.SecureIp;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 对前端请求进行接收与封装bean
 * @Author JL
 * @Date 2020/05/28
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class SecureIpReq extends SecureIp implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
