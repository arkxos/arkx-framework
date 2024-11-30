package com.rapidark.cloud.base.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rapidark.cloud.base.client.model.entity.BaseMenu;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 菜单权限
 *
 * @author liuyadu
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorityMenu extends BaseMenu implements Serializable {

    private static final long serialVersionUID = 3474271304324863160L;
    /**
     * 权限ID
     */
    private Long authorityId;

    /**
     * 权限标识
     */
    private String authority;

    private List<AuthorityAction> actionList;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AuthorityMenu)) {
            return false;
        }
        AuthorityMenu a = (AuthorityMenu) obj;
        return this.authorityId.equals(a.getAuthorityId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorityId);
    }
}
