package com.rapidark.platform.system.repository;

import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import com.rapidark.platform.system.api.dto.UserDTO;
import com.rapidark.platform.system.api.entity.SysUser;
import com.rapidark.platform.system.api.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface SysUserRepository extends BaseRepository<SysUser, Long> {

    SysUser findByUsername(String username);

	/**
	 * 通过用户名查询用户信息（含有角色信息）
	 * @param username 用户名
	 * @return userVo
	 */
	@SqlToyQuery
	UserVO getUserVoByUsername(String username);

	/**
	 * 分页查询用户信息（含角色）
	 * @param page 分页
	 * @param userDTO 查询参数
	 * @return list
	 */
	@SqlToyQuery
	Page<UserVO> getUserVosPage(Pageable page, @Param("query") UserDTO userDTO);

	/**
	 * 通过ID查询用户信息
	 * @param id 用户ID
	 * @return userVo
	 */
	@SqlToyQuery
	UserVO getUserVoById(Long id);

	/**
	 * 查询用户列表
	 * @param userDTO 查询条件
	 * @return
	 */
	@SqlToyQuery
	List<UserVO> selectVoList(@Param("query") UserDTO userDTO);

}
