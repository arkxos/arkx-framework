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
public class BaseAuthorityAction extends BaseEntity {

    private static final long serialVersionUID = 1471599074044557390L;

    @Id
    @Column(name = "ID")
    @ApiModelProperty(value = "ID")
    private String id;

    /**
     * 操作资源ID
     */
    private String actionId;

    /**
     * 权限ID
     */
    private String authorityId;

}
