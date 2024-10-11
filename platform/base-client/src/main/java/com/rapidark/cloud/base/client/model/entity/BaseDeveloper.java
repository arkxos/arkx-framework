package com.rapidark.cloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rapidark.common.annotation.TableAlias;
import com.rapidark.common.model.BaseEntity;
import com.rapidark.common.mybatis.base.entity.AbstractEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 系统用户-管理员信息
 * @author darkness
 * @date 2022/6/24 14:24
 * @version 1.0
 */
@Entity
@Getter
@Setter
@Table(name="base_developer")
public class BaseDeveloper extends BaseEntity {

    private static final long serialVersionUID = -735161640894047414L;

    /**
     * 系统用户ID
     */
    @Id
    @Column(name = "ID")
    @ApiModelProperty(value = "ID")
    private Long id;

    /**
     * 开发者类型:isp-服务提供商 dev-自研开发者
     */
    private Integer type;

    private String companyName; // 公司名称

    private String personName; // 联系人

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 登陆名
     */
    private String userName;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 描述
     */
    private String userDesc;

    /**
     * 密码
     */
    @JsonIgnore
    @Transient
    private String password;

    /**
     * 状态:0-禁用 1-正常 2-锁定
     */
    private Integer status;

}
