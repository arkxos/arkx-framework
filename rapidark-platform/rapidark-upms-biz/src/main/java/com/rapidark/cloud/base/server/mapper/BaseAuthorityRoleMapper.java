package com.rapidark.cloud.base.server.mapper;

import com.rapidark.cloud.base.client.model.AuthorityMenu;
import com.rapidark.cloud.base.client.model.entity.SysRoleAuthority;
import com.rapidark.framework.data.mybatis.mapper.SuperMapper;
import com.rapidark.framework.common.security.OpenAuthority;
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
