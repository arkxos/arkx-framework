package com.rapidark.cloud.base.server.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rapidark.cloud.base.server.repository.SysRoleAuthorityRepository;
import com.rapidark.cloud.platform.common.core.constant.CacheConstants;
import com.rapidark.framework.common.utils.CriteriaQueryWrapper;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.platform.system.api.entity.SysRoleAuthority;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/30 15:33
 */
@Service
@AllArgsConstructor
public class SysRoleAuthorityService extends BaseService<SysRoleAuthority, Long, SysRoleAuthorityRepository> {

	private final CacheManager cacheManager;

	/**
	 * 更新角色资源
	 * @param roleId 角色
	 * @param menuIds 菜单ID拼成的字符串，每个id之间根据逗号分隔
	 * @return
	 */
//	@Override
	@Transactional(rollbackFor = Exception.class)
//	@CacheEvict(value = CacheConstants.MENU_DETAILS, key = "#roleId")
	public Boolean saveRoleMenus(Long roleId, String menuIds) {
		CriteriaQueryWrapper<SysRoleAuthority> queryWrapper = new CriteriaQueryWrapper();
		queryWrapper.eq(ObjectUtils.isNotEmpty(roleId), SysRoleAuthority::getRoleId, roleId);

		this.deleteByCriteria(queryWrapper);

		if (StrUtil.isBlank(menuIds)) {
			return Boolean.TRUE;
		}
		List<SysRoleAuthority> roleMenuList = Arrays.stream(menuIds.split(StrUtil.COMMA)).map(menuId -> {
			SysRoleAuthority roleMenu = new SysRoleAuthority();
			roleMenu.setRoleId(roleId);
			roleMenu.setAuthorityId(Long.valueOf(menuId));
			return roleMenu;
		}).collect(Collectors.toList());

		// 清空userinfo
		cacheManager.getCache(CacheConstants.USER_DETAILS).clear();
		this.saveAll(roleMenuList);
		return Boolean.TRUE;
	}

}
