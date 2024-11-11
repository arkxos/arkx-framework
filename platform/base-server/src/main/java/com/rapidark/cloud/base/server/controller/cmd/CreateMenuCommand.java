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
@ApiModel(value = "创建菜单命令")
@Data
public class CreateMenuCommand {

    @ApiModelProperty(required = true, value = "菜单编码")
    @NotNull(message = "菜单编码不能为空")
    private String menuCode;
    @ApiModelProperty(required = true, value = "菜单名称")
    @NotNull(message = "菜单名称不能为空")
    private String menuName;
    @ApiModelProperty(required = false, value = "图标")
    private String icon;
    @ApiModelProperty(required = false, value = "请求协议", allowableValues = "/,http://,https://", example = "/")
    private String scheme = "/";
    @ApiModelProperty(required = true, value = "路由地址")
    private String path = "";
    @ApiModelProperty(required = false, value = "组件路径")
    private String component = "";
    @ApiModelProperty(required = false, value = "打开方式", allowableValues = "_self,_blank", example = "_self")
    private String target="_self";
    @ApiModelProperty(required = true, allowableValues = "0,1", example="1", value = "是否启用")
    private Integer status = 1;
    @ApiModelProperty(required = true, allowableValues = "0,1", example="1", value = "菜单可见")
    private Integer visible = 1;
    @ApiModelProperty(required = false, value = "父节点ID", example = "0")
    private Long parentId = 0L;
    @ApiModelProperty(required = false, value = "优先级（越小越靠前）")
    private Integer priority = 0;
    @ApiModelProperty(required = false, value = "描述")
    private String menuDesc = "";
    @ApiModelProperty(required = false, value = "前端应用")
    private String serviceId = "";

}
