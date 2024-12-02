package com.rapidark.cloud.base.server.controller.cmd;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/13 18:22
 */
@Schema(description = "修改菜单命令")
@Data
public class UpdateMenuCommand extends CreateMenuCommand {

    @Schema(required = true, title = "菜单ID")
    @NotNull(message = "菜单id不能为空")
    private Long menuId;

}
