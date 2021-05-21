package com.opencloud.base.server.controller.cmd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/13 18:22
 */
@ApiModel(value = "修改菜单命令")
@Data
public class UpdateMenuCommand extends CreateMenuCommand {

    @ApiModelProperty(required = true, value = "菜单ID")
    @NotNull(message = "菜单id不能为空")
    private Long menuId;

}
