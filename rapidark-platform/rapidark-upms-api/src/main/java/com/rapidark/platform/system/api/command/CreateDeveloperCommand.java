package com.rapidark.platform.system.api.command;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/27 17:25
 */
@Data
@Schema(description = "创建开发商参数")
public class CreateDeveloperCommand {

    /**
     * 开发者类型:isp-服务提供商 dev-自研开发者
     */
    @Schema(name = "type", title = "公司类型", example = "", required = true)
    @NotNull(message = "公司类型不能为空")
    private Integer type;

    @Schema(name = "companyName", title = "公司名称", example = "", required = true)
    @NotEmpty(message = "公司名称不能为空")
    private String companyName;

    @Schema(name = "personName", title = "联系人", example = "", required = true)
    @NotNull(message = "联系人不能为空")
    private String personName;

    @Schema(name = "mobile", title = "手机号", example = "", required = true)
    @NotNull(message = "手机号不能为空")
    private String mobile;

    @Schema(name = "userName", title = "用户名", example = "", required = true)
    @NotEmpty(message = "用户名不能为空")
    private String userName;

    @Schema(name = "password", title = "密码", example = "", required = true)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @Schema(name = "companyId", title = "企业ID", example = "", required = true)
    private Long companyId;

    @Schema(name = "nickName", title = "昵称", example = "", required = true)
    @NotNull(message = "昵称不能为空")
    private String nickName;

    @Schema(name = "avatar", title = "头像", example = "", required = true)
    private String avatar;

    @Schema(name = "email", title = "邮箱", example = "", required = true)
    private String email;

    @Schema(name = "userDesc", title = "描述", example = "", required = true)
    private String userDesc;

    @Schema(name = "status", title = "状态:0-禁用 1-正常 2-锁定", example = "", required = true)
    @NotNull(message = "状态不能为空")
    private Integer status;

}
