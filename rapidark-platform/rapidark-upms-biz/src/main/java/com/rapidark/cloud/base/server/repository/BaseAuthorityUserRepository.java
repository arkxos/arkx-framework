package com.rapidark.cloud.base.server.repository;

import com.rapidark.cloud.base.client.model.AuthorityMenu;
import com.rapidark.framework.common.security.OpenAuthority;
import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import com.rapidark.platform.system.api.entity.BaseAuthorityUser;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface BaseAuthorityUserRepository extends BaseRepository<BaseAuthorityUser, Long> {

    /**
     * 获取用户已授权权限
     *
     * @param userId
     * @return
     */
    @SqlToyQuery
    List<OpenAuthority> selectAuthorityByUser(@Param("userId") Long userId);

    /**
     * 获取用户已授权权限完整信息
     *
     * @param userId
     * @param serviceId
     * @return
     */
    @SqlToyQuery
    List<AuthorityMenu> selectAuthorityMenuByUser(@Param("userId") Long userId, @Param("serviceId") String serviceId);

    void deleteByUserId(@Param("userId") Long userId);
}
