package com.rapidark.cloud.base.server.repository;

import com.rapidark.cloud.base.client.model.entity.BaseRole;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface BaseRoleRepository extends BaseRepository<BaseRole, String> {

    @SqlToyQuery
    List<BaseRole> selectRoleList(Map params);

    @SqlToyQuery
    BaseRole findByRoleIdOrRoleCode(@Param("roleId") String roleId, @Param("roleCode") String roleCode);
}
