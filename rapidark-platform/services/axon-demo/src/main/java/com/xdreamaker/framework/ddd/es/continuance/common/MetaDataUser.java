package com.xdreamaker.framework.ddd.es.continuance.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author darkness
 * @date 2021/6/26 23:46
 * @version 1.0
 */
@Getter
@Setter
@Builder
public class MetaDataUser implements MetaDataUserInterface {

    private String name;

    private Long userId;

    private Long customerId;

}
