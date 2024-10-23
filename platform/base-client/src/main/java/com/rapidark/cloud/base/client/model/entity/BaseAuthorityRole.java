package com.rapidark.cloud.base.client.model.entity;


import com.rapidark.framework.data.jpa.entity.IdLongEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 系统权限-角色关联
 *
 * @author liuyadu
 */
@Data
@Entity
@Table(name="base_authority_role")
public class BaseAuthorityRole extends IdLongEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    private Long authorityId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 过期时间:null表示长期
     */
    private Date expireTime;

}
