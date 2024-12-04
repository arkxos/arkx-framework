package com.rapidark.cloud.base.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rapidark.cloud.base.client.constants.ResourceType;
import com.rapidark.cloud.base.client.constants.UserConstants;
import com.rapidark.cloud.base.client.model.entity.SysMenu;
import com.rapidark.cloud.base.server.repository.SysMenuRepository;
import com.rapidark.cloud.platform.common.core.constant.CommonConstants;
import com.rapidark.cloud.platform.common.core.constant.enums.MenuTypeEnum;
import com.rapidark.cloud.platform.common.core.exception.ErrorCodes;
import com.rapidark.cloud.platform.common.core.util.MsgUtils;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.framework.common.utils.CriteriaQueryWrapper;
import com.rapidark.platform.system.api.entity.SysRoleAuthority;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 菜单资源管理
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SysMenuService extends BaseService<SysMenu, Long, SysMenuRepository> {

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
    public Page<SysMenu> findListPage(PageParams pageParams) {
        SysMenu query = pageParams.mapToObject(SysMenu.class);
        CriteriaQueryWrapper<SysMenu> queryWrapper = new CriteriaQueryWrapper();
        queryWrapper
                .likeRight(ObjectUtils.isNotEmpty(query.getCode()), SysMenu::getCode, query.getCode())
                .likeRight(ObjectUtils.isNotEmpty(query.getName()), SysMenu::getName, query.getName());
        Pageable pageable = PageRequest.of(pageParams.getPage(), pageParams.getLimit());
        return findAllByCriteria(queryWrapper, pageable);
    }

    /**
     * 查询列表
     *
     * @return
     */
    public List<SysMenu> findAllList() {
        //根据优先级从小到大排序
        List<SysMenu> list = entityRepository.findAll(Sort.by(Sort.Direction.ASC, "priority"));
        return list;
    }

    /**
     * 检查菜单编码是否存在
     *
     * @param menuCode
     * @return
     */
    public Boolean isExist(String menuCode) {
        SysMenu queryWrapper = new SysMenu();
        queryWrapper.setCode(menuCode);
        long count = count(queryWrapper);
        return count > 0 ? true : false;
    }

    /**
     * 添加菜单资源
     *
     * @param menu
     * @return
     */
    public SysMenu addMenu(SysMenu menu) {
        if (isExist(menu.getCode())) {
            throw new OpenAlertException(String.format("%s编码已存在!", menu.getCode()));
        }
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getPriority() == null) {
            menu.setPriority(0);
        }
//        if (menu.getStatus() == null) {
//            menu.setStatus(1);
//        }
//        if (menu.getIsPersist() == null) {
//            menu.setIsPersist(0);
//        }
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
    public SysMenu updateMenu(SysMenu menu) {
        SysMenu saved = findById(menu.getMenuId());
        if (saved == null) {
            throw new OpenAlertException(String.format("%s信息不存在!", menu.getMenuId()));
        }
        if (!saved.getCode().equals(menu.getCode())) {
            // 和原来不一致重新检查唯一性
            if (isExist(menu.getCode())) {
                throw new OpenAlertException(String.format("%s编码已存在!", menu.getCode()));
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
        SysMenu menu = findById(menuId);
//        if (menu != null && menu.getIsPersist().equals(BaseConstants.ENABLED)) {
//            throw new OpenAlertException(String.format("保留数据,不允许删除!"));
//        }
        // 移除菜单权限
        baseAuthorityService.removeAuthority(menuId, ResourceType.menu);
        // 移除功能按钮和相关权限
        baseActionService.removeByMenuId(menuId);
        // 移除菜单信息
        deleteById(menuId);
    }

	/**
	 * 通过角色编号查询URL 权限
	 * @param roleId 角色ID
	 * @return 菜单列表
	 */
//	@Override
//	@Cacheable(value = CacheConstants.MENU_DETAILS, key = "#roleId", unless = "#result.isEmpty()")
	public List<SysMenu> findMenuByRoleId(Long roleId) {
		return entityRepository.queryMenusByRoleId(roleId);
	}

	/**
	 * 级联删除菜单
	 * @param id 菜单ID
	 * @return 成功、失败
	 */
//	@Override
	@Transactional(rollbackFor = Exception.class)
//	@CacheEvict(value = CacheConstants.MENU_DETAILS, allEntries = true)
	public void removeMenuById(Long id) {
		// 查询父节点为当前节点的节点
		SysMenu example = new SysMenu();
		example.setParentId(id);
		List<SysMenu> children = this.findAll(example);

		if (CollUtil.isNotEmpty(children)) {
			throw new RuntimeException(MsgUtils.getMessage(ErrorCodes.SYS_MENU_DELETE_EXISTING));
		}

		sysRoleMenuMapper.delete(Wrappers.<SysRoleAuthority>query().lambda().eq(SysRoleAuthority::getAuthorityId, id));
		// 删除当前菜单及其子菜单
		this.deleteById(id);
	}

	/**
	 * 更新菜单信息
	 * @param sysMenu 菜单信息
	 * @return 成功、失败
	 */
//	@Override
//	@CacheEvict(value = CacheConstants.MENU_DETAILS, allEntries = true)
	public void updateMenuById(SysMenu sysMenu) {
		this.update(sysMenu);
	}

	/**
	 * 构建树查询 1. 不是懒加载情况，查询全部 2. 是懒加载，根据parentId 查询 2.1 父节点为空，则查询ID -1
	 * @param parentId 父节点ID
	 * @param menuName 菜单名称
	 * @return
	 */
//	@Override
	public List<Tree<Long>> treeMenu(Long parentId, String menuName, String type) {
		Long parent = parentId == null ? CommonConstants.MENU_TREE_ROOT_ID : parentId;

		List<SysMenu> menus = entityRepository.queryByNameAndType(menuName, type);
		List<TreeNode<Long>> collect = menus
				.stream()
				.map(getNodeFunction())
				.collect(Collectors.toList());

		// 模糊查询 不组装树结构 直接返回 表格方便编辑
		if (StrUtil.isNotBlank(menuName)) {
			return collect.stream().map(node -> {
				Tree<Long> tree = new Tree<>();
				tree.putAll(node.getExtra());
				BeanUtils.copyProperties(node, tree);
				return tree;
			}).collect(Collectors.toList());
		}

		return TreeUtil.build(collect, parent);
	}

	/**
	 * 查询菜单
	 * @param all 全部菜单
	 * @param type 类型
	 * @param parentId 父节点ID
	 * @return
	 */
//	@Override
	public List<Tree<Long>> filterMenu(Set<SysMenu> all, String type, Long parentId) {
		List<TreeNode<Long>> collect = all.stream()
				.filter(menuTypePredicate(type))
				.map(getNodeFunction())
				.collect(Collectors.toList());

		Long parent = parentId == null ? CommonConstants.MENU_TREE_ROOT_ID : parentId;
		return TreeUtil.build(collect, parent);
	}

	@NotNull
	private Function<SysMenu, TreeNode<Long>> getNodeFunction() {
		return menu -> {
			TreeNode<Long> node = new TreeNode<>();
			node.setId(menu.getMenuId());
			node.setName(menu.getName());
			node.setParentId(menu.getParentId());
			node.setWeight(menu.getSortOrder());
			// 扩展属性
			Map<String, Object> extra = new HashMap<>();
			extra.put("path", menu.getPath());
			extra.put("menuType", menu.getMenuType());
			extra.put("permission", menu.getPermission());
			extra.put("sortOrder", menu.getSortOrder());

			// 适配 vue3
			Map<String, Object> meta = new HashMap<>();
			meta.put("title", menu.getName());
			meta.put("isLink", menu.getPath() != null && menu.getPath().startsWith("http") ? menu.getPath() : "");
			meta.put("isHide", menu.getVisible() == 0);
			meta.put("isKeepAlive", menu.getKeepAlive() == 1);
			meta.put("isAffix", false);
			meta.put("isIframe", menu.getIntegrateMode() == UserConstants.INTEGRATE_MODE_FRAME);
			meta.put("icon", menu.getIcon());
			// 增加英文
			meta.put("enName", menu.getCode());

			extra.put("meta", meta);
			node.setExtra(extra);
			return node;
		};
	}

	/**
	 * menu 类型断言
	 * @param type 类型
	 * @return Predicate
	 */
	private Predicate<SysMenu> menuTypePredicate(String type) {
		return vo -> {
			if (MenuTypeEnum.TOP_MENU.getDescription().equals(type)) {
				return MenuTypeEnum.TOP_MENU.getType().equals(vo.getMenuType());
			}
			// 其他查询 左侧 + 顶部
			return !MenuTypeEnum.BUTTON.getType().equals(vo.getMenuType());
		};
	}

}
