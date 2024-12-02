package com.rapidark.cloud.base.server.controller.cmd;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/21 15:21
 */
@Schema(description = "删除菜单命令")
@Data
public class DeleteMenuCommand {

    @Schema(required = true, title = "菜单编号")
    @NotNull(message = "菜单编号不能为空")
    private Long menuId;

}
