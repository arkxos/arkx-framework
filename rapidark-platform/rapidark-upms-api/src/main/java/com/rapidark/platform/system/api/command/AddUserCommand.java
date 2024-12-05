package com.rapidark.platform.system.api.command;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/21 10:56
 */
@Data
@Schema(description = "添加用户命令")
public class AddUserCommand {

    @Schema(name = "userName", title = "用户名", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "用户名不能为空")
    private String userName;

    @Schema(name = "password", title = "密码", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @Schema(name = "nickName", title = "昵称", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "昵称不能为空")
    private String nickName;

    @Schema(name = "status", title = "状态:0-禁用 1-正常 2-锁定", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(name = "userType", title = "用户类型:super-超级管理员 normal-普通管理员", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "用户类型不能为空")
    private String userType;

    @Schema(name = "email", title = "邮箱", example = "")
    private String email;

    @Schema(name = "mobile", title = "电话", example = "")
    private String mobile;

    @Schema(name = "userDesc", title = "描述", example = "")
    private String userDesc;

    @Schema(name = "avatar", title = "头像", example = "")
    private String avatar;

}
