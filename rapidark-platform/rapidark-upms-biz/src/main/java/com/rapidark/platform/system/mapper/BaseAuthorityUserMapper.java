package com.rapidark.platform.system.mapper;

import com.rapidark.framework.common.security.OpenAuthority;
import com.rapidark.framework.data.mybatis.mapper.SuperMapper;
import com.rapidark.platform.system.api.entity.AuthorityMenu;
import com.rapidark.platform.system.api.entity.BaseAuthorityUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuyadu
 */
@Repository
public interface BaseAuthorityUserMapper extends SuperMapper<BaseAuthorityUser> {
    /**
     * 获取用户已授权权限
     *
     * @param userId
     * @return
     */
    List<OpenAuthority> selectAuthorityByUser(@Param("userId") Long userId);

    /**
     * 获取用户已授权权限完整信息
     *
     * @param userId
     * @param serviceId
     * @return
     */
    List<AuthorityMenu> selectAuthorityMenuByUser(@Param("userId") Long userId, @Param("serviceId") String serviceId);
}
