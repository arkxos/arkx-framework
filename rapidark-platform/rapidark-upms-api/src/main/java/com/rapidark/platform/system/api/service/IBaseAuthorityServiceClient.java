package com.rapidark.platform.system.api.service;

import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.platform.system.api.entity.AuthorityMenu;
import com.rapidark.platform.system.api.entity.AuthorityResource;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 权限控制API接口
 *
 * @author liuyadu
 */
public interface IBaseAuthorityServiceClient {
    /**
     * 获取所有访问权限列表
     *
     * @return
     */
    @GetMapping("/authority/access")
    ResponseResult<List<AuthorityResource>> findAuthorityResource();

    /**
     * 获取菜单权限列表
     *
     * @return
     */
    @GetMapping("/authority/menu")
    ResponseResult<List<AuthorityMenu>> findAuthorityMenu();
}
