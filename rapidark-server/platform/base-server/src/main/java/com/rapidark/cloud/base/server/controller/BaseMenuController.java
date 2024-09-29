package com.rapidark.cloud.base.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rapidark.cloud.base.client.model.entity.BaseAction;
import com.rapidark.cloud.base.client.model.entity.BaseMenu;
import com.rapidark.cloud.base.server.controller.cmd.CreateMenuCommand;
import com.rapidark.cloud.base.server.controller.cmd.DeleteMenuCommand;
import com.rapidark.cloud.base.server.controller.cmd.UpdateMenuCommand;
import com.rapidark.cloud.base.server.service.BaseActionService;
import com.rapidark.cloud.base.server.service.OpenAppService;
import com.rapidark.cloud.base.server.service.BaseMenuQuery;
import com.rapidark.cloud.base.server.service.BaseMenuService;
import com.rapidark.cloud.base.server.service.dto.OpenAppDto;
import com.rapidark.cloud.base.server.service.dto.OpenClientQueryCriteria;
import com.rapidark.common.model.PageParams;
import com.rapidark.common.model.ResultBody;
import com.rapidark.common.security.http.OpenRestTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liuyadu
 */
@Api(tags = "系统菜单资源管理")
@RestController
public class BaseMenuController {

    @Autowired
    private BaseMenuService baseResourceMenuService;
    @Autowired
    private BaseMenuQuery baseMenuQuery;

    @Autowired
    private BaseActionService baseResourceOperationService;

    @Autowired
    private OpenAppService openAppService;

    @Autowired
    private OpenRestTemplate openRestTemplate;

    /**
     * 所有服务列表
     *
     * @return
     */
    @ApiOperation(value = "所有服务列表", notes = "所有服务列表")
    @GetMapping("/menu/services")
    public ResultBody<List<JSONObject>> getServiceList() {
        List<OpenAppDto> apps = openAppService.queryAll(new OpenClientQueryCriteria());
        List<JSONObject> jsonList = new ArrayList<>();
        for (OpenAppDto app : apps) {
            JSONObject json = new JSONObject();
            json.put("serviceId", app.getAppNameEn());
            String serviceNameDisplay = app.getAppName() + "(" + app.getAppNameEn() + ")";
            if (app.getStatus() != 1) {
                serviceNameDisplay += "[未启用]";
            }
            json.put("serviceName", serviceNameDisplay);
            jsonList.add(json);
        }
        return ResultBody.ok().data(jsonList);
    }

    /**
     * 获取分页菜单资源列表
     *
     * @return
     */
    @ApiOperation(value = "获取分页菜单资源列表", notes = "获取分页菜单资源列表")
    @GetMapping("/menu")
    public ResultBody<IPage<BaseMenu>> getMenuListPage(@RequestParam(required = false) Map map) {
        return ResultBody.ok().data(baseResourceMenuService.findListPage(new PageParams(map)));
    }

    /**
     * 菜单所有资源列表
     *
     * @return
     */
    @ApiOperation(value = "菜单所有资源列表", notes = "菜单所有资源列表")
    @GetMapping("/menu/all")
    public ResultBody<List<BaseMenu>> getMenuAllList() {
        return ResultBody.ok().data(baseResourceMenuService.findAllList());
    }


    /**
     * 获取菜单下所有操作
     *
     * @param menuId
     * @return
     */
    @ApiOperation(value = "获取菜单下所有操作", notes = "获取菜单下所有操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuId", value = "menuId", paramType = "form"),
    })
    @GetMapping("/menu/action")
    public ResultBody<List<BaseAction>> getMenuAction(Long menuId) {
        return ResultBody.ok().data(baseResourceOperationService.findListByMenuId(menuId));
    }

    /**
     * 获取菜单资源详情
     *
     * @param menuId
     * @return 应用信息
     */
    @ApiOperation(value = "获取菜单资源详情", notes = "获取菜单资源详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuId", required = true, value = "menuId"),
    })
    @GetMapping("/menu/{menuId}/info")
    public ResultBody<BaseMenu> getMenu(@PathVariable("menuId") Long menuId) {
        return ResultBody.ok().data(baseMenuQuery.getMenu(menuId));
    }

    /**
     * 添加菜单资源
     *
     * @return
     */
    @ApiOperation(value = "添加菜单资源", notes = "添加菜单资源")
    @PostMapping("/menu/add")
    public ResultBody<Long> addMenu(@RequestBody CreateMenuCommand command) {
        BaseMenu menu = new BaseMenu();
        menu.setMenuCode(command.getMenuCode());
        menu.setMenuName(command.getMenuName());
        menu.setIcon(command.getIcon());
        menu.setPath(command.getPath());
        menu.setComponent(command.getComponent());
        menu.setScheme(command.getScheme());
        menu.setTarget(command.getTarget());
        menu.setStatus(command.getStatus());
        menu.setVisible(command.getVisible());
        menu.setParentId(command.getParentId());
        menu.setPriority(command.getPriority());
        menu.setMenuDesc(command.getMenuDesc());
        menu.setServiceId(command.getServiceId());
        Long menuId = null;
        BaseMenu result = baseResourceMenuService.addMenu(menu);
        if (result != null) {
            menuId = result.getMenuId();
        }
        return ResultBody.ok().data(menuId);
    }

    /**
     * 编辑菜单资源
     * @param command
     * @return
     */
    @ApiOperation(value = "编辑菜单资源", notes = "编辑菜单资源")
    @PostMapping("/menu/update")
    public ResultBody updateMenu(@RequestBody @Valid UpdateMenuCommand command) {
        BaseMenu menu = new BaseMenu();
        menu.setMenuId(command.getMenuId());
        menu.setMenuCode(command.getMenuCode());
        menu.setMenuName(command.getMenuName());
        menu.setIcon(command.getIcon());
        menu.setPath(command.getPath());
        menu.setComponent(command.getComponent());
        menu.setScheme(command.getScheme());
        menu.setTarget(command.getTarget());
        menu.setStatus(command.getStatus());
        menu.setVisible(command.getVisible());
        menu.setParentId(command.getParentId());
        menu.setPriority(command.getPriority());
        menu.setMenuDesc(command.getMenuDesc());
        menu.setServiceId(command.getServiceId());
        baseResourceMenuService.updateMenu(menu);
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }

    /**
     * 移除菜单资源
     *
     * @param menuId
     * @return
     */
    @ApiOperation(value = "移除菜单资源", notes = "移除菜单资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuId", required = true, value = "menuId", paramType = "form"),
    })
    @PostMapping("/menu/remove")
    public ResultBody<Boolean> removeMenu(@Valid @RequestBody DeleteMenuCommand command) {
        baseResourceMenuService.removeMenu(command.getMenuId());
        openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }
}
