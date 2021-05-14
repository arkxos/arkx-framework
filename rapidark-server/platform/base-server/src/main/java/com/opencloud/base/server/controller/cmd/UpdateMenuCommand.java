package com.opencloud.base.server.controller.cmd;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/13 18:22
 */
@Data
public class UpdateMenuCommand {

    /**
     * @param menuCode 菜单编码
     *      * @param menuName 菜单名称
     *      * @param icon     图标
     *      * @param scheme   请求前缀
     *      * @param path     请求路径
     *      * @param target   打开方式
     *      * @param status   是否启用
     *      * @param parentId 父节点ID
     *      * @param priority 优先级越小越靠前
     *      * @param menuDesc 描述
     */
    @NotNull(message = "菜单id不能为空")
    private Long menuId;
    @NotNull(message = "菜单编码不能为空")
    private String menuCode;
    @NotNull(message = "菜单名称不能为空")
    private String menuName;
    private String icon;
    private String scheme = "/";
    private String path = "";
    private String target="_self";
    private Integer status = 1;
    private Long parentId = 0L;
    private Integer priority = 0;
    private String menuDesc = "";
    private String serviceId = "";

}
