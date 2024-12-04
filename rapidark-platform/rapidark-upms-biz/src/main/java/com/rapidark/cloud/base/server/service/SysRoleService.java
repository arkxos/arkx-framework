package com.rapidark.cloud.base.server.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.pig4cloud.plugin.excel.vo.ErrorMessage;
import com.rapidark.cloud.base.server.repository.SysRoleRepository;
import com.rapidark.cloud.base.server.repository.SysUserRoleRepository;
import com.rapidark.cloud.base.server.repository.SysUserRepository;
import com.rapidark.cloud.platform.common.core.exception.ErrorCodes;
import com.rapidark.cloud.platform.common.core.util.MsgUtils;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.framework.common.constants.CommonConstants;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.framework.common.utils.CriteriaQueryWrapper;
import com.rapidark.framework.common.utils.StringUtils;
import com.rapidark.platform.system.api.entity.SysRole;
import com.rapidark.platform.system.api.entity.SysRoleAuthority;
import com.rapidark.platform.system.api.entity.SysUser;
import com.rapidark.platform.system.api.entity.SysUserRole;
import com.rapidark.platform.system.api.vo.RoleExcelVO;
import com.rapidark.platform.system.api.vo.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色管理
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysRoleService extends BaseService<SysRole, Long, SysRoleRepository> {

    @Autowired
    private SysUserRoleRepository sysUserRoleRepository;
    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysRoleAuthorityService roleMenuService;

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    public Page<SysRole> findListPage(PageParams pageParams) {
        SysRole query = pageParams.mapToObject(SysRole.class);
        CriteriaQueryWrapper<SysRole> queryWrapper = new CriteriaQueryWrapper();
        queryWrapper
                .likeRight(ObjectUtils.isNotEmpty(query.getRoleCode()), SysRole::getRoleCode, query.getRoleCode())
                .likeRight(ObjectUtils.isNotEmpty(query.getRoleName()), SysRole::getRoleName, query.getRoleName());
//        queryWrapper.orderByDesc("create_time");
        Pageable pageable = PageRequest.of(pageParams.getPage(), pageParams.getLimit(),
                Sort.by(Sort.Direction.DESC, "createTime"));
        return findAllByCriteria(queryWrapper, pageable);
    }

    /**
     * 查询列表
     *
     * @return
     */
    public List<SysRole> findAllList() {
        List<SysRole> list = findAll();
        return list;
    }

    /**
     * 获取角色信息
     *
     * @param roleId
     * @return
     */
    public SysRole getRole(Long roleId) {
        return findById(roleId);
    }

    /**
     * 添加角色
     *
     * @param role 角色
     * @return
     */
    public SysRole addRole(SysRole role) {
        if (isExist(role.getRoleCode())) {
            throw new OpenAlertException(String.format("%s编码已存在!", role.getRoleCode()));
        }
//        if (role.getStatus() == null) {
//            role.setStatus(BaseConstants.ENABLED);
//        }
//        if (role.getIsPersist() == null) {
//            role.setIsPersist(BaseConstants.DISABLED);
//        }
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(role.getCreateTime());
        save(role);
        return role;
    }

    /**
     * 更新角色
     *
     * @param role 角色
     * @return
     */
    public SysRole updateRole(SysRole role) {
        SysRole saved = getRole(role.getRoleId());
        if (role == null) {
            throw new OpenAlertException("信息不存在!");
        }
        if (!saved.getRoleCode().equals(role.getRoleCode())) {
            // 和原来不一致重新检查唯一性
            if (isExist(role.getRoleCode())) {
                throw new OpenAlertException(String.format("%s编码已存在!", role.getRoleCode()));
            }
        }
        role.setUpdateTime(LocalDateTime.now());
        save(role);
        return role;
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return
     */
    public void removeRole(Long roleId) {
        if (roleId == null) {
            return;
        }
        SysRole role = getRole(roleId);
//        if (role != null && role.getStatus() == Status.LOCKED) {//.equals(BaseConstants.ENABLED)) {
//            throw new OpenAlertException(String.format("保留数据,不允许删除"));
//        }
        long count = getCountByRole(roleId);
        if (count > 0) {
            throw new OpenAlertException("该角色下存在授权人员,不允许删除!");
        }
        deleteById(roleId);
    }

    /**
     * 检测角色编码是否存在
     *
     * @param roleCode
     * @return
     */
    public Boolean isExist(String roleCode) {
        if (StringUtils.isBlank(roleCode)) {
            throw new OpenAlertException("roleCode不能为空!");
        }
        SysRole queryWrapper = new SysRole();
        queryWrapper.setRoleCode(roleCode);
        return count(queryWrapper) > 0;
    }

    /**
     * 用户添加角色
     *
     * @param userId
     * @param roles
     * @return
     */
    public void saveUserRoles(Long userId, String... roles) {
        if (userId == null || roles == null) {
            return;
        }
        Optional<SysUser> userOptional = sysUserRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return;
        }
        SysUser user = userOptional.get();
        if (CommonConstants.ROOT.equals(user.getUsername())) {
            throw new OpenAlertException("默认用户无需分配!");
        }
        // 先清空,在添加
        removeUserRoles(userId);
        if (roles.length > 0) {
            for (String roleId : roles) {
                SysUserRole roleUser = new SysUserRole();
                roleUser.setUserId(userId);
                roleUser.setRoleId(Long.valueOf(roleId));
                sysUserRoleRepository.save(roleUser);
            }
            // 批量保存
        }
    }

    /**
     * 角色添加成员
     *
     * @param roleId
     * @param userIds
     */
    public void saveRoleUsers(Long roleId, String... userIds) {
        if (roleId == null || userIds == null) {
            return;
        }
        // 先清空,在添加
        removeRoleUsers(roleId);
        if (userIds.length > 0) {
            for (String userId : userIds) {
                SysUserRole roleUser = new SysUserRole();
                roleUser.setUserId(Long.valueOf(userId));
                roleUser.setRoleId(roleId);
                sysUserRoleRepository.save(roleUser);
            }
            // 批量保存
        }
    }

    /**
     * 查询角色成员
     *
     * @return
     */
    public List<SysUserRole> findRoleUsers(Long roleId) {
        return sysUserRoleRepository.queryByRoleId(roleId);
    }

    /**
     * 查询角色成员
     *
     * @param roleId
     * @param roleCode
     * @return
     */
    public List<SysUserRole> findRoleUsersByRoleIdOrRoleCode(String roleId, String roleCode) {
        //查询角色信息
        SysRole sysRole = entityRepository.findByRoleIdOrRoleCode(roleId, roleCode);
        if (sysRole == null) {
            //角色不存在,直接返回
            return null;
        }
        //角色存在,查询角色下的用户列表
        return findRoleUsers(sysRole.getRoleId());
    }


    /**
     * 获取角色所有授权组员数量
     *
     * @param roleId
     * @return
     */
    public long getCountByRole(Long roleId) {
        SysUserRole queryWrapper = new SysUserRole();
        queryWrapper.setRoleId(roleId);
        long result = sysUserRoleService.count(queryWrapper);
        return result;
    }

    /**
     * 获取组员角色数量
     *
     * @param userId
     * @return
     */
    public long getCountByUser(Long userId) {
        SysUserRole queryWrapper = new SysUserRole();
        queryWrapper.setUserId(userId);
        long result = sysUserRoleService.count(queryWrapper);
        return result;
    }

    /**
     * 移除角色所有组员
     *
     * @param roleId
     * @return
     */
    public void removeRoleUsers(Long roleId) {
        sysUserRoleRepository.deleteByRoleId(roleId);
    }

    /**
     * 移除组员的所有角色
     *
     * @param userId
     * @return
     */
    public void removeUserRoles(Long userId) {
        sysUserRoleRepository.deleteByUserId(userId);
    }

    /**
     * 检测是否存在
     *
     * @param userId
     * @param roleId
     * @return
     */
    public Boolean isExist(Long userId, Long roleId) {
        SysUserRole queryWrapper = new SysUserRole();
        queryWrapper.setRoleId(roleId);
        queryWrapper.setUserId(userId);
//        baseRoleUserMapper.delete(queryWrapper);
        long result = sysUserRoleRepository.count(Example.of(queryWrapper));
        return result > 0;
    }


    /**
     * 获取组员角色
     *
     * @param userId
     * @return
     */
    public List<SysRole> getUserRoles(Long userId) {
        List<SysRole> roles = sysUserRoleRepository.selectRoleUserList(userId);
        return roles;
    }

    /**
     * 获取用户角色ID列表
     *
     * @param userId
     * @return
     */
    public List<String> getUserRoleIds(Long userId) {
        return sysUserRoleRepository.selectRoleUserIdList(userId);
    }

//=====================================
	/**
	 * 通过用户ID，查询角色信息
	 * @param userId
	 * @return
	 */
//	@Override
	public List<SysRole> findRolesByUserId(Long userId) {
		return entityRepository.listRolesByUserId(userId);
	}

	/**
	 * 根据角色ID 查询角色列表，注意缓存删除
	 * @param roleIdList 角色ID列表
	 * @param key 缓存key
	 * @return
	 */
//	@Override
//	@Cacheable(value = CacheConstants.ROLE_DETAILS, key = "#key", unless = "#result.isEmpty()")
	public List<SysRole> findRolesByRoleIds(List<Long> roleIdList, String key) {
		return entityRepository.findAllById(roleIdList);
	}

	/**
	 * 通过角色ID，删除角色,并清空角色菜单缓存
	 * @param ids
	 * @return
	 */
//	@Override
//	@Transactional(rollbackFor = Exception.class)
	public void removeRoleByIds(Long[] ids) {
		CriteriaQueryWrapper<SysRoleAuthority> queryWrapper = new CriteriaQueryWrapper();
		queryWrapper.in(SysRoleAuthority::getRoleId, CollUtil.toList(ids));

		roleMenuService.deleteByCriteria(queryWrapper);

		CriteriaQueryWrapper<SysRole> queryWrapper1 = new CriteriaQueryWrapper();
		queryWrapper1.in(SysRole::getRoleId, CollUtil.toList(ids));
		this.deleteByCriteria(queryWrapper1);
	}

	/**
	 * 根据角色菜单列表
	 * @param roleVo 角色&菜单列表
	 * @return
	 */
//	@Override
	public Boolean updateRoleMenus(RoleVO roleVo) {
		return roleMenuService.saveRoleMenus(roleVo.getRoleId(), roleVo.getMenuIds());
	}

	/**
	 * 导入角色
	 * @param excelVOList 角色列表
	 * @param bindingResult 错误信息列表
	 * @return ok fail
	 */
//	@Override
	public ResponseResult importRole(List<RoleExcelVO> excelVOList, BindingResult bindingResult) {
		// 通用校验获取失败的数据
		List<ErrorMessage> errorMessageList = (List<ErrorMessage>) bindingResult.getTarget();

		// 个性化校验逻辑
		List<SysRole> roleList = this.findAll();

		// 执行数据插入操作 组装 RoleDto
		for (RoleExcelVO excel : excelVOList) {
			Set<String> errorMsg = new HashSet<>();
			// 检验角色名称或者角色编码是否存在
			boolean existRole = roleList.stream()
					.anyMatch(sysRole -> excel.getRoleName().equals(sysRole.getRoleName())
							|| excel.getRoleCode().equals(sysRole.getRoleCode()));

			if (existRole) {
				errorMsg.add(MsgUtils.getMessage(ErrorCodes.SYS_ROLE_NAMEORCODE_EXISTING, excel.getRoleName(),
						excel.getRoleCode()));
			}

			// 数据合法情况
			if (CollUtil.isEmpty(errorMsg)) {
				insertExcelRole(excel);
			}
			else {
				// 数据不合法情况
				errorMessageList.add(new ErrorMessage(excel.getLineNum(), errorMsg));
			}
		}
		if (CollUtil.isNotEmpty(errorMessageList)) {
			return ResponseResult.failed(errorMessageList);
		}
		return ResponseResult.ok();
	}

	/**
	 * 查询全部的角色
	 * @return list
	 */
//	@Override
	public List<RoleExcelVO> listRole() {
		List<SysRole> roleList = this.findAllList();
		// 转换成execl 对象输出
		return roleList.stream().map(role -> {
			RoleExcelVO roleExcelVO = new RoleExcelVO();
			BeanUtil.copyProperties(role, roleExcelVO);
			return roleExcelVO;
		}).collect(Collectors.toList());
	}

	/**
	 * 插入excel Role
	 */
	private void insertExcelRole(RoleExcelVO excel) {
		SysRole sysRole = new SysRole();
		sysRole.setRoleName(excel.getRoleName());
		sysRole.setRoleDesc(excel.getRoleDesc());
		sysRole.setRoleCode(excel.getRoleCode());
		this.save(sysRole);
	}

}
