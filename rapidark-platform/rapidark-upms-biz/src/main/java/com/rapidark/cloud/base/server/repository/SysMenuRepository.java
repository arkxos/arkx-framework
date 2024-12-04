package com.rapidark.cloud.base.server.repository;

import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import com.rapidark.platform.system.api.entity.SysMenu;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface SysMenuRepository extends BaseRepository<SysMenu, Long> {


	/**
	 * 通过角色编号查询菜单
	 * @param roleId 角色ID
	 * @return
	 */
	@SqlToyQuery
	List<SysMenu> queryMenusByRoleId(Long roleId);

	@SqlToyQuery
	List<SysMenu> queryByNameAndType(@Param("name") String menuName, @Param("type") String type);
}
