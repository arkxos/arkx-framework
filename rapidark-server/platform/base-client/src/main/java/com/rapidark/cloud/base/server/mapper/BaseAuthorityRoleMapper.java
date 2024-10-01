package com.rapidark.cloud.base.server.mapper;

import com.rapidark.cloud.base.client.model.AuthorityMenu;
import com.rapidark.cloud.base.client.model.entity.BaseAuthorityRole;
import com.rapidark.common.mybatis.base.mapper.SuperMapper;
import com.rapidark.common.security.OpenAuthority;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuyadu
 */
@Repository
public interface BaseAuthorityRoleMapper extends SuperMapper<BaseAuthorityRole> {
    /**
     * 获取角色已授权权限
     *
     * @param roleId
     * @return
     */
    List<OpenAuthority> selectAuthorityByRole(@Param("roleId") Long roleId);

    /**
     * 获取角色菜单权限
     *
     * @param roleId
     * @return
     */
    List<AuthorityMenu> selectAuthorityMenuByRole(@Param("roleId") Long roleId, @Param("serviceId") String serviceId);
}
