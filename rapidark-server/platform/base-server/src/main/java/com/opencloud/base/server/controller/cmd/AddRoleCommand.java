package com.opencloud.base.server.controller.cmd;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/21 10:41
 */
@Data
@ApiModel(value = "创建角色命令")
public class AddRoleCommand {

    @ApiModelProperty(name = "roleCode", value = "角色编码", example = "", required = true)
    @NotNull
    private String roleCode;

    @ApiModelProperty(name = "roleName", value = "角色显示名称", example = "", required = true)
    @NotNull
    private String roleName;

    @ApiModelProperty(name = "roleDesc", value = "描述", example = "", required = false)
    private String roleDesc;

    @ApiModelProperty(name = "status", required = true, example = "1", allowableValues = "0,1", value = "是否启用")
    @NotNull
    private Integer status = 1;

}
