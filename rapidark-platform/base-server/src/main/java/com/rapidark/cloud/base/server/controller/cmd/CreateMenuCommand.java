package com.rapidark.cloud.base.server.controller.cmd;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/21 15:21
 */
@Schema(description = "创建菜单命令")
@Data
public class CreateMenuCommand {

    @Schema(required = true, title = "菜单编码")
    @NotNull(message = "菜单编码不能为空")
    private String menuCode;
    @Schema(required = true, title = "菜单名称")
    @NotNull(message = "菜单名称不能为空")
    private String menuName;
    @Schema(required = false, title = "图标")
    private String icon;
    @Schema(required = false, title = "请求协议", allowableValues = "/,http://,https://", example = "/")
    private String scheme = "/";
    @Schema(required = true, title = "路由地址")
    private String path = "";
    @Schema(required = false, title = "组件路径")
    private String component = "";
    @Schema(required = false, title = "打开方式", allowableValues = "_self,_blank", example = "_self")
    private String target="_self";
    @Schema(required = true, allowableValues = "0,1", example="1", title = "是否启用")
    private Integer status = 1;
    @Schema(required = true, allowableValues = "0,1", example="1", title = "菜单可见")
    private Integer visible = 1;
    @Schema(required = false, title = "父节点ID", example = "0")
    private Long parentId = 0L;
    @Schema(required = false, title = "优先级（越小越靠前）")
    private Integer priority = 0;
    @Schema(required = false, title = "描述")
    private String menuDesc = "";
    @Schema(required = false, title = "前端应用")
    private String serviceId = "";

}
