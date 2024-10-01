package com.rapidark.cloud.base.server.service;

import com.rapidark.cloud.base.client.model.entity.BaseMenu;
import com.rapidark.cloud.base.server.mapper.BaseMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/5/13 21:42
 */
@Service
public class BaseMenuQuery {

    @Autowired
    private BaseMenuMapper baseMenuMapper;

    /**
     * 根据主键获取菜单
     *
     * @param menuId
     * @return
     */
    public BaseMenu getMenu(Long menuId) {
        return baseMenuMapper.selectById(menuId);
    }

}
