package com.rapidark.cloud.base.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.base.client.model.entity.BaseRole;
import com.rapidark.cloud.base.client.model.entity.BaseRoleUser;
import com.rapidark.cloud.base.client.model.entity.BaseUser;
import com.rapidark.cloud.base.server.repository.BaseRoleRepository;
import com.rapidark.cloud.base.server.repository.BaseRoleUserRepository;
import com.rapidark.cloud.base.server.service.BaseRoleService;
import com.rapidark.cloud.base.server.repository.BaseUserRepository;
import com.rapidark.cloud.gateway.formwork.base.BaseService;
import com.rapidark.common.constants.CommonConstants;
import com.rapidark.common.exception.OpenAlertException;
import com.rapidark.common.model.PageParams;
import com.rapidark.common.mybatis.base.service.impl.BaseServiceImpl;
import com.rapidark.common.utils.CriteriaQueryWrapper;
import com.rapidark.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 角色管理
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseRoleService extends BaseService<BaseRole, String, BaseRoleRepository> {

    @Autowired
    private BaseRoleUserRepository baseRoleUserRepository;
    @Autowired
    private BaseUserRepository baseUserRepository;
    @Autowired
    private BaseRoleUserService baseRoleUserService;

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    public Page<BaseRole> findListPage(PageParams pageParams) {
        BaseRole query = pageParams.mapToObject(BaseRole.class);
        CriteriaQueryWrapper<BaseRole> queryWrapper = new CriteriaQueryWrapper();
        queryWrapper
                .likeRight(ObjectUtils.isNotEmpty(query.getRoleCode()), BaseRole::getRoleCode, query.getRoleCode())
                .likeRight(ObjectUtils.isNotEmpty(query.getRoleName()), BaseRole::getRoleName, query.getRoleName());
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
    public List<BaseRole> findAllList() {
        List<BaseRole> list = findAll();
        return list;
    }

    /**
     * 获取角色信息
     *
     * @param roleId
     * @return
     */
    public BaseRole getRole(String roleId) {
        return findById(roleId);
    }

    /**
     * 添加角色
     *
     * @param role 角色
     * @return
     */
    public BaseRole addRole(BaseRole role) {
        if (isExist(role.getRoleCode())) {
            throw new OpenAlertException(String.format("%s编码已存在!", role.getRoleCode()));
        }
        if (role.getStatus() == null) {
            role.setStatus(BaseConstants.ENABLED);
        }
        if (role.getIsPersist() == null) {
            role.setIsPersist(BaseConstants.DISABLED);
        }
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
    public BaseRole updateRole(BaseRole role) {
        BaseRole saved = getRole(role.getRoleId());
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
    public void removeRole(String roleId) {
        if (roleId == null) {
            return;
        }
        BaseRole role = getRole(roleId);
        if (role != null && role.getIsPersist().equals(BaseConstants.ENABLED)) {
            throw new OpenAlertException(String.format("保留数据,不允许删除"));
        }
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
        BaseRole queryWrapper = new BaseRole();
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
    public void saveUserRoles(String userId, String... roles) {
        if (userId == null || roles == null) {
            return;
        }
        Optional<BaseUser> userOptional = baseUserRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return;
        }
        BaseUser user = userOptional.get();
        if (CommonConstants.ROOT.equals(user.getUserName())) {
            throw new OpenAlertException("默认用户无需分配!");
        }
        // 先清空,在添加
        removeUserRoles(userId);
        if (roles.length > 0) {
            for (String roleId : roles) {
                BaseRoleUser roleUser = new BaseRoleUser();
                roleUser.setUserId(userId);
                roleUser.setRoleId(roleId);
                baseRoleUserRepository.save(roleUser);
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
    public void saveRoleUsers(String roleId, String... userIds) {
        if (roleId == null || userIds == null) {
            return;
        }
        // 先清空,在添加
        removeRoleUsers(roleId);
        if (userIds.length > 0) {
            for (String userId : userIds) {
                BaseRoleUser roleUser = new BaseRoleUser();
                roleUser.setUserId(userId);
                roleUser.setRoleId(roleId);
                baseRoleUserRepository.save(roleUser);
            }
            // 批量保存
        }
    }

    /**
     * 查询角色成员
     *
     * @return
     */
    public List<BaseRoleUser> findRoleUsers(String roleId) {
        return baseRoleUserRepository.queryByRoleId(roleId);
    }

    /**
     * 查询角色成员
     *
     * @param roleId
     * @param roleCode
     * @return
     */
    public List<BaseRoleUser> findRoleUsersByRoleIdOrRoleCode(String roleId, String roleCode) {
        //查询角色信息
        BaseRole baseRole = entityRepository.findByRoleIdOrRoleCode(roleId, roleCode);
        if (baseRole == null) {
            //角色不存在,直接返回
            return null;
        }
        //角色存在,查询角色下的用户列表
        return findRoleUsers(baseRole.getRoleId());
    }


    /**
     * 获取角色所有授权组员数量
     *
     * @param roleId
     * @return
     */
    public long getCountByRole(String roleId) {
        BaseRoleUser queryWrapper = new BaseRoleUser();
        queryWrapper.setRoleId(roleId);
        long result = baseRoleUserService.count(queryWrapper);
        return result;
    }

    /**
     * 获取组员角色数量
     *
     * @param userId
     * @return
     */
    public long getCountByUser(String userId) {
        BaseRoleUser queryWrapper = new BaseRoleUser();
        queryWrapper.setUserId(userId);
        long result = baseRoleUserService.count(queryWrapper);
        return result;
    }

    /**
     * 移除角色所有组员
     *
     * @param roleId
     * @return
     */
    public void removeRoleUsers(String roleId) {
        baseRoleUserRepository.deleteByRoleId(roleId);
    }

    /**
     * 移除组员的所有角色
     *
     * @param userId
     * @return
     */
    public void removeUserRoles(String userId) {
        baseRoleUserRepository.deleteByUserId(userId);
    }

    /**
     * 检测是否存在
     *
     * @param userId
     * @param roleId
     * @return
     */
    public Boolean isExist(String userId, String roleId) {
        BaseRoleUser queryWrapper = new BaseRoleUser();
        queryWrapper.setRoleId(roleId);
        queryWrapper.setUserId(userId);
//        baseRoleUserMapper.delete(queryWrapper);
        long result = baseRoleUserRepository.count(Example.of(queryWrapper));
        return result > 0;
    }


    /**
     * 获取组员角色
     *
     * @param userId
     * @return
     */
    public List<BaseRole> getUserRoles(String userId) {
        List<BaseRole> roles = baseRoleUserRepository.selectRoleUserList(userId);
        return roles;
    }

    /**
     * 获取用户角色ID列表
     *
     * @param userId
     * @return
     */
    public List<String> getUserRoleIds(String userId) {
        return baseRoleUserRepository.selectRoleUserIdList(userId);
    }
}
