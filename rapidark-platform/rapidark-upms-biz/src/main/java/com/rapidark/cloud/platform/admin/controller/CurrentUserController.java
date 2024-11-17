package com.rapidark.cloud.platform.admin.controller;

import cn.hutool.core.lang.tree.Tree;
import com.rapidark.cloud.platform.admin.api.entity.SysMenu;
import com.rapidark.cloud.platform.admin.application.service.RouterService;
import com.rapidark.cloud.platform.admin.service.SysMenuService;
import com.rapidark.cloud.platform.common.core.util.R;
import com.rapidark.cloud.platform.common.security.util.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public R ueryCurrentUserMenu() {
		// 获取符合条件的菜单
		Set<SysMenu> all = new HashSet<>();
		SecurityUtils.getRoles().forEach(roleId -> all.addAll(sysMenuService.findMenuByRoleId(roleId)));

		List<SysMenu> menus = new ArrayList<>();
		menus.addAll(all);

		return R.ok(routerService.buildRouters(menus));
	}

}
