package com.rapidark.platform.system.api.command;

import io.swagger.v3.oas.annotations.media.Schema;

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

    @Schema(name = "userId", title = "用户Id", example = "", required = true)
    @NotNull(message = "用户Id不能为空")
    private Long userId;

    @Schema(name = "password", title = "密码", example = "", required = true)
    @NotEmpty(message = "密码不能为空")
    private String password;

}
