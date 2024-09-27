package com.rapidark.cloud.base.server.controller.cmd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/21 11:15
 */
@Data
@ApiModel(value = "添加用户角色命令")
public class AddUserRolesCommand {

    @ApiModelProperty(name = "userId", value = "用户Id", example = "", required = true)
    @NotNull(message = "用户Id不能为空")
    private Long userId;

    @ApiModelProperty(name = "roleIds", value = "角色id列表", example = "", required = true)
    @NotEmpty(message = "角色id列表不能为空")
    private String roleIds;

}
