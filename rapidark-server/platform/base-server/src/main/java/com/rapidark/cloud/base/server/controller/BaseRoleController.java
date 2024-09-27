package com.rapidark.cloud.base.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rapidark.cloud.base.client.model.entity.BaseRole;
import com.rapidark.cloud.base.client.model.entity.BaseRoleUser;
import com.rapidark.cloud.base.server.controller.cmd.AddRoleCommand;
import com.rapidark.cloud.base.server.controller.cmd.UpdateRoleCommand;
import com.rapidark.cloud.base.server.service.BaseRoleService;
import com.rapidark.common.model.PageParams;
import com.rapidark.common.model.ResultBody;
import com.rapidark.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author liuyadu
 */
@Api(tags = "系统角色管理")
@RestController
public class BaseRoleController {
    @Autowired
    private BaseRoleService baseRoleService;

    /**
     * 获取分页角色列表
     *
     * @return
     */
    @ApiOperation(value = "获取分页角色列表", notes = "获取分页角色列表")
    @GetMapping("/role")
    public ResultBody<IPage<BaseRole>> getRoleListPage(@RequestParam(required = false) Map map) {
        return ResultBody.ok().data(baseRoleService.findListPage(new PageParams(map)));
    }

    /**
     * 获取所有角色列表
     *
     * @return
     */
    @ApiOperation(value = "获取所有角色列表", notes = "获取所有角色列表")
    @GetMapping("/role/all")
    public ResultBody<List<BaseRole>> getRoleAllList() {
        return ResultBody.ok().data(baseRoleService.findAllList());
    }

    /**
     * 获取角色详情
     *
     * @param roleId
     * @return
     */
    @ApiOperation(value = "获取角色详情", notes = "获取角色详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色ID", defaultValue = "", required = true, paramType = "path")
    })
    @GetMapping("/role/{roleId}/info")
    public ResultBody<BaseRole> getRole(@PathVariable(value = "roleId") Long roleId) {
        BaseRole result = baseRoleService.getRole(roleId);
        return ResultBody.ok().data(result);
    }

    /**
     * 添加角色
     *
     * @return
     */
    @ApiOperation(value = "添加角色", notes = "添加角色")
    @PostMapping("/role/add")
    public ResultBody<Long> addRole(@RequestBody @Valid AddRoleCommand command) {
        BaseRole role = new BaseRole();
        role.setRoleCode(command.getRoleCode());
        role.setRoleName(command.getRoleName());
        role.setStatus(command.getStatus());
        role.setRoleDesc(command.getRoleDesc());
        Long roleId = null;
        BaseRole result = baseRoleService.addRole(role);
        if (result != null) {
            roleId = result.getRoleId();
        }
        return ResultBody.ok().data(roleId);
    }

    /**
     * 编辑角色
     *
     * @return
     */
    @ApiOperation(value = "编辑角色", notes = "编辑角色")
    @PostMapping("/role/update")
    public ResultBody updateRole(@Valid @RequestBody UpdateRoleCommand command) {
        BaseRole role = new BaseRole();
        role.setRoleId(command.getRoleId());
        role.setRoleCode(command.getRoleCode());
        role.setRoleName(command.getRoleName());
        role.setStatus(command.getStatus());
        role.setRoleDesc(command.getRoleDesc());
        baseRoleService.updateRole(role);
        return ResultBody.ok();
    }

    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    @ApiOperation(value = "删除角色", notes = "删除角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色ID", defaultValue = "", required = true, paramType = "form")
    })
    @PostMapping("/role/remove")
    public ResultBody removeRole(
            @RequestParam(value = "roleId") Long roleId
    ) {
        baseRoleService.removeRole(roleId);
        return ResultBody.ok();
    }

    /**
     * 角色添加成员
     *
     * @param roleId
     * @param userIds
     * @return
     */
    @ApiOperation(value = "角色添加成员", notes = "角色添加成员")
    @PostMapping("/role/users/add")
    public ResultBody addUserRoles(
            @RequestParam(value = "roleId") Long roleId,
            @RequestParam(value = "userIds", required = false) String userIds
    ) {
        baseRoleService.saveRoleUsers(roleId, StringUtils.isNotBlank(userIds) ? userIds.split(",") : new String[]{});
        return ResultBody.ok();
    }

    /**
     * 查询角色成员
     *
     * @param roleId
     * @return
     */
    @ApiOperation(value = "查询角色成员", notes = "查询角色成员")
    @GetMapping("/role/users")
    public ResultBody<List<BaseRoleUser>> getRoleUsers(@RequestParam(value = "roleId", required = false) Long roleId,
                                                       @RequestParam(value = "roleCode", required = false) String roleCode) {
        if (roleId == null && StringUtils.isEmpty(roleCode)) {
            return ResultBody.failed().msg("查询参数角色ID与角色编码不能同时为空");
        }
        return ResultBody.ok().data(baseRoleService.findRoleUsersByRoleIdOrRoleCode(roleId, roleCode));
    }
}
