package com.rapidark.cloud.base.server.service;

import com.rapidark.cloud.base.client.model.entity.SysMenu;
import com.rapidark.cloud.base.server.repository.SysMenuRepository;
import com.rapidark.framework.data.jpa.service.BaseService;
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
