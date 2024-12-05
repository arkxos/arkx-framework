package com.rapidark.cloud.base.server.controller;

import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.utils.PageResult;
import com.rapidark.framework.data.jpa.entity.Status;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.platform.system.api.entity.BaseAction;
import com.rapidark.platform.system.service.BaseActionService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author liuyadu
 */
@Schema(title = "系统功能按钮管理")
@RestController
public class BaseActionController {

    @Autowired
    private BaseActionService baseActionService;
//    @Autowired
//    private OpenRestTemplate openRestTemplate;

    /**
     * 获取分页功能按钮列表
     *
     * @return
     */
    @Schema(title = "获取分页功能按钮列表", name = "获取分页功能按钮列表")
    @GetMapping("/action")
    public ResponseResult<PageResult<BaseAction>> findActionListPage(@RequestParam(required = false) Map map) {
        return ResponseResult.ok(baseActionService.findListPage(new PageParams(map)));
    }


    /**
     * 获取功能按钮详情
     *
     * @param actionId
     * @return
     */
    @Schema(title = "获取功能按钮详情", name = "获取功能按钮详情")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "actionId", required = true, value = "功能按钮Id", paramType = "path"),
//    })
    @GetMapping("/action/{actionId}/info")
    public ResponseResult<BaseAction> getAction(@PathVariable("actionId") Long actionId) {
        return ResponseResult.ok(baseActionService.getAction(actionId));
    }

    /**
     * 添加功能按钮
     *
     * @param actionCode 功能按钮编码
     * @param actionName 功能按钮名称
     * @param menuId     上级菜单
     * @param status     是否启用
     * @param priority   优先级越小越靠前
     * @param actionDesc 描述
     * @return
     */
    @Schema(title = "添加功能按钮", name = "添加功能按钮")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "actionCode", required = true, value = "功能按钮编码", paramType = "form"),
//            @ApiImplicitParam(name = "actionName", required = true, value = "功能按钮名称", paramType = "form"),
//            @ApiImplicitParam(name = "menuId", required = true, value = "上级菜单", paramType = "form"),
//            @ApiImplicitParam(name = "status", required = true, defaultValue = "1", allowableValues = "0,1", value = "是否启用", paramType = "form"),
//            @ApiImplicitParam(name = "priority", required = false, value = "优先级越小越靠前", paramType = "form"),
//            @ApiImplicitParam(name = "actionDesc", required = false, value = "描述", paramType = "form"),
//    })
    @PostMapping("/action/add")
    public ResponseResult<Long> addAction(
            @RequestParam(value = "actionCode") String actionCode,
            @RequestParam(value = "actionName") String actionName,
            @RequestParam(value = "menuId") Long menuId,
            @RequestParam(value = "status", defaultValue = "1") Integer status,
            @RequestParam(value = "priority", required = false, defaultValue = "0") Integer priority,
            @RequestParam(value = "actionDesc", required = false, defaultValue = "") String actionDesc
    ) {
        BaseAction action = new BaseAction();
        action.setActionCode(actionCode);
        action.setActionName(actionName);
        action.setMenuId(menuId);
        action.setStatus(Status.codeOf(status));
        action.setPriority(priority);
        action.setActionDesc(actionDesc);
        Long actionId = null;
        BaseAction result = baseActionService.addAction(action);
        if (result != null) {
            actionId = result.getActionId();
//            // openRestTemplate.refreshGateway();
        }
        return ResponseResult.ok(actionId);
    }

    /**
     * 编辑功能按钮
     *
     * @param actionId   功能按钮ID
     * @param actionCode 功能按钮编码
     * @param actionName 功能按钮名称
     * @param menuId     上级菜单
     * @param status     是否启用
     * @param priority   优先级越小越靠前
     * @param actionDesc 描述
     * @return
     */
    @Schema(title = "编辑功能按钮", name = "添加功能按钮")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "actionId", required = true, value = "功能按钮ID", paramType = "form"),
//            @ApiImplicitParam(name = "actionCode", required = true, value = "功能按钮编码", paramType = "form"),
//            @ApiImplicitParam(name = "actionName", required = true, value = "功能按钮名称", paramType = "form"),
//            @ApiImplicitParam(name = "menuId", required = true, value = "上级菜单", paramType = "form"),
//            @ApiImplicitParam(name = "status", required = true, defaultValue = "1", allowableValues = "0,1", value = "是否启用", paramType = "form"),
//            @ApiImplicitParam(name = "priority", required = false, value = "优先级越小越靠前", paramType = "form"),
//            @ApiImplicitParam(name = "actionDesc", required = false, value = "描述", paramType = "form"),
//    })
    @PostMapping("/action/update")
    public ResponseResult updateAction(
            @RequestParam("actionId") Long actionId,
            @RequestParam(value = "actionCode") String actionCode,
            @RequestParam(value = "actionName") String actionName,
            @RequestParam(value = "menuId") Long menuId,
            @RequestParam(value = "status", defaultValue = "1") Integer status,
            @RequestParam(value = "priority", required = false, defaultValue = "0") Integer priority,
            @RequestParam(value = "actionDesc", required = false, defaultValue = "") String actionDesc
    ) {
        BaseAction action = new BaseAction();
        action.setActionId(actionId);
        action.setActionCode(actionCode);
        action.setActionName(actionName);
        action.setMenuId(menuId);
        action.setStatus(Status.codeOf(status));
        action.setPriority(priority);
        action.setActionDesc(actionDesc);
        baseActionService.updateAction(action);
        // 刷新网关
//        // openRestTemplate.refreshGateway();
        return ResponseResult.ok();
    }


    /**
     * 移除功能按钮
     *
     * @param actionId
     * @return
     */
    @Schema(title = "移除功能按钮", name = "移除功能按钮")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "actionId", required = true, value = "功能按钮ID", paramType = "form")
//    })
    @PostMapping("/action/remove")
    public ResponseResult removeAction(
            @RequestParam("actionId") Long actionId
    ) {
        baseActionService.removeAction(actionId);
        // 刷新网关
//        // openRestTemplate.refreshGateway();
        return ResponseResult.ok();
    }
}
