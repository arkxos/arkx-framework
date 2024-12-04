package com.rapidark.cloud.base.server.repository;

import com.rapidark.cloud.base.client.model.entity.SysRole;
import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface SysRoleRepository extends BaseRepository<SysRole, Long> {

	/**
	 * 通过用户ID，查询角色信息
	 * @param userId
	 * @return
	 */
	List<SysRole> listRolesByUserId(Long userId);

	@SqlToyQuery
    List<SysRole> selectRoleList(Map params);

    @SqlToyQuery
    SysRole findByRoleIdOrRoleCode(@Param("roleId") String roleId, @Param("roleCode") String roleCode);

	SysRole findByRoleCode(String defaultRole);
}
