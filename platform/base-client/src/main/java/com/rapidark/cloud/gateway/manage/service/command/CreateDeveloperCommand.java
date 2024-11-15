package com.rapidark.cloud.gateway.manage.service.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
    @Schema( name = "type", value = "公司类型", example = "", required = true)
    @NotNull(message = "公司类型不能为空")
    private Integer type;

    @Schema( name = "companyName", value = "公司名称", example = "", required = true)
    @NotEmpty(message = "公司名称不能为空")
    private String companyName;

    @Schema( name = "personName", value = "联系人", example = "", required = true)
    @NotNull(message = "联系人不能为空")
    private String personName;

    @Schema( name = "mobile", value = "手机号", example = "", required = true)
    @NotNull(message = "手机号不能为空")
    private String mobile;

    @Schema( name = "userName", value = "用户名", example = "", required = true)
    @NotEmpty(message = "用户名不能为空")
    private String userName;

    @Schema( name = "password", value = "密码", example = "", required = true)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @Schema( name = "companyId", value = "企业ID", example = "", required = true)
    private Long companyId;

    @Schema( name = "nickName", value = "昵称", example = "", required = true)
    @NotNull(message = "昵称不能为空")
    private String nickName;

    @Schema( name = "avatar", value = "头像", example = "", required = true)
    private String avatar;

    @Schema( name = "email", value = "邮箱", example = "", required = true)
    private String email;

    @Schema( name = "userDesc", value = "描述", example = "", required = true)
    private String userDesc;

    @Schema( name = "status", value = "状态:0-禁用 1-正常 2-锁定", example = "", required = true)
    @NotNull(message = "状态不能为空")
    private Integer status;

}
