package com.flying.fish.formwork.bean;

import com.flying.fish.formwork.entity.SecureIp;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 对前端请求进行接收与封装bean
 * @Author jianglong
 * @Date 2020/05/28
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class SecureIpReq extends SecureIp implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
