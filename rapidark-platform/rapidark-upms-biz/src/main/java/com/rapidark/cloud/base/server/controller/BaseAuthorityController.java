package com.rapidark.cloud.base.server.controller;

import com.rapidark.cloud.base.client.model.AuthorityApi;
import com.rapidark.cloud.base.client.model.AuthorityMenu;
import com.rapidark.cloud.base.client.model.AuthorityResource;
import com.rapidark.cloud.base.client.model.entity.BaseAuthorityAction;
import com.rapidark.cloud.base.client.model.entity.BaseUser;
import com.rapidark.cloud.base.client.service.IBaseAuthorityServiceClient;
import com.rapidark.cloud.base.server.controller.cmd.GrantAuthorityActionCommand;
import com.rapidark.cloud.base.server.controller.cmd.GrantOpenClientAppApiAuthorityCommand;
import com.rapidark.cloud.base.server.service.BaseAuthorityService;
import com.rapidark.cloud.base.server.service.BaseUserService;
import com.rapidark.framework.common.constants.CommonConstants;
import com.rapidark.framework.common.model.ResultBody;
import com.rapidark.framework.common.security.OpenAuthority;
//import com.rapidark.framework.common.security.http.OpenRestTemplate;
import com.rapidark.framework.common.utils.StringUtils;



import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @author: liuyadu
 * @date: 2018/11/26 18:20
 * @description:
 */
@Schema(title = "系统权限管理")
@RestController
public class BaseAuthorityController implements IBaseAuthorityServiceClient {

    @Autowired
    private BaseAuthorityService baseAuthorityService;
    @Autowired
    private BaseUserService baseUserService;
//    @Autowired
//    private OpenRestTemplate openRestTemplate;

    /**
     * 获取所有访问权限列表
     *
     * @return
     */
    @Schema(title = "获取所有访问权限列表", name = "获取所有访问权限列表")
    @GetMapping("/authority/access")
    @Override
    public ResultBody<List<AuthorityResource>> findAuthorityResource() {
        List<AuthorityResource> result = baseAuthorityService.findAuthorityResource();
        return ResultBody.ok(result);
    }

    /**
     * 获取权限列表
     *
     * @return
     */
    @Schema(title = "获取接口权限列表", name = "获取接口权限列表")
    @GetMapping("/authority/api")
    public ResultBody<List<AuthorityApi>> findAuthorityApi(
            @RequestParam(value = "serviceId", required = false) String serviceId
    ) {
        List<AuthorityApi> result = baseAuthorityService.findAuthorityApi(serviceId);
        return ResultBody.ok(result);
    }


    /**
     * 获取菜单权限列表
     *
     * @return
     */
    @Schema(title = "获取菜单权限列表", name = "获取菜单权限列表")
    @GetMapping("/authority/menu")
    @Override
    public ResultBody<List<AuthorityMenu>> findAuthorityMenu() {
        List<AuthorityMenu> result = baseAuthorityService.findAuthorityMenu(1, null);
        return ResultBody.ok(result);
    }

    /**
     * 获取功能权限列表
     *
     * @param actionId
     * @return
     */
    @Schema(title = "获取功能权限列表", name = "获取功能权限列表")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "actionId", required = true, value = "功能按钮ID", paramType = "form")
//    })
    @GetMapping("/authority/action")
    public ResultBody<List<BaseAuthorityAction>> findAuthorityAction(
            @RequestParam(value = "actionId") Long actionId
    ) {
        List<BaseAuthorityAction> list = baseAuthorityService.findAuthorityAction(actionId);
        return ResultBody.ok(list);
    }


    /**
     * 获取角色已分配权限
     *
     * @param roleId 角色ID
     * @return
     */
    @Schema(title = "获取角色已分配权限", name = "获取角色已分配权限")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "roleId", value = "角色ID", defaultValue = "", required = true, paramType = "form")
//    })
    @GetMapping("/authority/role")
    public ResultBody<List<OpenAuthority>> findAuthorityRole(Long roleId) {
        List<OpenAuthority> result = baseAuthorityService.findAuthorityByRole(roleId);
        return ResultBody.ok(result);
    }


    /**
     * 获取用户已分配权限
     *
     * @param userId 用户ID
     * @return
     */
    @Schema(title = "获取用户已分配权限", name = "获取用户已分配权限")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId", value = "用户ID", defaultValue = "", required = true, paramType = "form")
//    })
    @GetMapping("/authority/user")
    public ResultBody<List<OpenAuthority>> findAuthorityUser(
            @RequestParam(value = "userId") Long userId
    ) {
        BaseUser user = baseUserService.getUserById(userId);
        List<OpenAuthority> result = baseAuthorityService.findAuthorityByUser(userId, CommonConstants.ROOT.equals(user.getUserName()));
        return ResultBody.ok(result);
    }


    /**
     * 获取应用已分配接口权限
     *
     * @param appId 角色ID
     * @return
     */
    @Schema(title = "获取应用已分配接口权限", name = "获取应用已分配接口权限")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "appId", value = "应用Id", defaultValue = "", required = true, paramType = "form")
//    })
    @GetMapping("/authority/app")
    public ResultBody<List<OpenAuthority>> findAuthorityApp(
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "appSystemCode") String appSystemCode
    ) {
        List<OpenAuthority> result = baseAuthorityService.findAuthorityByApp(appId, appSystemCode);
        return ResultBody.ok(result);
    }

    /**
     * 分配角色权限
     *
     * @param roleId       角色ID
     * @param expireTime   授权过期时间
     * @param authorityIds 权限ID.多个以,隔开
     * @return
     */
    @Schema(title = "分配角色权限", name = "分配角色权限")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "roleId", value = "角色ID", defaultValue = "", required = true, paramType = "form"),
//            @ApiImplicitParam(name = "expireTime", value = "过期时间.选填", defaultValue = "", required = false, paramType = "form"),
//            @ApiImplicitParam(name = "authorityIds", value = "权限ID.多个以,隔开.选填", defaultValue = "", required = false, paramType = "form")
//    })
    @PostMapping("/authority/role/grant")
    public ResultBody grantAuthorityRole(
            @RequestParam(value = "roleId") Long roleId,
            @RequestParam(value = "expireTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date expireTime,
            @RequestParam(value = "authorityIds", required = false) String authorityIds
    ) {
        baseAuthorityService.addAuthorityRole(roleId, expireTime, StringUtils.isNotBlank(authorityIds) ? authorityIds.split(",") : new String[]{});
        // openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }


    /**
     * 分配用户权限
     *
     * @param userId       用户ID
     * @param expireTime   授权过期时间
     * @param authorityIds 权限ID.多个以,隔开
     * @return
     */
    @Schema(title = "分配用户权限", name = "分配用户权限")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId", value = "用户ID", defaultValue = "", required = true, paramType = "form"),
//            @ApiImplicitParam(name = "expireTime", value = "过期时间.选填", defaultValue = "", required = false, paramType = "form"),
//            @ApiImplicitParam(name = "authorityIds", value = "权限ID.多个以,隔开.选填", defaultValue = "", required = false, paramType = "form")
//    })
    @PostMapping("/authority/user/grant")
    public ResultBody grantAuthorityUser(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "expireTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date expireTime,
            @RequestParam(value = "authorityIds", required = false) String authorityIds
    ) {
        baseAuthorityService.addAuthorityUser(userId, expireTime, StringUtils.isNotBlank(authorityIds) ? authorityIds.split(",") : new String[]{});
        // openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }


    /**
     * 分配应用权限
     *
     * @return
     */
    @Schema(title = "分配应用权限", name = "分配应用权限")
    @PostMapping("/authority/app/grant")
    public ResultBody grantAuthorityApp(@Valid @RequestBody GrantOpenClientAppApiAuthorityCommand command) {
        baseAuthorityService
                .addAuthorityApp(command.getAppId(), command.getAppSystemCode(),
                        command.getExpireTime(), command.getAuthorityIds().split(","));
        // openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }

    /**
     * 功能按钮绑定API
     *
     * @return
     */
    @Schema(title = "功能按钮授权", name = "功能按钮授权")
    @PostMapping("/authority/action/grant")
    public ResultBody grantAuthorityAction(@Valid @RequestBody GrantAuthorityActionCommand command) {
        baseAuthorityService.addAuthorityAction(command.getActionId(), StringUtils.isNotBlank(command.getAuthorityIds()) ? command.getAuthorityIds().split(",") : new String[]{});
        // openRestTemplate.refreshGateway();
        return ResultBody.ok();
    }
}
