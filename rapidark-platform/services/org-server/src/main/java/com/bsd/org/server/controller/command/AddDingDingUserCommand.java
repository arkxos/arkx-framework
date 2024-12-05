package com.bsd.org.server.controller.command;

import com.rapidark.platform.system.api.command.AddUserCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/21 11:06
 */
@Getter
@Setter
@Schema(description = "添加钉钉用户命令")
public class AddDingDingUserCommand extends AddUserCommand {

    @Schema(name = "ddUserid", title = "用户钉钉ID", example = "", required = true)
    @NotEmpty(message = "用户钉钉ID不能为空")
    private String ddUserid;

}
