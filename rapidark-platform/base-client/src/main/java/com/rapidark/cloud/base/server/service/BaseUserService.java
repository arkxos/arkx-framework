package com.rapidark.cloud.base.server.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.base.client.model.UserAccount;
import com.rapidark.cloud.base.client.model.entity.BaseAccount;
import com.rapidark.cloud.base.client.model.entity.BaseAccountLogs;
import com.rapidark.cloud.base.client.model.entity.BaseRole;
import com.rapidark.cloud.base.client.model.entity.BaseUser;
import com.rapidark.cloud.base.server.repository.BaseUserRepository;
import com.rapidark.cloud.base.server.repository.BaseRoleUserRepository;
import com.rapidark.cloud.gateway.formwork.base.BaseService;
import com.rapidark.framework.common.constants.CommonConstants;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.common.model.PageParams;
import com.rapidark.framework.common.security.OpenAuthority;
import com.rapidark.framework.common.security.OpenSecurityConstants;
import com.rapidark.framework.common.utils.CriteriaQueryWrapper;
import com.rapidark.framework.common.utils.StringUtils;
import com.rapidark.framework.data.jpa.entity.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 系统用户资料管理
 * @author darkness
 * @date 2022/5/27 12:11
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseUserService extends BaseService<BaseUser, Long, BaseUserRepository> {

    @Autowired
    private BaseRoleUserRepository baseRoleUserRepository;
    @Autowired
    private BaseAuthorityService baseAuthorityService;
    @Autowired
    private BaseAccountService baseAccountService;

    private final String ACCOUNT_DOMAIN = BaseConstants.ACCOUNT_DOMAIN_ADMIN;

    /**
     * 获取组员角色
     *
     * @param userId
     * @return
     */
    public List<BaseRole> getUserRoles(Long userId) {
        List<BaseRole> roles = baseRoleUserRepository.selectRoleUserList(userId);
        return roles;
    }

    /**
     * 添加系统用户
     *
     * @param baseUser
     * @return
     */
    public void addUser(BaseUser baseUser) {
        if (getUserByUsername(baseUser.getUserName()) != null) {
            throw new OpenAlertException("用户名:" + baseUser.getUserName() + "已存在!");
        }
        baseUser.setCreateTime(LocalDateTime.now());
        baseUser.setUpdateTime(baseUser.getCreateTime());
        //保存系统用户信息
        save(baseUser);
        //默认注册用户名账户
        baseAccountService.register(baseUser.getUserId(), baseUser.getUserName(), baseUser.getPassword(), BaseConstants.ACCOUNT_TYPE_USERNAME, baseUser.getStatus(), ACCOUNT_DOMAIN, null);
        if (StringUtils.matchEmail(baseUser.getEmail())) {
            //注册email账号登陆
            baseAccountService.register(baseUser.getUserId(), baseUser.getEmail(), baseUser.getPassword(), BaseConstants.ACCOUNT_TYPE_EMAIL, baseUser.getStatus(), ACCOUNT_DOMAIN, null);
        }
        if (StringUtils.matchMobile(baseUser.getMobile())) {
            //注册手机号账号登陆
            baseAccountService.register(baseUser.getUserId(), baseUser.getMobile(), baseUser.getPassword(), BaseConstants.ACCOUNT_TYPE_MOBILE, baseUser.getStatus(), ACCOUNT_DOMAIN, null);
        }
    }

    /**
     * 更新系统用户
     *
     * @param baseUser
     * @return
     */
    public void updateUser(BaseUser baseUser) {
        if (baseUser == null || baseUser.getUserId() == null) {
            return;
        }
        if (baseUser.getStatus() != null) {
            baseAccountService.updateStatusByUserId(baseUser.getUserId(), ACCOUNT_DOMAIN, baseUser.getStatus());
        }
        save(baseUser);
    }

    /**
     * 添加第三方登录用户
     *
     * @param baseUser
     * @param accountType
     */
    public void addUserThirdParty(BaseUser baseUser, String accountType) {
        if (!baseAccountService.isExist(baseUser.getUserName(), accountType, ACCOUNT_DOMAIN)) {
            baseUser.setUserType(BaseConstants.USER_TYPE_ADMIN);
            baseUser.setCreateTime(LocalDateTime.now());
            baseUser.setUpdateTime(baseUser.getCreateTime());
            //保存系统用户信息
            save(baseUser);
            // 注册账号信息
            baseAccountService.register(baseUser.getUserId(), baseUser.getUserName(), baseUser.getPassword(), accountType, Status.ENABLED, ACCOUNT_DOMAIN, null);
        }
    }

    /**
     * 更新密码
     *
     * @param userId
     * @param password
     */
    public void updatePassword(Long userId, String password) {
        baseAccountService.updatePasswordByUserId(userId, ACCOUNT_DOMAIN, password);
    }

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    public Page<BaseUser> findListPage(PageParams pageParams) {
        BaseUser query = pageParams.mapToObject(BaseUser.class);
        CriteriaQueryWrapper<BaseUser> queryWrapper = new CriteriaQueryWrapper();
        queryWrapper
                .eq(ObjectUtils.isNotEmpty(query.getUserId()), BaseUser::getUserId, query.getUserId())
                .eq(ObjectUtils.isNotEmpty(query.getUserType()), BaseUser::getUserType, query.getUserType())
                .eq(ObjectUtils.isNotEmpty(query.getUserName()), BaseUser::getUserName, query.getUserName())
                .eq(ObjectUtils.isNotEmpty(query.getMobile()), BaseUser::getMobile, query.getMobile());
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
    public List<BaseUser> findAllList() {
        List<BaseUser> list = findAll();
        return list;
    }

    /**
     * 依据系统用户Id查询系统用户信息
     *
     * @param userId
     * @return
     */
    public BaseUser getUserById(Long userId) {
        return findById(userId);
    }

    /**
     * 根据用户ID获取用户信息和权限
     *
     * @param userId
     * @return
     */
    public UserAccount getUserAccount(Long userId) {
        // 用户权限列表
        List<OpenAuthority> authorities = Lists.newArrayList();
        // 用户角色列表
        List<Map> roles = Lists.newArrayList();
        List<BaseRole> rolesList = getUserRoles(userId);
        if (rolesList != null) {
            for (BaseRole role : rolesList) {
                Map roleMap = Maps.newHashMap();
                roleMap.put("roleId", role.getRoleId());
                roleMap.put("roleCode", role.getRoleCode());
                roleMap.put("roleName", role.getRoleName());
                // 用户角色详情
                roles.add(roleMap);
                // 加入角色标识
                OpenAuthority authority = new OpenAuthority(role.getRoleId(), OpenSecurityConstants.AUTHORITY_PREFIX_ROLE + role.getRoleCode(), null, "role");
                authorities.add(authority);

                // 查询角色拥有的权限
                List<OpenAuthority> roleAuthorities = baseAuthorityService.findAuthorityByRole(role.getRoleId());
                for (OpenAuthority roleAuthority : roleAuthorities) {
                    authorities.add(roleAuthority);
                }
            }
        }

        //查询系统用户资料
        BaseUser baseUser = getUserById(userId);

        // 加入用户权限
        List<OpenAuthority> userGrantedAuthority = baseAuthorityService.findAuthorityByUser(userId, CommonConstants.ROOT.equals(baseUser.getUserName()));
        if (userGrantedAuthority != null && userGrantedAuthority.size() > 0) {
            authorities.addAll(userGrantedAuthority);
        }
        UserAccount userAccount = new UserAccount();
        // 昵称
        userAccount.setNickName(baseUser.getNickName());
        // 头像
        userAccount.setAvatar(baseUser.getAvatar());
        // 权限信息
        userAccount.setAuthorities(authorities);
        userAccount.setRoles(roles);
        return userAccount;
    }


    /**
     * 依据登录名查询系统用户信息
     *
     * @param username
     * @return
     */
    public BaseUser getUserByUsername(String username) {
        BaseUser saved = entityRepository.findByUserName(username);
        return saved;
    }


    /**
     * 支持系统用户名、手机号、email登陆
     *
     * @param account
     * @return
     */
    public UserAccount login(String account, Map<String, String> parameterMap, String ip, String userAgent) {
        if (StringUtils.isBlank(account)) {
            return null;
        }
        // 第三方登录标识
        String loginType = parameterMap.get("login_type");
        BaseAccount baseAccount;
        if (StringUtils.isNotBlank(loginType)) {
            baseAccount = baseAccountService.getAccount(account, loginType, ACCOUNT_DOMAIN);
        } else {
            // 非第三方登录

            //用户名登录
            baseAccount = baseAccountService.getAccount(account, BaseConstants.ACCOUNT_TYPE_USERNAME, ACCOUNT_DOMAIN);

            // 手机号登陆
            if (baseAccount == null && StringUtils.matchMobile(account)) {
                baseAccount = baseAccountService.getAccount(account, BaseConstants.ACCOUNT_TYPE_MOBILE, ACCOUNT_DOMAIN);
            }
            // 邮箱登陆
            if (baseAccount == null && StringUtils.matchEmail(account)) {
                baseAccount = baseAccountService.getAccount(account, BaseConstants.ACCOUNT_TYPE_EMAIL, ACCOUNT_DOMAIN);
            }
        }

        // 获取用户详细信息
        if (baseAccount != null) {
            //添加登录日志
            try {
                if (!StringUtils.isEmpty(ip)) {
                    BaseAccountLogs log = new BaseAccountLogs();
                    log.setId(IdUtil.getSnowflakeNextId());
                    log.setDomain(ACCOUNT_DOMAIN);
                    log.setUserId(baseAccount.getUserId());
                    log.setAccount(baseAccount.getAccount());
                    log.setAccountId(String.valueOf(baseAccount.getAccountId()));
                    log.setAccountType(baseAccount.getAccountType());
                    log.setLoginIp(ip);
                    log.setLoginAgent(userAgent);
                    baseAccountService.addLoginLog(log);
                }
            } catch (Exception e) {
                log.error("添加登录日志失败:{}", e);
            }
            // 用户权限信息
            UserAccount userAccount = getUserAccount(baseAccount.getUserId());
            // 复制账号信息
            BeanUtils.copyProperties(baseAccount, userAccount);
            return userAccount;
        }
        return null;
    }
}
