package com.rapidark.platform.system.repository;

import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import com.rapidark.platform.system.api.entity.SysRole;
import com.rapidark.platform.system.api.entity.SysUserRole;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface SysUserRoleRepository extends BaseRepository<SysUserRole, Long> {

    /**
     * 查询系统用户角色
     *
     * @param userId
     * @return
     */
    @SqlToyQuery
    List<SysRole> selectRoleUserList(@Param("userId") Long userId);

    /**
     * 查询用户角色ID列表
     *
     * @param userId
     * @return
     */
    @SqlToyQuery
    List<String> selectRoleUserIdList(@Param("userId") Long userId);

    @SqlToyQuery
    List<SysUserRole> queryByRoleId(@Param("roleId") Long roleId);

    void deleteByRoleId(@Param("roleId") Long roleId);

    void deleteByUserId(@Param("roleId") Long userId);

	void deleteByUserIds(ArrayList<Long> list);
}
