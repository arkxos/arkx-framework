package com.rapidark.cloud.base.server.service;

import com.rapidark.cloud.base.client.model.entity.BaseMenu;
import com.rapidark.cloud.base.server.repository.BaseMenuRepository;
import com.rapidark.framework.data.jpa.service.BaseService;
import org.springframework.stereotype.Service;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/5/13 21:42
 */
@Service
public class BaseMenuQuery extends BaseService<BaseMenu, Long, BaseMenuRepository> {

    /**
     * 根据主键获取菜单
     *
     * @param menuId
     * @return
     */
    public BaseMenu getMenu(Long menuId) {
        return findById(menuId);
    }

}
