package com.rapidark.cloud.platform.gateway.framework.bean;

import com.rapidark.cloud.platform.gateway.framework.entity.ClientServerRegister;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author JL
 * @Date 2020/05/16
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ClientServerRegisterReq extends ClientServerRegister implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
