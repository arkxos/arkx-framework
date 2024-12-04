package com.rapidark.cloud.base.client.service;

import com.rapidark.cloud.base.client.model.AuthorityMenu;
import com.rapidark.cloud.base.client.model.AuthorityResource;
import com.rapidark.framework.common.model.ResponseResult;
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
