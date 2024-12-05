package com.rapidark.platform.system.repository;

import com.rapidark.framework.common.security.OpenAuthority;
import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import com.rapidark.platform.system.api.entity.AuthorityMenu;
import com.rapidark.platform.system.api.entity.SysRoleAuthority;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface SysRoleAuthorityRepository extends BaseRepository<SysRoleAuthority, Long> {

    /**
     * 获取角色已授权权限
     *
     * @param roleId
     * @return
     */
    @SqlToyQuery
    List<OpenAuthority> selectAuthorityByRole(@Param("roleId") Long roleId);

    /**
     * 获取角色菜单权限
     *
     * @param roleId
     * @return
     */
    @SqlToyQuery
    List<AuthorityMenu> selectAuthorityMenuByRole(@Param("roleId") Long roleId, @Param("serviceId") String serviceId);

    void deleteByRoleId(@Param("roleId") Long roleId);

	void deleteByAuthorityId(Long id);
}
