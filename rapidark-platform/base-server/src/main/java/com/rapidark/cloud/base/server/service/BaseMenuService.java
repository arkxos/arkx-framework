package com.rapidark.cloud.base.server.service;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rapidark.cloud.base.client.constants.ResourceType;
import com.rapidark.cloud.base.client.model.entity.BaseMenu;
import com.rapidark.cloud.base.server.repository.BaseMenuRepository;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.common.model.PageParams;
import com.rapidark.framework.common.utils.CriteriaQueryWrapper;
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

}
