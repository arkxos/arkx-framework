package com.rapidark.platform.system.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.*;

/**
 * 系统用户-基础信息
 *
 * @author liuyadu
 */
@Data
@Entity
@Table(name="sys_user")
@Schema(description = "用户")
public class SysUser extends AbstractIdLongEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 系统用户ID
     */
    @Id
    @Column(name = "user_Id")
    @Schema(title = "userId")
    private Long userId;

	@Schema(description = "用户所属部门id")
	private Long deptId;

    /**
     * 登陆名
     */
	@Schema(description = "用户名")
    private String username;

    /**
     * 用户类型:super-超级管理员 normal-普通管理员
     */
    private String userType;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 昵称
     */
	@Schema(description = "昵称")
    private String nickName;

	@Schema(description = "姓名")
	private String name;

    /**
     * 头像
     */
	@Schema(description = "头像地址")
    private String avatar;

    /**
     * 邮箱
     */
	@Schema(description = "邮箱")
    private String email;

    /**
     * 手机号
     */
	@Schema(description = "手机号")
    private String mobile;

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

    @Override
    public Long getId() {
        return userId;
    }

    @Override
    public void setId(Long id) {

    }
}
