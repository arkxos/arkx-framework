package com.rapidark.cloud.gateway.formwork.bean;

import com.rapidark.cloud.base.client.model.entity.OpenApp;
import com.rapidark.cloud.gateway.formwork.entity.Client;

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
public class ClientReq extends OpenApp implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
