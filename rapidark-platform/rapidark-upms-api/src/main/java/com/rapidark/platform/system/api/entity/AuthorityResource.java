package com.rapidark.platform.system.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 资源权限
 * @author darkness
 * @date 2022/5/27 11:25
 * @version 1.0
 */
@Data
public class AuthorityResource implements Serializable {

    private static final long serialVersionUID = -320031660125425711L;

    /**
     * 访问路径
     */
    private String path;

    /**
     * 权限标识
     */
    private String authority;

    /**
     * 权限ID
     */
    private Long authorityId;

    /**
     * 是否身份认证
     */
    private Integer isAuth;

    /**
     * 是否公开访问
     */
    private Integer isOpen;

    /**
     * 服务名称
     */
    private String serviceId;

    /**
     * 前缀
     */
    private String prefix;

    /**
     * 资源状态
     */
    private Integer status;

}
