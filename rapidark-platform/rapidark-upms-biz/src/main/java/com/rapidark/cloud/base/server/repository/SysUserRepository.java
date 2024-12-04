package com.rapidark.cloud.base.server.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rapidark.cloud.base.client.model.entity.SysUser;
import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.platform.system.api.dto.UserDTO;
import com.rapidark.platform.system.api.vo.UserVO;
import org.apache.ibatis.annotations.Param;

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
	UserVO getUserVoByUsername(String username);

	/**
	 * 分页查询用户信息（含角色）
	 * @param page 分页
	 * @param userDTO 查询参数
	 * @param dataScope
	 * @return list
	 */
	IPage<UserVO> getUserVosPage(Page page, @Param("query") UserDTO userDTO);

	/**
	 * 通过ID查询用户信息
	 * @param id 用户ID
	 * @return userVo
	 */
	UserVO getUserVoById(Long id);

	/**
	 * 查询用户列表
	 * @param userDTO 查询条件
	 * @param dataScope 数据权限声明
	 * @return
	 */
	List<UserVO> selectVoList(@Param("query") UserDTO userDTO);

}
