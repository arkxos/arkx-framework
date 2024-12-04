package com.rapidark.cloud.platform.admin.application.service;

import com.rapidark.cloud.platform.admin.api.entity.SysMenu;
import com.rapidark.cloud.platform.admin.api.model.MetaVo;
import com.rapidark.cloud.platform.admin.api.model.RouterVo;
import com.rapidark.framework.common.utils.StreamUtils;
import com.rapidark.framework.common.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouterService {

	public List<RouterVo> buildRouters(List<SysMenu> menus) {
		List<SysMenu> treeMenus = getChildPerms(menus, 0);
		return buildMenus(treeMenus);
	}

	/**
	 * 构建前端路由所需要的菜单
	 * 路由name命名规则 path首字母转大写 + id
	 *
	 * @param menus 菜单列表
	 * @return 路由列表
	 */
	public List<RouterVo> buildMenus(List<SysMenu> menus) {
		String TYPE_APP = "0";
//		String TYPE_DIR = "1";

		List<RouterVo> routers = new LinkedList<>();
		for (SysMenu menu : menus) {
			if("1".equals(menu.getMenuType())) {
				continue;// 按钮
			}
			String name = menu.getEnName() + menu.getMenuId();
			RouterVo router = new RouterVo();
			router.setAppCode(menu.getAppCode());
			router.setHidden("0".equals(menu.getVisible()));
			router.setName(name);
			router.setPath(menu.getPath());
			router.setComponent(menu.getComponent());
			router.setQuery(menu.getQueryParam());

			MetaVo metaVo = new MetaVo(menu.getName(), menu.getIcon(), "0".equals(menu.getKeepAlive()), menu.getPath());
			if("1".equals(menu.getEmbedded())) {
//
//			}
//			if(UserConstants.INTEGRATE_MODE_FRAME.equals(menu.getIntegrateMode())) {
				metaVo.setType("iframe");
				router.setName("iframe" + menu.getMenuId());
			}
			router.setMeta(metaVo);
			List<SysMenu> cMenus = menu.getChildren();
			if (!cMenus.isEmpty() && TYPE_APP.equals(menu.getMenuType())) {
//				router.setAlwaysShow(true);
//				router.setRedirect("noRedirect");
				router.setChildren(buildMenus(cMenus));
			}
//			else if (CollUtil.isNotEmpty(cMenus) && TYPE_DIR.equals(menu.getMenuType())) {
//				router.setAlwaysShow(true);
//				router.setRedirect("noRedirect");
//				router.setChildren(buildMenus(cMenus));
//			}
//			else if (menu.isMenuFrame()) {
			if("1".equals(menu.getEmbedded())) {
				String frameName = StringUtils.capitalize(menu.getPath()) + menu.getMenuId();
				router.setMeta(null);
				List<RouterVo> childrenList = new ArrayList<>();
				RouterVo children = new RouterVo();
				children.setPath(menu.getPath());
				children.setComponent(menu.getComponent());
				children.setName(frameName);
				children.setMeta(new MetaVo(menu.getName(), menu.getIcon(), "0".equals(menu.getKeepAlive()), menu.getPath()));
				children.setQuery(menu.getQueryParam());
				childrenList.add(children);
				router.setChildren(childrenList);
			}
//			else if (menu.getParentId().intValue() == 0 && menu.isInnerLink()) {
//				router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
//				router.setPath("/");
//				List<RouterVo> childrenList = new ArrayList<>();
//				RouterVo children = new RouterVo();
//				String routerPath = BaseMenu.innerLinkReplaceEach(menu.getPath());
//				String innerLinkName = StringUtils.capitalize(routerPath) + menu.getMenuId();
//				children.setPath(routerPath);
//				children.setComponent(UserConstants.INNER_LINK);
//				children.setName(innerLinkName);
//				children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
//				childrenList.add(children);
//				router.setChildren(childrenList);
//			}
			routers.add(router);
		}
		return routers;
	}

	/**
	 * 根据父节点的ID获取所有子节点
	 *
	 * @param list     分类表
	 * @param parentId 传入的父节点ID
	 * @return String
	 */
	private List<SysMenu> getChildPerms(List<SysMenu> list, int parentId) {
		List<SysMenu> returnList = new ArrayList<>();
		for (SysMenu t : list) {
			// 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
			if (t.getParentId() == parentId) {
				recursionFn(list, t);
				returnList.add(t);
			}
		}
		return returnList;
	}

	/**
	 * 递归列表
	 */
	private void recursionFn(List<SysMenu> list, SysMenu t) {
		// 得到子节点列表
		List<SysMenu> childList = StreamUtils.filter(list, n -> n.getParentId().equals(t.getMenuId()));
		childList.sort(Comparator.comparingInt(SysMenu::getSortOrder));
		t.setChildren(childList);
		for (SysMenu tChild : childList) {
			// 判断是否有子节点
			if (list.stream().anyMatch(n -> n.getParentId().equals(tChild.getMenuId()))) {
				recursionFn(list, tChild);
			}
		}
	}

}
