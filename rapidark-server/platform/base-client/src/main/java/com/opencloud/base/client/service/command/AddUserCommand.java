package com.opencloud.base.client.service.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/21 10:56
 */
@Data
@ApiModel(value = "添加用户命令")
public class AddUserCommand {

    @ApiModelProperty(name = "userName", value = "用户名", example = "", required = true)
    @NotEmpty(message = "用户名不能为空")
    private String userName;

    @ApiModelProperty(name = "password", value = "密码", example = "", required = true)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @ApiModelProperty(name = "nickName", value = "昵称", example = "", required = true)
    @NotEmpty(message = "昵称不能为空")
    private String nickName;

    @ApiModelProperty(name = "status", value = "状态:0-禁用 1-正常 2-锁定", example = "", required = true)
    @NotNull(message = "状态不能为空")
    private Integer status;

    @ApiModelProperty(name = "userType", value = "用户类型:super-超级管理员 normal-普通管理员", example = "", required = true)
    @NotEmpty(message = "用户类型不能为空")
    private String userType;

    @ApiModelProperty(name = "email", value = "邮箱", example = "")
    private String email;

    @ApiModelProperty(name = "mobile", value = "电话", example = "")
    private String mobile;

    @ApiModelProperty(name = "userDesc", value = "描述", example = "")
    private String userDesc;

    @ApiModelProperty(name = "avatar", value = "头像", example = "")
    private String avatar;

}
