package com.rapidark.cloud.base.client.model.entity;

import com.rapidark.framework.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 系统权限-用户关联
 *
 * @author liuyadu
 */
@Data
@Entity
@Table(name="base_authority_user")
public class BaseAuthorityUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @ApiModelProperty(value = "ID")
    private Long id;

    /**
     * 权限ID
     */
    private Long authorityId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 过期时间
     */
    private Date expireTime;

}
