package com.rapidark.cloud.uaa.admin.server.controller.cmd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/25 15:37
 */
@Data
@ApiModel(value = "退出登录参数")
public class LogoutCommand {

    @ApiModelProperty(required = true, value = "令牌")
    @NotNull(message = "令牌不能为空")
    private String token;

}
