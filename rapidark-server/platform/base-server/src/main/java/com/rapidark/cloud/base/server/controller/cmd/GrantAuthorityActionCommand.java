package com.rapidark.cloud.base.server.controller.cmd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/21 12:41
 */
@Data
@ApiModel(value = "功能接口授权")
public class GrantAuthorityActionCommand {

    @ApiModelProperty(name = "actionId", value = "功能按钮ID", example = "", required = true)
    @NotNull(message = "功能按钮ID不能为空")
    private Long actionId;

    @ApiModelProperty(name = "authorityIds", value = "接口ID:多个用,号隔开", example = "", required = true)
    @NotEmpty(message = "接口ID不能为空")
    private String authorityIds;

}
