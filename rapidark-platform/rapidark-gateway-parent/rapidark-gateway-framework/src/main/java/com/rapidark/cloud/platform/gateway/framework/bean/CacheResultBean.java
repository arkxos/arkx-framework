package com.rapidark.cloud.platform.gateway.framework.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author JL
 * @Date 2023/10/13
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class CacheResultBean implements java.io.Serializable {
    private Boolean checked;
}
