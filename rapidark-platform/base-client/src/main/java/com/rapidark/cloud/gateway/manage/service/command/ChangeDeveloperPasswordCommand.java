package com.rapidark.cloud.gateway.manage.service.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/27 16:53
 */
@Data
@Schema(description = "修改开发商密码参数")
public class ChangeDeveloperPasswordCommand {

    @Schema(name = "userId", value = "用户Id", example = "", required = true)
    @NotNull(message = "用户Id不能为空")
    private Long userId;

    @Schema(name = "password", value = "密码", example = "", required = true)
    @NotEmpty(message = "密码不能为空")
    private String password;

}
