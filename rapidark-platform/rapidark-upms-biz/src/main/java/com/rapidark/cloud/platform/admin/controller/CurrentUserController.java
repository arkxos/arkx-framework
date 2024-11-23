package com.rapidark.cloud.platform.admin.controller;

import com.rapidark.cloud.platform.admin.api.entity.SysMenu;
import com.rapidark.cloud.platform.admin.application.model.RouterVo;
import com.rapidark.cloud.platform.admin.application.service.RouterService;
import com.rapidark.cloud.platform.admin.service.SysMenuService;
import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.common.security.util.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@AllArgsConstructor
@RestController
public class CurrentUserController {

	private final SysMenuService sysMenuService;
	private final RouterService routerService;

	/**
	 * 返回当前用户的树形菜单集合
	 * @return 当前用户的树形菜单
	 */
	@GetMapping("/current/user/routers")
	public ResponseResult<List<RouterVo>> ueryCurrentUserMenu() {
		// 获取符合条件的菜单
		Set<SysMenu> all = new HashSet<>();
		SecurityUtils.getRoles().forEach(roleId -> all.addAll(sysMenuService.findMenuByRoleId(roleId)));

		List<SysMenu> menus = new ArrayList<>(all);
		menus.sort(Comparator.comparingLong(SysMenu::getParentId).thenComparingInt(SysMenu::getSortOrder));

		return ResponseResult.ok(routerService.buildRouters(menus));
	}

}
