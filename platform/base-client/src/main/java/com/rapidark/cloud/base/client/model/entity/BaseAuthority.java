package com.rapidark.cloud.base.client.model.entity;


import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 系统权限-菜单权限、操作权限、API权限
 *
 * @author liuyadu
 */
@Data
@Entity
@Table(name="base_authority")
public class BaseAuthority extends AbstractIdLongEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "authority_Id")
    @Schema(value = "authority_Id")
    private Long authorityId;

    /**
     * 权限标识
     */
    private String authority;

    /**
     * 菜单资源ID
     */
    private Long menuId;

    /**
     * API资源ID
     */
    private Long apiId;

    /**
     * 操作资源ID
     */
    private Long actionId;

    @Override
    public Long getId() {
        return authorityId;
    }

    @Override
    public void setId(Long id) {
        this.authorityId = id;
    }
}
