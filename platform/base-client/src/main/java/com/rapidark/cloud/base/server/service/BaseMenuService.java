package com.rapidark.cloud.base.server.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.base.client.constants.ResourceType;
import com.rapidark.cloud.base.client.constants.UserConstants;
import com.rapidark.cloud.base.client.model.entity.BaseMenu;
import com.rapidark.cloud.base.server.repository.BaseMenuRepository;
import com.rapidark.cloud.base.server.service.dto.MetaVo;
import com.rapidark.cloud.base.server.service.dto.RouterVo;
import com.rapidark.cloud.gateway.formwork.base.BaseService;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.common.model.PageParams;
import com.rapidark.framework.common.utils.CriteriaQueryWrapper;
import com.rapidark.framework.common.utils.StreamUtils;
import com.rapidark.framework.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 菜单资源管理
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseMenuService extends BaseService<BaseMenu, Long, BaseMenuRepository> {

    @Autowired
    private BaseAuthorityService baseAuthorityService;

    @Autowired
    private BaseActionService baseActionService;

    @Value("${spring.application.name}")
    private String DEFAULT_SERVICE_ID;

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    public Page<BaseMenu> findListPage(PageParams pageParams) {
        BaseMenu query = pageParams.mapToObject(BaseMenu.class);
        CriteriaQueryWrapper<BaseMenu> queryWrapper = new CriteriaQueryWrapper();
        queryWrapper
                .likeRight(ObjectUtils.isNotEmpty(query.getMenuCode()), BaseMenu::getMenuCode, query.getMenuCode())
                .likeRight(ObjectUtils.isNotEmpty(query.getMenuName()), BaseMenu::getMenuName, query.getMenuName());
        Pageable pageable = PageRequest.of(pageParams.getPage(), pageParams.getLimit());
        return findAllByCriteria(queryWrapper, pageable);
    }

    /**
     * 查询列表
     *
     * @return
     */
    public List<BaseMenu> findAllList() {
        //根据优先级从小到大排序
        List<BaseMenu> list = entityRepository.findAll(Sort.by(Sort.Direction.ASC, "priority"));
        return list;
    }



    /**
     * 检查菜单编码是否存在
     *
     * @param menuCode
     * @return
     */
    public Boolean isExist(String menuCode) {
        BaseMenu queryWrapper = new BaseMenu();
        queryWrapper.setMenuCode(menuCode);
        long count = count(queryWrapper);
        return count > 0 ? true : false;
    }

    /**
     * 添加菜单资源
     *
     * @param menu
     * @return
     */
    public BaseMenu addMenu(BaseMenu menu) {
        if (isExist(menu.getMenuCode())) {
            throw new OpenAlertException(String.format("%s编码已存在!", menu.getMenuCode()));
        }
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getPriority() == null) {
            menu.setPriority(0);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        if (menu.getIsPersist() == null) {
            menu.setIsPersist(0);
        }
        // check service id
        if (menu.getServiceId() == null || "".equals(menu.getServiceId())) {
            menu.setServiceId(DEFAULT_SERVICE_ID);
        }
        menu.setCreateTime(LocalDateTime.now());
        menu.setUpdateTime(menu.getCreateTime());
        save(menu);
        // 同步权限表里的信息
        baseAuthorityService.saveOrUpdateAuthority(menu.getMenuId(), ResourceType.menu);
        return menu;
    }

    /**
     * 修改菜单资源
     *
     * @param menu
     * @return
     */
    public BaseMenu updateMenu(BaseMenu menu) {
        BaseMenu saved = findById(menu.getMenuId());
        if (saved == null) {
            throw new OpenAlertException(String.format("%s信息不存在!", menu.getMenuId()));
        }
        if (!saved.getMenuCode().equals(menu.getMenuCode())) {
            // 和原来不一致重新检查唯一性
            if (isExist(menu.getMenuCode())) {
                throw new OpenAlertException(String.format("%s编码已存在!", menu.getMenuCode()));
            }
        }
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getPriority() == null) {
            menu.setPriority(0);
        }
        menu.setUpdateTime(LocalDateTime.now());
        save(menu);
        // 同步权限表里的信息
        baseAuthorityService.saveOrUpdateAuthority(menu.getMenuId(), ResourceType.menu);
        return menu;
    }


    /**
     * 移除菜单
     *
     * @param menuId
     * @return
     */
    public void removeMenu(Long menuId) {
        BaseMenu menu = findById(menuId);
        if (menu != null && menu.getIsPersist().equals(BaseConstants.ENABLED)) {
            throw new OpenAlertException(String.format("保留数据,不允许删除!"));
        }
        // 移除菜单权限
        baseAuthorityService.removeAuthority(menuId, ResourceType.menu);
        // 移除功能按钮和相关权限
        baseActionService.removeByMenuId(menuId);
        // 移除菜单信息
        deleteById(menuId);
    }

    public List<RouterVo> buildRouters(List<BaseMenu> menus) {
        List<BaseMenu> treeMenus = getChildPerms(menus, 0);
        return buildMenus(treeMenus);
    }

    /**
     * 构建前端路由所需要的菜单
     * 路由name命名规则 path首字母转大写 + id
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<RouterVo> buildMenus(List<BaseMenu> menus) {
        List<RouterVo> routers = new LinkedList<>();
        for (BaseMenu menu : menus) {
            String name = menu.getRouteName() + menu.getMenuId();
            RouterVo router = new RouterVo();
            router.setAppCode(menu.getAppCode());
            router.setHidden(menu.getVisible() == 0);
            router.setName(name);
            router.setPath(menu.getRouterPath());
            router.setComponent(menu.getComponentInfo());
            router.setQuery(menu.getQueryParam());

            MetaVo metaVo = new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getIsCache() == 0, menu.getPath());
            if(UserConstants.INTEGRATE_MODE_FRAME.equals(menu.getIntegrateMode())) {
                metaVo.setType("iframe");
                router.setName("iframe" + menu.getMenuId());
            }
            router.setMeta(metaVo);
            List<BaseMenu> cMenus = menu.getChildren();
            if (!cMenus.isEmpty() && UserConstants.TYPE_APP.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (CollUtil.isNotEmpty(cMenus) && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (menu.isMenuFrame()) {
                String frameName = StringUtils.capitalize(menu.getPath()) + menu.getMenuId();
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(frameName);
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getIsCache() == 0, menu.getPath()));
                children.setQuery(menu.getQueryParam());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().intValue() == 0 && menu.isInnerLink()) {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                String routerPath = BaseMenu.innerLinkReplaceEach(menu.getPath());
                String innerLinkName = StringUtils.capitalize(routerPath) + menu.getMenuId();
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(innerLinkName);
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
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
    private List<BaseMenu> getChildPerms(List<BaseMenu> list, int parentId) {
        List<BaseMenu> returnList = new ArrayList<>();
        for (BaseMenu t : list) {
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
    private void recursionFn(List<BaseMenu> list, BaseMenu t) {
        // 得到子节点列表
        List<BaseMenu> childList = StreamUtils.filter(list, n -> n.getParentId().equals(t.getMenuId()));
        t.setChildren(childList);
        for (BaseMenu tChild : childList) {
            // 判断是否有子节点
            if (list.stream().anyMatch(n -> n.getParentId().equals(tChild.getMenuId()))) {
                recursionFn(list, tChild);
            }
        }
    }

}
