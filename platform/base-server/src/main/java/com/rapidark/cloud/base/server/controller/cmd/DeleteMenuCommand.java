package com.rapidark.cloud.base.server.controller.cmd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/21 15:21
 */
@ApiModel(value = "删除菜单命令")
@Data
public class DeleteMenuCommand {

    @ApiModelProperty(required = true, value = "菜单编号")
    @NotNull(message = "菜单编号不能为空")
    private Long menuId;

}
