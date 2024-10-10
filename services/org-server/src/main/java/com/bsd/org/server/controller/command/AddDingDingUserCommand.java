package com.bsd.org.server.controller.command;

import com.rapidark.cloud.base.client.service.command.AddUserCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/21 11:06
 */
@Getter
@Setter
@ApiModel(value = "添加钉钉用户命令")
public class AddDingDingUserCommand extends AddUserCommand {

    @ApiModelProperty(name = "ddUserid", value = "用户钉钉ID", example = "", required = true)
    @NotEmpty(message = "用户钉钉ID不能为空")
    private String ddUserid;

}
