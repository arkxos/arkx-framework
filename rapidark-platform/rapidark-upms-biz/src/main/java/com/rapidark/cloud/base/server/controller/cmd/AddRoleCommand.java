package com.rapidark.cloud.base.server.controller.cmd;



import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/21 10:41
 */
@Data
@Schema(description = "创建角色命令")
public class AddRoleCommand {

    @Schema(name = "roleCode", title = "角色编码", example = "", required = true)
    @NotNull
    private String roleCode;

    @Schema(name = "roleName", title = "角色显示名称", example = "", required = true)
    @NotNull
    private String roleName;

    @Schema(name = "roleDesc", title = "描述", example = "", required = false)
    private String roleDesc;

    @Schema(name = "status", required = true, example = "1", allowableValues = "0,1", title = "是否启用")
    @NotNull
    private Integer status = 1;

}
