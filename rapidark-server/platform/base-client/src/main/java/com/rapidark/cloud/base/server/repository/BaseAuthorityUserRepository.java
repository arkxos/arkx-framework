package com.rapidark.cloud.base.server.repository;

import com.rapidark.cloud.base.client.model.AuthorityMenu;
import com.rapidark.cloud.base.client.model.entity.BaseAuthorityUser;
import com.rapidark.common.security.OpenAuthority;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface BaseAuthorityUserRepository extends BaseRepository<BaseAuthorityUser, String> {

    /**
     * 获取用户已授权权限
     *
     * @param userId
     * @return
     */
    @SqlToyQuery
    List<OpenAuthority> selectAuthorityByUser(@Param("userId") String userId);

    /**
     * 获取用户已授权权限完整信息
     *
     * @param userId
     * @param serviceId
     * @return
     */
    @SqlToyQuery
    List<AuthorityMenu> selectAuthorityMenuByUser(@Param("userId") String userId, @Param("serviceId") String serviceId);

    void deleteByUserId(@Param("userId") String userId);
}
