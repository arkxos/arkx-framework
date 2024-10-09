package com.rapidark.cloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.common.model.BaseEntity;
import com.rapidark.common.mybatis.base.entity.AbstractEntity;
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
    private String id;

    /**
     * 权限ID
     */
    private String authorityId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 过期时间
     */
    private Date expireTime;

}
