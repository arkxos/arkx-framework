package com.rapidark.cloud.base.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rapidark.cloud.base.client.model.entity.BaseAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 功能权限
 *
 * @author liuyadu
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorityAction extends BaseAction {

    private static final long serialVersionUID = -691740581827186502L;

    /**
     * 权限ID
     */
    private Long authorityId;

    /**
     * 权限标识
     */
    private String authority;

    /**
     * 是否需要安全认证
     */
    private Boolean isAuth = true;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AuthorityAction)) {
            return false;
        }
        AuthorityAction a = (AuthorityAction) obj;
        return this.authorityId.equals(a.getAuthorityId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorityId);
    }
}
