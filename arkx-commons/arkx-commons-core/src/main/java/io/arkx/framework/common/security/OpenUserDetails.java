package io.arkx.framework.common.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;

import lombok.Data;

/**
 * 自定义认证用户信息
 *
 * @author liuyadu
 */
@Data
public class OpenUserDetails implements UserDetails {

    private static final long serialVersionUID = -123308657146774881L;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 登录名
     */
    private String username;
    /**
     * 密码
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String password;

    /**
     * 用户权限
     */
    private Collection<? extends GrantedAuthority> authorities;
    /**
     * 是否已锁定
     */
    private boolean accountNonLocked;
    /**
     * 是否已过期
     */
    private boolean accountNonExpired;
    /**
     * 是否启用
     */
    private boolean enabled;
    /**
     * 密码是否已过期
     */
    private boolean credentialsNonExpired;
    /**
     * 认证客户端ID
     */
    private String clientId;
    /**
     * 认证中心域,适用于区分多用户源,多认证中心域
     */
    private String domain;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 账户Id
     */
    private Long accountId;

    /***
     * 账户类型
     */
    private String accountType;

    /**
     * 用户附加属性
     */
    private Map<String, Object> attrs = Maps.newHashMap();

    /**
     * 只是客户端模式.不包含用户信息
     *
     * @return
     */
    public Boolean isClientOnly() {
        return clientId != null && username == null;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            return Collections.EMPTY_LIST;
        }
        return this.authorities;
    }

}
