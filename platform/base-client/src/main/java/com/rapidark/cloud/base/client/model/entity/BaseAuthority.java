package com.rapidark.cloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * 系统权限-菜单权限、操作权限、API权限
 *
 * @author liuyadu
 */
@Data
@Entity
@Table(name="base_authority")
public class BaseAuthority extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "authority_Id")
    @ApiModelProperty(value = "authority_Id")
    private String authorityId;

    /**
     * 权限标识
     */
    private String authority;

    /**
     * 菜单资源ID
     */
    private String menuId;

    /**
     * API资源ID
     */
    private String apiId;

    /**
     * 操作资源ID
     */
    private String actionId;

    /**
     * 状态
     */
    private Integer status;

}
