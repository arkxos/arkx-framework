package com.rapidark.cloud.base.client.model.entity;


import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;
import com.rapidark.framework.data.jpa.entity.IdLongEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 系统权限-功能操作关联表
 *
 * @author: liuyadu
 * @date: 2018/10/24 16:21
 * @description:
 */
@Data
@Entity
@Table(name="base_authority_action")
public class BaseAuthorityAction extends IdLongEntity {

    private static final long serialVersionUID = 1471599074044557390L;

    /**
     * 操作资源ID
     */
    private Long actionId;

    /**
     * 权限ID
     */
    private Long authorityId;

}
