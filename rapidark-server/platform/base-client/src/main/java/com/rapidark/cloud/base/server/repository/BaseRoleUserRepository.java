package com.rapidark.cloud.base.server.repository;

import com.rapidark.cloud.base.client.model.entity.BaseRole;
import com.rapidark.cloud.base.client.model.entity.BaseRoleUser;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface BaseRoleUserRepository extends BaseRepository<BaseRoleUser, String> {

    /**
     * 查询系统用户角色
     *
     * @param userId
     * @return
     */
    @SqlToyQuery
    List<BaseRole> selectRoleUserList(@Param("userId") String userId);

    /**
     * 查询用户角色ID列表
     *
     * @param userId
     * @return
     */
    @SqlToyQuery
    List<String> selectRoleUserIdList(@Param("userId") String userId);

    @SqlToyQuery
    List<BaseRoleUser> queryByRoleId(@Param("roleId") String roleId);

    void deleteByRoleId(@Param("roleId") String roleId);

    void deleteByUserId(@Param("roleId") String userId);
}
