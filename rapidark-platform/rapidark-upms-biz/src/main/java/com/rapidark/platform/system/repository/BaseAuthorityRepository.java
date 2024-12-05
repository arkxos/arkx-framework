package com.rapidark.platform.system.repository;

import com.rapidark.cloud.base.client.model.AuthorityAction;
import com.rapidark.cloud.base.client.model.AuthorityApi;
import com.rapidark.cloud.base.client.model.AuthorityMenu;
import com.rapidark.cloud.base.client.model.AuthorityResource;
import com.rapidark.framework.common.security.OpenAuthority;
import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import com.rapidark.platform.system.api.entity.BaseAuthority;

import java.util.List;
import java.util.Map;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface BaseAuthorityRepository extends BaseRepository<BaseAuthority, Long> {

    /**
     * 查询所有资源授权列表
     *
     * @return
     */
    @SqlToyQuery
    List<AuthorityResource> selectAllAuthorityResource();

    /**
     * 查询已授权权限列表
     *
     * @param map
     * @return
     */
    @SqlToyQuery
    List<OpenAuthority> selectAuthorityAll(Map map);


    /**
     * 获取菜单权限
     *
     * @param map
     * @return
     */
    @SqlToyQuery
    List<AuthorityMenu> selectAuthorityMenu(Map map);

    /**
     * 获取操作权限
     *
     * @param map
     * @return
     */
    @SqlToyQuery
    List<AuthorityAction> selectAuthorityAction(Map map);

    /**
     * 获取API权限
     *
     * @param map
     * @return
     */
    @SqlToyQuery
    List<AuthorityApi> selectAuthorityApi(Map map);

}
