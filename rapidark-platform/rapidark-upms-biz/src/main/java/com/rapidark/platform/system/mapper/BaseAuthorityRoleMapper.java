package com.rapidark.platform.system.mapper;

import com.rapidark.framework.common.security.OpenAuthority;
import com.rapidark.framework.data.mybatis.mapper.SuperMapper;
import com.rapidark.platform.system.api.entity.AuthorityMenu;
import com.rapidark.platform.system.api.entity.SysRoleAuthority;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuyadu
 */
@Repository
public interface BaseAuthorityRoleMapper extends SuperMapper<SysRoleAuthority> {
    /**
     * 获取角色已授权权限
     *
     * @param roleId
     * @return
     */
    List<OpenAuthority> selectAuthorityByRole(@Param("roleId") String roleId);

    /**
     * 获取角色菜单权限
     *
     * @param roleId
     * @return
     */
    List<AuthorityMenu> selectAuthorityMenuByRole(@Param("roleId") String roleId, @Param("serviceId") String serviceId);
}
