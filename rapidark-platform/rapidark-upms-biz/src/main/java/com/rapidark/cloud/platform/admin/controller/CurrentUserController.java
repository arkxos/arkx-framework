package com.rapidark.cloud.platform.admin.controller;

import com.rapidark.cloud.base.client.model.AuthorityMenu;
import com.rapidark.cloud.base.client.model.entity.BaseMenu;
import com.rapidark.cloud.base.client.model.entity.BaseUser;
import com.rapidark.cloud.base.server.service.BaseAuthorityService;
import com.rapidark.cloud.base.server.service.BaseMenuService;
import com.rapidark.cloud.base.server.service.BaseUserService;
import com.rapidark.cloud.base.server.service.OpenAppService;
import com.rapidark.cloud.platform.admin.api.entity.SysMenu;
import com.rapidark.cloud.platform.admin.application.model.RouterVo;
import com.rapidark.cloud.platform.admin.application.service.RouterService;
import com.rapidark.cloud.platform.admin.service.SysMenuService;
import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.common.security.util.SecurityUtils;
import com.rapidark.framework.common.constants.CommonConstants;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.common.model.ResultBody;
import com.rapidark.framework.common.security.OpenHelper;
import com.rapidark.framework.common.security.OpenUserDetails;
import com.rapidark.framework.common.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

	@Autowired
	private BaseUserService baseUserService;
	@Autowired
	private BaseAuthorityService baseAuthorityService;
	@Autowired
	private OpenAppService openAppService;
	//    @Autowired
//    private RedisTokenStore redisTokenStore;
	@Autowired
	private BaseMenuService menuService;

//	@Autowired
	private PasswordEncoder passwordEncoder;

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

	/**
	 * 修改当前登录用户密码
	 *
	 * @return
	 */
	@Schema(title = "修改当前登录用户密码", name = "修改当前登录用户密码")
	@PostMapping("/current/user/rest/password")
	public ResultBody restPassword(
			@RequestParam(value = "oldPassword") String oldPassword,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "confirmPassword") String confirmPassword
	) {
		OpenUserDetails user = OpenHelper.getUser();
		Assert.notNull(user, "登录过期，请重新登录");
		if (StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword) || !confirmPassword.equals(password)) {
			throw new OpenAlertException("新密码与确认密码不一致");
		}
		if (StringUtils.isBlank(oldPassword) || !passwordEncoder.matches(oldPassword, user.getPassword())) {
			throw new OpenAlertException("旧密码输入错误");
		}
		if (passwordEncoder.matches(password, user.getPassword())) {
			throw new OpenAlertException("新密码与旧密码不能相同");
		}
		baseUserService.updatePassword(user.getUserId(), password);
		return  ResultBody.ok().msg("修改密码成功");
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
	public ResultBody updateUserInfo(
			@RequestParam(value = "nickName") String nickName,
			@RequestParam(value = "userDesc", required = false) String userDesc,
			@RequestParam(value = "avatar", required = false) String avatar
	) {
		OpenUserDetails openUserDetails = OpenHelper.getUser();
		Assert.notNull(openUserDetails, "登录过期，请重新登录");
		BaseUser user = new BaseUser();
		user.setUserId(openUserDetails.getUserId());
		user.setNickName(nickName);
		if (userDesc != null && !"".equals(userDesc)) {
			user.setUserDesc(userDesc);
		}
		if (avatar != null && !"".equals(avatar)) {
			user.setAvatar(avatar);
		}
		baseUserService.updateUser(user);
		openUserDetails.setNickName(nickName);
		openUserDetails.setAvatar(avatar);
//        OpenHelper.updateOpenUser(redisTokenStore, openUserDetails);
		return ResultBody.ok();
	}

	/**
	 * 获取登陆用户已分配权限
	 *
	 * @return
	 */
	@Schema(title = "获取当前登录用户已分配菜单权限", name = "获取当前登录用户已分配菜单权限")
	@GetMapping("/current/user/menu")
	public ResultBody<List<AuthorityMenu>> findAuthorityMenu(@RequestParam(value = "serviceId", required = false) String serviceId) {
		OpenUserDetails user = OpenHelper.getUser();
		Assert.notNull(user, "登录过期，请重新登录");
		if (StringUtils.isEmpty(serviceId)) {
			// modify, add search menu with serviceId(appNameEn)
			serviceId = openAppService.getAppClientInfo(user.getClientId()).getAdditionalInformation().get("appNameEn").toString();
		}
		serviceId = serviceId.trim();
		List<AuthorityMenu> result = baseAuthorityService.findAuthorityMenuByUser(
				user.getUserId(), CommonConstants.ROOT.equals(user.getUsername()), serviceId);
		return ResultBody.ok(result);
	}

	/**
	 * 获取路由信息
	 *
	 * @return 路由信息
	 */
	@GetMapping("/current/user/routersx")
	public ResultBody<List<RouterVo>> getRouters() {
		OpenUserDetails user = OpenHelper.getUser();
		List<AuthorityMenu> menus = baseAuthorityService.findAuthorityMenuByUser(
				user.getUserId(), CommonConstants.ROOT.equals(user.getUsername()), "");

		List<BaseMenu> baseMenus = new ArrayList<>();
		for (AuthorityMenu menu : menus) {
			baseMenus.add(menu);
		}
		List<RouterVo> routers = null;//menuService.buildRouters(baseMenus);
		return ResultBody.ok(routers);
	}

}
