package com.rapidark.cloud.base.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.rapidark.cloud.base.client.service.dto.OpenAppDto;
import com.rapidark.cloud.base.client.service.dto.OpenClientQueryCriteria;
import com.rapidark.cloud.base.server.controller.cmd.CreateMenuCommand;
import com.rapidark.cloud.base.server.controller.cmd.UpdateMenuCommand;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.utils.PageResult;
import com.rapidark.framework.data.jpa.entity.Status;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.platform.system.api.entity.BaseAction;
import com.rapidark.platform.system.api.entity.SysMenu;
import com.rapidark.platform.system.service.BaseActionService;
import com.rapidark.platform.system.service.OpenAppService;
import com.rapidark.platform.system.service.SysMenuQuery;
import com.rapidark.platform.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liuyadu
 */
@Schema(title = "系统菜单资源管理")
@RestController
@RequestMapping("/basemenu")
public class BaseMenuController {

    @Autowired
    private SysMenuService baseResourceMenuService;
    @Autowired
    private SysMenuQuery sysMenuQuery;

    @Autowired
    private BaseActionService baseResourceOperationService;

    @Autowired
    private OpenAppService openAppService;

//    @Autowired
//    private OpenRestTemplate openRestTemplate;

    /**
     * 所有服务列表
     *
     * @return
     */
    @Schema(title = "所有服务列表", name = "所有服务列表")
    @GetMapping("/menu/services")
    public ResponseResult<List<JSONObject>> getServiceList() {
        List<OpenAppDto> apps = openAppService.queryAll(new OpenClientQueryCriteria());
        List<JSONObject> jsonList = new ArrayList<>();
        for (OpenAppDto app : apps) {
            JSONObject json = new JSONObject();
            json.put("serviceId", app.getAppNameEn());
            String serviceNameDisplay = app.getAppName() + "(" + app.getAppNameEn() + ")";
            if (app.getStatus() != Status.ENABLED) {
                serviceNameDisplay += "[未启用]";
            }
            json.put("serviceName", serviceNameDisplay);
            jsonList.add(json);
        }
        return ResponseResult.ok(jsonList);
    }

    /**
     * 获取分页菜单资源列表
     *
     * @return
     */
    @Schema(title = "获取分页菜单资源列表", name = "获取分页菜单资源列表")
    @GetMapping("/menu")
    public ResponseResult<PageResult<SysMenu>> getMenuListPage(@RequestParam(required = false) Map map) {
        return ResponseResult.ok(baseResourceMenuService.findListPage(new PageParams(map)));
    }

    /**
     * 菜单所有资源列表
     *
     * @return
     */
    @Schema(title = "菜单所有资源列表", name = "菜单所有资源列表")
    @GetMapping("/menu/all")
    public ResponseResult<List<SysMenu>> getMenuAllList() {
        return ResponseResult.ok(baseResourceMenuService.findAllList());
    }


    /**
     * 获取菜单下所有操作
     *
     * @param menuId
     * @return
     */
    @Schema(title = "获取菜单下所有操作", name = "获取菜单下所有操作")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "menuId", value = "menuId", paramType = "form"),
//    })
    @GetMapping("/menu/action")
    public ResponseResult<List<BaseAction>> getMenuAction(Long menuId) {
        return ResponseResult.ok(baseResourceOperationService.findListByMenuId(menuId));
    }

    /**
     * 获取菜单资源详情
     *
     * @param menuId
     * @return 应用信息
     */
    @Schema(title = "获取菜单资源详情", name = "获取菜单资源详情")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "menuId", required = true, value = "menuId"),
//    })
    @GetMapping("/menu/{menuId}/info")
    public ResponseResult<SysMenu> getMenu(@PathVariable("menuId") Long menuId) {
        return ResponseResult.ok(sysMenuQuery.getMenu(menuId));
    }

    /**
     * 添加菜单资源
     *
     * @return
     */
    @Schema(title = "添加菜单资源", name = "添加菜单资源")
    @PostMapping("/menu/add")
    public ResponseResult<Long> addMenu(@RequestBody CreateMenuCommand command) {
        SysMenu menu = new SysMenu();
        menu.setCode(command.getMenuCode());
        menu.setName(command.getMenuName());
        menu.setIcon(command.getIcon());
        menu.setPath(command.getPath());
        menu.setComponent(command.getComponent());
        menu.setScheme(command.getScheme());
        menu.setIntegrateMode(command.getIntegrateMode());
        menu.setStatus(Status.codeOf(command.getStatus()));
        menu.setVisible(command.getVisible());
        menu.setParentId(command.getParentId());
        menu.setPriority(command.getPriority());
        menu.setMenuDesc(command.getMenuDesc());
        menu.setServiceId(command.getServiceId());
        Long menuId = null;
        SysMenu result = baseResourceMenuService.addMenu(menu);
        if (result != null) {
            menuId = result.getMenuId();
        }
        return ResponseResult.ok(menuId);
    }

    /**
     * 编辑菜单资源
     * @param command
     * @return
     */
    @Schema(title = "编辑菜单资源", name = "编辑菜单资源")
    @PostMapping("/menu/update")
    public ResponseResult updateMenu(@RequestBody @Valid UpdateMenuCommand command) {
        SysMenu menu = new SysMenu();
        menu.setMenuId(command.getMenuId());
        menu.setCode(command.getMenuCode());
        menu.setName(command.getMenuName());
        menu.setIcon(command.getIcon());
        menu.setPath(command.getPath());
        menu.setComponent(command.getComponent());
        menu.setScheme(command.getScheme());
        menu.setIntegrateMode(command.getIntegrateMode());
        menu.setStatus(Status.codeOf(command.getStatus()));
        menu.setVisible(command.getVisible());
        menu.setParentId(command.getParentId());
        menu.setPriority(command.getPriority());
        menu.setMenuDesc(command.getMenuDesc());
        menu.setServiceId(command.getServiceId());
        baseResourceMenuService.updateMenu(menu);
        // openRestTemplate.refreshGateway();
        return ResponseResult.ok();
    }

    /**
     * 移除菜单资源
     *
     * @param menuId
     * @return
     */
    @Schema(title = "移除菜单资源", name = "移除菜单资源")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "menuId", required = true, value = "menuId", paramType = "form"),
//    })
    @PostMapping("/menu/remove")
    public ResponseResult<Boolean> removeMenu(@RequestParam("menuId") Long menuId) {
        baseResourceMenuService.removeMenu(menuId);
        // openRestTemplate.refreshGateway();
        return ResponseResult.ok();
    }
}
