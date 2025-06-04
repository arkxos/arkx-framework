package com.arkxos.framework.common.security;

import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 自定义已授权权限标识
 * @author darkness
 * @date 2022/6/6 15:46
 * @version 1.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OpenAuthority implements GrantedAuthority {

    private static final long serialVersionUID = -4682269495406460314L;

    /**
     * 权限Id
     */
    private Long authorityId;
    /**
     * 权限标识
     */
    private String authority;
    /**
     * 过期时间,用于判断权限是否已过期
     */
    private Date expireTime;

    /**
     * 权限所有者
     */
    private String owner;

    private String serviceId;

    private String path;

    @JsonProperty("isExpired")
    public Boolean getIsExpired() {
        if (expireTime != null && System.currentTimeMillis() > expireTime.getTime()) {
            return true;
        }
        return false;
    }

    public OpenAuthority() {

    }

    public OpenAuthority(String authority) {
        Assert.hasText(authority, "A granted authority textual representation is required");
        this.authority = authority;
    }

    public OpenAuthority(String authority, Date expireTime) {
        Assert.hasText(authority, "A granted authority textual representation is required");
        this.authority = authority;
        this.expireTime = expireTime;
    }

    public OpenAuthority(Long authorityId, String authority, Date expireTime, String owner) {
        this.authorityId = authorityId;
        this.authority = authority;
        this.expireTime = expireTime;
        this.owner = owner;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof OpenAuthority ? this.authority.equals(((OpenAuthority) obj).authority) : false;
        }
    }

    @Override
    public int hashCode() {
        return this.authority.hashCode();
    }

    @Override
    public String toString() {
        return this.authority;
    }

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
