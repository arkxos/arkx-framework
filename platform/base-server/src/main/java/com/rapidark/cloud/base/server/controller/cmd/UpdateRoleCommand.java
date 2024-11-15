package com.rapidark.cloud.base.server.controller.cmd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/21 10:41
 */
@Data
@Schema(description = "修改角色命令")
public class UpdateRoleCommand {

    @Schema( name = "roleId", value = "角色ID", example = "", required = true)
    @NotNull
    private Long roleId;

    @Schema( name = "roleCode", value = "角色编码", example = "", required = true)
    @NotNull
    private String roleCode;

    @Schema( name = "roleName", value = "角色显示名称", example = "", required = true)
    @NotNull
    private String roleName;

    @Schema( name = "roleDesc", value = "描述", example = "", required = false)
    private String roleDesc;

    @Schema( name = "status", required = true, example = "1", allowableValues = "0,1", value = "是否启用")
    @NotNull
    private Integer status = 1;

}
