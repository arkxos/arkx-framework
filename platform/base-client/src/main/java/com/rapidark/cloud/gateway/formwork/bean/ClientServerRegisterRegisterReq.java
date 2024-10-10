package com.rapidark.cloud.gateway.formwork.bean;

import com.rapidark.cloud.gateway.formwork.entity.ClientServerRegister;

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
public class ClientServerRegisterRegisterReq extends ClientServerRegister implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
