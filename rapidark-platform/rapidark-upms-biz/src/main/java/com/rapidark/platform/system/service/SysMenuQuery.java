package com.rapidark.platform.system.service;

import com.rapidark.platform.system.repository.SysMenuRepository;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.platform.system.api.entity.SysMenu;
import org.springframework.stereotype.Service;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/5/13 21:42
 */
@Service
public class SysMenuQuery extends BaseService<SysMenu, Long, SysMenuRepository> {

    /**
     * 根据主键获取菜单
     *
     * @param menuId
     * @return
     */
    public SysMenu getMenu(Long menuId) {
        return findById(menuId);
    }

}
