package com.rapidark.cloud.base.server.controller;

import com.rapidark.cloud.base.server.controller.cmd.AddRoleCommand;
import com.rapidark.cloud.base.server.controller.cmd.UpdateRoleCommand;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.utils.PageResult;
import com.rapidark.framework.common.utils.StringUtils;
import com.rapidark.framework.data.jpa.entity.Status;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.platform.system.api.entity.SysRole;
import com.rapidark.platform.system.api.entity.SysUserRole;
import com.rapidark.platform.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author liuyadu
 */
@Schema(title = "系统角色管理")
@RestController
public class BaseRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 获取分页角色列表
     *
     * @return
     */
    @Schema(title = "获取分页角色列表", name = "获取分页角色列表")
    @GetMapping("/role")
    public ResponseResult<PageResult<SysRole>> getRoleListPage(@RequestParam(required = false) Map map) {
        return ResponseResult.ok(sysRoleService.findListPage(new PageParams(map)));
    }

    /**
     * 获取所有角色列表
     *
     * @return
     */
    @Schema(title = "获取所有角色列表", name = "获取所有角色列表")
    @GetMapping("/role/all")
    public ResponseResult<List<SysRole>> getRoleAllList() {
        return ResponseResult.ok(sysRoleService.findAllList());
    }

    /**
     * 获取角色详情
     *
     * @param roleId
     * @return
     */
    @Schema(title = "获取角色详情", name = "获取角色详情")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "roleId", value = "角色ID", defaultValue = "", required = true, paramType = "path")
//    })
    @GetMapping("/role/{roleId}/info")
    public ResponseResult<SysRole> getRole(@PathVariable(value = "roleId") Long roleId) {
        SysRole result = sysRoleService.getRole(roleId);
        return ResponseResult.ok(result);
    }

    /**
     * 添加角色
     *
     * @return
     */
    @Schema(title = "添加角色", name = "添加角色")
    @PostMapping("/role/add")
    public ResponseResult<Long> addRole(@RequestBody @Valid AddRoleCommand command) {
        SysRole role = new SysRole();
        role.setRoleCode(command.getRoleCode());
        role.setRoleName(command.getRoleName());
        role.setStatus(Status.codeOf(command.getStatus()));
        role.setRoleDesc(command.getRoleDesc());
        Long roleId = null;
        SysRole result = sysRoleService.addRole(role);
        if (result != null) {
            roleId = result.getRoleId();
        }
        return ResponseResult.ok(roleId);
    }

    /**
     * 编辑角色
     *
     * @return
     */
    @Schema(title = "编辑角色", name = "编辑角色")
    @PostMapping("/role/update")
    public ResponseResult updateRole(@Valid @RequestBody UpdateRoleCommand command) {
        SysRole role = new SysRole();
        role.setRoleId(command.getRoleId());
        role.setRoleCode(command.getRoleCode());
        role.setRoleName(command.getRoleName());
        role.setStatus(Status.codeOf(command.getStatus()));
        role.setRoleDesc(command.getRoleDesc());
        sysRoleService.updateRole(role);
        return ResponseResult.ok();
    }

    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    @Schema(title = "删除角色", name = "删除角色")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "roleId", value = "角色ID", defaultValue = "", required = true, paramType = "form")
//    })
    @PostMapping("/role/remove")
    public ResponseResult removeRole(
            @RequestParam(value = "roleId") Long roleId
    ) {
        sysRoleService.removeRole(roleId);
        return ResponseResult.ok();
    }

    /**
     * 角色添加成员
     *
     * @param roleId
     * @param userIds
     * @return
     */
    @Schema(title = "角色添加成员", name = "角色添加成员")
    @PostMapping("/role/users/add")
    public ResponseResult addUserRoles(
            @RequestParam(value = "roleId") Long roleId,
            @RequestParam(value = "userIds", required = false) String userIds
    ) {
        sysRoleService.saveRoleUsers(roleId, StringUtils.isNotBlank(userIds) ? userIds.split(",") : new String[]{});
        return ResponseResult.ok();
    }

    /**
     * 查询角色成员
     *
     * @param roleId
     * @return
     */
    @Schema(title = "查询角色成员", name = "查询角色成员")
    @GetMapping("/role/users")
    public ResponseResult<List<SysUserRole>> getRoleUsers(@RequestParam(value = "roleId", required = false) String roleId,
														  @RequestParam(value = "roleCode", required = false) String roleCode) {
        if (roleId == null && StringUtils.isEmpty(roleCode)) {
            return ResponseResult.failed("查询参数角色ID与角色编码不能同时为空");
        }
        return ResponseResult.ok(sysRoleService.findRoleUsersByRoleIdOrRoleCode(roleId, roleCode));
    }
}
