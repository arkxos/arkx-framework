package com.rapidark.platform.system.web.rest;

import com.rapidark.cloud.platform.common.security.util.SecurityUtils;
import com.rapidark.framework.common.constants.CommonConstants;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.security.OpenHelper;
import com.rapidark.framework.common.security.OpenUserDetails;
import com.rapidark.framework.common.utils.StringUtils;
import com.rapidark.platform.system.api.entity.AuthorityMenu;
import com.rapidark.platform.system.api.entity.SysMenu;
import com.rapidark.platform.system.api.entity.SysUser;
import com.rapidark.platform.system.api.model.RouterVo;
import com.rapidark.platform.system.service.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author: liuyadu
 * @date: 2019/5/24 13:31
 * @description:
 */
@Schema(title = "当前登陆用户")
@AllArgsConstructor
@RestController
public class CurrentUserController {

	private final SysMenuService sysMenuService;
	private final RouterService routerService;

	private final SysUserService sysUserService;
	private final BaseAuthorityService baseAuthorityService;
	private final OpenAppService openAppService;
	//    @Autowired
//    private RedisTokenStore redisTokenStore;
	private final SysMenuService menuService;

	//	@Autowired
//	private final PasswordEncoder passwordEncoder;

	/**
	 * 返回当前用户的树形菜单集合
	 * @return 当前用户的树形菜单
	 */
	@GetMapping("/current/user/routers")
	public ResponseResult<List<RouterVo>> ueryCurrentUserMenu() {
		// 获取符合条件的菜单
		Set<com.rapidark.platform.system.api.entity.SysMenu> all = new HashSet<>();
		SecurityUtils.getRoles().forEach(roleId -> all.addAll(sysMenuService.findMenuByRoleId(roleId)));

		List<com.rapidark.platform.system.api.entity.SysMenu> menus = new ArrayList<>(all);
		menus.sort(Comparator.comparingLong(com.rapidark.platform.system.api.entity.SysMenu::getParentId).thenComparingInt(com.rapidark.platform.system.api.entity.SysMenu::getSortOrder));

		return ResponseResult.ok(routerService.buildRouters(menus));
	}

	/**
	 * 修改当前登录用户密码
	 *
	 * @return
	 */
	@Schema(title = "修改当前登录用户密码", name = "修改当前登录用户密码")
	@PostMapping("/current/user/rest/password")
	public ResponseResult restPassword(
			@RequestParam(value = "oldPassword") String oldPassword,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "confirmPassword") String confirmPassword
	) {
		OpenUserDetails user = OpenHelper.getUser();
		Assert.notNull(user, "登录过期，请重新登录");
		if (StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword) || !confirmPassword.equals(password)) {
			throw new OpenAlertException("新密码与确认密码不一致");
		}
//		if (StringUtils.isBlank(oldPassword) || !passwordEncoder.matches(oldPassword, user.getPassword())) {
//			throw new OpenAlertException("旧密码输入错误");
//		}
//		if (passwordEncoder.matches(password, user.getPassword())) {
//			throw new OpenAlertException("新密码与旧密码不能相同");
//		}
		sysUserService.updatePassword(user.getUserId(), password);
		return  ResponseResult.ok().msg("修改密码成功");
	}

	/**
	 * 修改当前登录用户基本信息
	 *
	 * @param nickName
	 * @param userDesc
	 * @param avatar
	 * @return
	 */
	@Schema(title = "修改当前登录用户基本信息", name = "修改当前登录用户基本信息")
	@PostMapping("/current/user/update")
	public ResponseResult updateUserInfo(
			@RequestParam(value = "nickName") String nickName,
			@RequestParam(value = "userDesc", required = false) String userDesc,
			@RequestParam(value = "avatar", required = false) String avatar
	) {
		OpenUserDetails openUserDetails = OpenHelper.getUser();
		Assert.notNull(openUserDetails, "登录过期，请重新登录");
		SysUser user = new SysUser();
		user.setUserId(openUserDetails.getUserId());
		user.setNickName(nickName);
		if (userDesc != null && !"".equals(userDesc)) {
			user.setUserDesc(userDesc);
		}
		if (avatar != null && !"".equals(avatar)) {
			user.setAvatar(avatar);
		}
		sysUserService.updateUser(user);
		openUserDetails.setNickName(nickName);
		openUserDetails.setAvatar(avatar);
//        OpenHelper.updateOpenUser(redisTokenStore, openUserDetails);
		return ResponseResult.ok();
	}

	/**
	 * 获取登陆用户已分配权限
	 *
	 * @return
	 */
	@Schema(title = "获取当前登录用户已分配菜单权限", name = "获取当前登录用户已分配菜单权限")
	@GetMapping("/current/user/menu")
	public ResponseResult<List<AuthorityMenu>> findAuthorityMenu(@RequestParam(value = "serviceId", required = false) String serviceId) {
		OpenUserDetails user = OpenHelper.getUser();
		Assert.notNull(user, "登录过期，请重新登录");
		if (StringUtils.isEmpty(serviceId)) {
			// modify, add search menu with serviceId(appNameEn)
			serviceId = openAppService.getAppClientInfo(user.getClientId()).getAdditionalInformation().get("appNameEn").toString();
		}
		serviceId = serviceId.trim();
		List<AuthorityMenu> result = baseAuthorityService.findAuthorityMenuByUser(
				user.getUserId(), CommonConstants.ROOT.equals(user.getUsername()), serviceId);
		return ResponseResult.ok(result);
	}

	/**
	 * 获取路由信息
	 *
	 * @return 路由信息
	 */
	@GetMapping("/current/user/routersx")
	public ResponseResult<List<RouterVo>> getRouters() {
		OpenUserDetails user = OpenHelper.getUser();
		List<AuthorityMenu> menus = baseAuthorityService.findAuthorityMenuByUser(
				user.getUserId(), CommonConstants.ROOT.equals(user.getUsername()), "");

		List<SysMenu> sysMenus = new ArrayList<>();
		for (AuthorityMenu menu : menus) {
			sysMenus.add(menu);
		}
		List<RouterVo> routers = null;//menuService.buildRouters(baseMenus);
		return ResponseResult.ok(routers);
	}

}
