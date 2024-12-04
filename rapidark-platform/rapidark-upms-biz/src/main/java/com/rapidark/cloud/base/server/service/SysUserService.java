package com.rapidark.cloud.base.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.support.spring.stat.annotation.Stat;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pig4cloud.plugin.excel.vo.ErrorMessage;
import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.base.client.model.UserAccount;
import com.rapidark.cloud.base.server.repository.SysRoleRepository;
import com.rapidark.cloud.base.server.repository.SysUserRepository;
import com.rapidark.cloud.base.server.repository.SysUserRoleRepository;
import com.rapidark.cloud.platform.admin.mapper.SysUserPostMapper;
import com.rapidark.cloud.platform.admin.service.SysDeptService;
import com.rapidark.cloud.platform.admin.service.SysPostService;
import com.rapidark.cloud.platform.common.core.constant.CacheConstants;
import com.rapidark.cloud.platform.common.core.exception.ErrorCodes;
import com.rapidark.cloud.platform.common.core.util.MsgUtils;
import com.rapidark.cloud.platform.common.security.util.SecurityUtils;
import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.framework.common.constants.CommonConstants;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.framework.common.security.OpenAuthority;
import com.rapidark.framework.common.security.OpenSecurityConstants;
import com.rapidark.framework.common.utils.CriteriaQueryWrapper;
import com.rapidark.framework.common.utils.StringUtils;
import com.rapidark.framework.data.jpa.entity.Status;
import com.rapidark.platform.system.api.dto.UserDTO;
import com.rapidark.platform.system.api.dto.UserInfo;
import com.rapidark.platform.system.api.entity.*;
import com.rapidark.platform.system.api.util.ParamResolver;
import com.rapidark.platform.system.api.vo.UserExcelVO;
import com.rapidark.platform.system.api.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统用户资料管理
 * @author darkness
 * @date 2022/5/27 12:11
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SysUserService extends BaseService<SysUser, Long, SysUserRepository> {

    @Autowired
    private SysUserRoleRepository sysUserRoleRepository;
    @Autowired
    private BaseAuthorityService baseAuthorityService;
    @Autowired
    private BaseAccountService baseAccountService;

    private final String ACCOUNT_DOMAIN = BaseConstants.ACCOUNT_DOMAIN_ADMIN;
	@Autowired
	private SysRoleRepository sysRoleRepository;

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
     * 添加系统用户
     *
     * @param sysUser
     * @return
     */
    public void addUser(SysUser sysUser) {
        if (getUserByUsername(sysUser.getUsername()) != null) {
            throw new OpenAlertException("用户名:" + sysUser.getUsername() + "已存在!");
        }
        sysUser.setCreateTime(LocalDateTime.now());
        sysUser.setUpdateTime(sysUser.getCreateTime());
        //保存系统用户信息
        save(sysUser);
        //默认注册用户名账户
        baseAccountService.register(sysUser.getUserId(), sysUser.getUsername(), sysUser.getPassword(), BaseConstants.ACCOUNT_TYPE_USERNAME, sysUser.getStatus(), ACCOUNT_DOMAIN, null);
        if (StringUtils.matchEmail(sysUser.getEmail())) {
            //注册email账号登陆
            baseAccountService.register(sysUser.getUserId(), sysUser.getEmail(), sysUser.getPassword(), BaseConstants.ACCOUNT_TYPE_EMAIL, sysUser.getStatus(), ACCOUNT_DOMAIN, null);
        }
        if (StringUtils.matchMobile(sysUser.getMobile())) {
            //注册手机号账号登陆
            baseAccountService.register(sysUser.getUserId(), sysUser.getMobile(), sysUser.getPassword(), BaseConstants.ACCOUNT_TYPE_MOBILE, sysUser.getStatus(), ACCOUNT_DOMAIN, null);
        }
    }

    /**
     * 更新系统用户
     *
     * @param sysUser
     * @return
     */
    public void updateUser(SysUser sysUser) {
        if (sysUser == null || sysUser.getUserId() == null) {
            return;
        }
        if (sysUser.getStatus() != null) {
            baseAccountService.updateStatusByUserId(sysUser.getUserId(), ACCOUNT_DOMAIN, sysUser.getStatus());
        }
        save(sysUser);
    }

    /**
     * 添加第三方登录用户
     *
     * @param sysUser
     * @param accountType
     */
    public void addUserThirdParty(SysUser sysUser, String accountType) {
        if (!baseAccountService.isExist(sysUser.getUsername(), accountType, ACCOUNT_DOMAIN)) {
            sysUser.setUserType(BaseConstants.USER_TYPE_ADMIN);
            sysUser.setCreateTime(LocalDateTime.now());
            sysUser.setUpdateTime(sysUser.getCreateTime());
            //保存系统用户信息
            save(sysUser);
            // 注册账号信息
            baseAccountService.register(sysUser.getUserId(), sysUser.getUsername(), sysUser.getPassword(), accountType, Status.ENABLED, ACCOUNT_DOMAIN, null);
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
    public Page<SysUser> findListPage(PageParams pageParams) {
        SysUser query = pageParams.mapToObject(SysUser.class);
        CriteriaQueryWrapper<SysUser> queryWrapper = new CriteriaQueryWrapper();
        queryWrapper
                .eq(ObjectUtils.isNotEmpty(query.getUserId()), SysUser::getUserId, query.getUserId())
                .eq(ObjectUtils.isNotEmpty(query.getUserType()), SysUser::getUserType, query.getUserType())
                .eq(ObjectUtils.isNotEmpty(query.getUsername()), SysUser::getUsername, query.getUsername())
                .eq(ObjectUtils.isNotEmpty(query.getMobile()), SysUser::getMobile, query.getMobile());
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
    public List<SysUser> findAllList() {
        List<SysUser> list = findAll();
        return list;
    }

    /**
     * 依据系统用户Id查询系统用户信息
     *
     * @param userId
     * @return
     */
    public SysUser getUserById(Long userId) {
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
        List<SysRole> rolesList = getUserRoles(userId);
        if (rolesList != null) {
            for (SysRole role : rolesList) {
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
        SysUser sysUser = getUserById(userId);

        // 加入用户权限
        List<OpenAuthority> userGrantedAuthority = baseAuthorityService.findAuthorityByUser(userId, CommonConstants.ROOT.equals(sysUser.getUsername()));
        if (userGrantedAuthority != null && userGrantedAuthority.size() > 0) {
            authorities.addAll(userGrantedAuthority);
        }
        UserAccount userAccount = new UserAccount();
        // 昵称
        userAccount.setNickName(sysUser.getNickName());
        // 头像
        userAccount.setAvatar(sysUser.getAvatar());
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
    public SysUser getUserByUsername(String username) {
        SysUser saved = entityRepository.findByUsername(username);
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
        SysAccount sysAccount;
        if (StringUtils.isNotBlank(loginType)) {
            sysAccount = baseAccountService.getAccount(account, loginType, ACCOUNT_DOMAIN);
        } else {
            // 非第三方登录

            //用户名登录
            sysAccount = baseAccountService.getAccount(account, BaseConstants.ACCOUNT_TYPE_USERNAME, ACCOUNT_DOMAIN);

            // 手机号登陆
            if (sysAccount == null && StringUtils.matchMobile(account)) {
                sysAccount = baseAccountService.getAccount(account, BaseConstants.ACCOUNT_TYPE_MOBILE, ACCOUNT_DOMAIN);
            }
            // 邮箱登陆
            if (sysAccount == null && StringUtils.matchEmail(account)) {
                sysAccount = baseAccountService.getAccount(account, BaseConstants.ACCOUNT_TYPE_EMAIL, ACCOUNT_DOMAIN);
            }
        }

        // 获取用户详细信息
        if (sysAccount != null) {
            //添加登录日志
            try {
                if (!StringUtils.isEmpty(ip)) {
                    BaseAccountLogs log = new BaseAccountLogs();
                    log.setId(IdUtil.getSnowflakeNextId());
                    log.setDomain(ACCOUNT_DOMAIN);
                    log.setUserId(sysAccount.getUserId());
                    log.setAccount(sysAccount.getAccount());
                    log.setAccountId(String.valueOf(sysAccount.getAccountId()));
                    log.setAccountType(sysAccount.getAccountType());
                    log.setLoginIp(ip);
                    log.setLoginAgent(userAgent);
                    baseAccountService.addLoginLog(log);
                }
            } catch (Exception e) {
                log.error("添加登录日志失败:{}", e);
            }
            // 用户权限信息
            UserAccount userAccount = getUserAccount(sysAccount.getUserId());
            // 复制账号信息
            BeanUtils.copyProperties(sysAccount, userAccount);
            return userAccount;
        }
        return null;
    }

	//=================================
	private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

	@Autowired
	private  SysMenuService sysMenuService;

	@Autowired
	private  SysRoleService sysRoleService;
	@Autowired
	private  SysPostService sysPostService;
	@Autowired
	private  SysDeptService sysDeptService;
	@Autowired
	private  SysUserPostMapper sysUserPostMapper;
	@Autowired
	private  CacheManager cacheManager;

	/**
	 * 保存用户信息
	 * @param userDto DTO 对象
	 * @return success/fail
	 */
	
	@Transactional(rollbackFor = Exception.class)
	public Boolean saveUser(UserDTO userDto) {
		SysUser sysUser = new SysUser();
		BeanUtils.copyProperties(userDto, sysUser);
//		sysUser.setDelFlag(com.rapidark.cloud.platform.common.core.constant.CommonConstants.STATUS_NORMAL);
//		sysUser.setCreateBy(userDto.getUsername());
//		sysUser.setPassword(ENCODER.encode(userDto.getPassword()));
		entityRepository.save(sysUser);

		// 保存用户岗位信息
		Optional.ofNullable(userDto.getPost()).ifPresent(posts -> {
			posts.stream().map(postId -> {
				SysUserPost userPost = new SysUserPost();
				userPost.setUserId(sysUser.getUserId());
				userPost.setPostId(postId);
				return userPost;
			}).forEach(sysUserPostMapper::insert);
		});

		// 如果角色为空，赋默认角色
		if (CollUtil.isEmpty(userDto.getRole())) {
			// 获取默认角色编码
			String defaultRole = ParamResolver.getStr("USER_DEFAULT_ROLE");
			// 默认角色
			SysRole sysRole = sysRoleRepository.findByRoleCode(defaultRole);
			userDto.setRole(Collections.singletonList(sysRole.getRoleId()));
		}

		// 插入用户角色关系表
		List<SysUserRole> userRoles = userDto.getRole().stream().map(roleId -> {
			SysUserRole userRole = new SysUserRole();
			userRole.setUserId(sysUser.getUserId());
			userRole.setRoleId(roleId);
			return userRole;
		}).collect(Collectors.toCollection(ArrayList::new));
		sysUserRoleRepository.saveAll(userRoles);
		return Boolean.TRUE;
	}

	/**
	 * 通过查用户的全部信息
	 * @param sysUser 用户
	 * @return
	 */
	
	public UserInfo findUserInfo(SysUser sysUser) {
		UserInfo userInfo = new UserInfo();
		userInfo.setSysUser(sysUser);

		// 设置角色列表 （ID）
		List<Long> roleIds = sysRoleService.findRolesByUserId(sysUser.getUserId())
				.stream()
				.map(SysRole::getRoleId)
				.collect(Collectors.toList());
		userInfo.setRoles(ArrayUtil.toArray(roleIds, Long.class));

		// 设置权限列表（menu.permission）
		Set<String> permissions = new HashSet<>();
		roleIds.forEach(roleId -> {
			List<String> permissionList = sysMenuService.findMenuByRoleId(roleId)
					.stream()
					.filter(menu -> StrUtil.isNotEmpty(menu.getPermission()))
					.map(SysMenu::getPermission)
					.collect(Collectors.toList());
			permissions.addAll(permissionList);
		});
		userInfo.setPermissions(ArrayUtil.toArray(permissions, String.class));
		return userInfo;
	}

	/**
	 * 分页查询用户信息（含有角色信息）
	 * @param page 分页对象
	 * @param userDTO 参数列表
	 * @return
	 */
	
	public IPage getUsersWithRolePage(com.baomidou.mybatisplus.extension.plugins.pagination.Page page, UserDTO userDTO) {
		return entityRepository.getUserVosPage(page, userDTO);
	}

	/**
	 * 通过ID查询用户信息
	 * @param id 用户ID
	 * @return 用户信息
	 */
	
	public UserVO selectUserVoById(Long id) {
		return entityRepository.getUserVoById(id);
	}

	/**
	 * 删除用户
	 * @param ids 用户ID 列表
	 * @return Boolean
	 */
	
	@Transactional(rollbackFor = Exception.class)
	public Boolean deleteUserByIds(Long[] ids) {
		// 删除 spring cache
		List<SysUser> userList = entityRepository.findAllById(CollUtil.toList(ids));
		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
		for (SysUser sysUser : userList) {
			// 立即删除
			cache.evictIfPresent(sysUser.getUsername());
		}


		sysUserRoleRepository.deleteByUserIds(CollUtil.toList(ids));

		entityRepository.deleteAllById(CollUtil.toList(ids));
		return Boolean.TRUE;
	}

	/**
	 * 更新当前用户基本信息
	 * @param userDto 用户信息
	 * @return Boolean
	 */
	
	@CacheEvict(value = CacheConstants.USER_DETAILS, key = "#userDto.username")
	public ResponseResult<Boolean> updateUserInfo(UserDTO userDto) {
		SysUser sysUser = new SysUser();
		sysUser.setUserId(SecurityUtils.getUser().getId());
		sysUser.setMobile(userDto.getMobile());
		sysUser.setAvatar(userDto.getAvatar());
		sysUser.setNickName(userDto.getNickName());
		sysUser.setName(userDto.getName());
		sysUser.setEmail(userDto.getEmail());
		this.entityRepository.save(sysUser);

		return ResponseResult.ok();
	}

	/**
	 * 更新指定用户信息
	 * @param userDto 用户信息
	 * @return
	 */
	
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = CacheConstants.USER_DETAILS, key = "#userDto.username")
	public Boolean updateUser(UserDTO userDto) {
		// 更新用户表信息
		SysUser sysUser = new SysUser();
		BeanUtils.copyProperties(userDto, sysUser);
		sysUser.setUpdateTime(LocalDateTime.now());
		if (StrUtil.isNotBlank(userDto.getPassword())) {
			sysUser.setPassword(ENCODER.encode(userDto.getPassword()));
		}
		this.entityRepository.save(sysUser);

		// 更新用户角色表
		if (Objects.nonNull(userDto.getRole())) {
			// 删除用户角色关系
			sysUserRoleRepository.deleteByUserId(userDto.getUserId());
			Collection<SysUserRole> userRoles = userDto.getRole().stream().map(roleId -> {
				SysUserRole userRole = new SysUserRole();
				userRole.setUserId(sysUser.getUserId());
				userRole.setRoleId(roleId);
				return userRole;
			}).collect(Collectors.toCollection(ArrayList::new));
			sysUserRoleRepository.saveAll(userRoles);
		}

		if (Objects.nonNull(userDto.getPost())) {
			// 删除用户岗位关系
			sysUserPostMapper
					.delete(Wrappers.<SysUserPost>lambdaQuery().eq(SysUserPost::getUserId, userDto.getUserId()));
			Collection<SysUserPost> posts = userDto.getPost().stream().map(postId -> {
				SysUserPost userPost = new SysUserPost();
				userPost.setUserId(sysUser.getUserId());
				userPost.setPostId(postId);
				return userPost;
			}).collect(Collectors.toCollection(ArrayList::new));

			sysUserPostMapper.insert(posts);
		}
		return Boolean.TRUE;
	}

	/**
	 * 查询全部的用户
	 * @param userDTO 查询条件
	 * @return list
	 */
	
	public List<UserExcelVO> listUser(UserDTO userDTO) {
		// 根据数据权限查询全部的用户信息
		List<UserVO> voList = entityRepository.selectVoList(userDTO);
		// 转换成execl 对象输出
		return voList.stream().map(userVO -> {
			UserExcelVO excelVO = new UserExcelVO();
			BeanUtils.copyProperties(userVO, excelVO);
			String roleNameList = userVO.getRoleList()
					.stream()
					.map(SysRole::getRoleName)
					.collect(Collectors.joining(StrUtil.COMMA));
			excelVO.setRoleNameList(roleNameList);
			String postNameList = userVO.getPostList()
					.stream()
					.map(SysPost::getPostName)
					.collect(Collectors.joining(StrUtil.COMMA));
			excelVO.setPostNameList(postNameList);
			return excelVO;
		}).collect(Collectors.toList());
	}

	/**
	 * excel 导入用户, 插入正确的 错误的提示行号
	 * @param excelVOList excel 列表数据
	 * @param bindingResult 错误数据
	 * @return ok fail
	 */
	
	public ResponseResult importUser(List<UserExcelVO> excelVOList, BindingResult bindingResult) {
		// 通用校验获取失败的数据
		List<ErrorMessage> errorMessageList = (List<ErrorMessage>) bindingResult.getTarget();
		List<SysDept> deptList = sysDeptService.list();
		List<SysRole> roleList = sysRoleService.findAllList();
		List<SysPost> postList = sysPostService.list();

		// 执行数据插入操作 组装 UserDto
		for (UserExcelVO excel : excelVOList) {
			// 个性化校验逻辑
			List<SysUser> userList = this.findAllList();

			Set<String> errorMsg = new HashSet<>();
			// 校验用户名是否存在
			boolean exsitUserName = userList.stream()
					.anyMatch(sysUser -> excel.getUsername().equals(sysUser.getUsername()));

			if (exsitUserName) {
				errorMsg.add(MsgUtils.getMessage(ErrorCodes.SYS_USER_USERNAME_EXISTING, excel.getUsername()));
			}

			// 判断输入的部门名称列表是否合法
			Optional<SysDept> deptOptional = deptList.stream()
					.filter(dept -> excel.getDeptName().equals(dept.getName()))
					.findFirst();
			if (!deptOptional.isPresent()) {
				errorMsg.add(MsgUtils.getMessage(ErrorCodes.SYS_DEPT_DEPTNAME_INEXISTENCE, excel.getDeptName()));
			}

			// 判断输入的角色名称列表是否合法
			List<String> roleNameList = StrUtil.split(excel.getRoleNameList(), StrUtil.COMMA);
			List<SysRole> roleCollList = roleList.stream()
					.filter(role -> roleNameList.stream().anyMatch(name -> role.getRoleName().equals(name)))
					.collect(Collectors.toList());

			if (roleCollList.size() != roleNameList.size()) {
				errorMsg.add(MsgUtils.getMessage(ErrorCodes.SYS_ROLE_ROLENAME_INEXISTENCE, excel.getRoleNameList()));
			}

			// 判断输入的部门名称列表是否合法
			List<String> postNameList = StrUtil.split(excel.getPostNameList(), StrUtil.COMMA);
			List<SysPost> postCollList = postList.stream()
					.filter(post -> postNameList.stream().anyMatch(name -> post.getPostName().equals(name)))
					.collect(Collectors.toList());

			if (postCollList.size() != postNameList.size()) {
				errorMsg.add(MsgUtils.getMessage(ErrorCodes.SYS_POST_POSTNAME_INEXISTENCE, excel.getPostNameList()));
			}

			// 数据合法情况
			if (CollUtil.isEmpty(errorMsg)) {
				insertExcelUser(excel, deptOptional, roleCollList, postCollList);
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
	 * 插入excel User
	 */
	private void insertExcelUser(UserExcelVO excel, Optional<SysDept> deptOptional, List<SysRole> roleCollList,
								 List<SysPost> postCollList) {
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(excel.getUsername());
		userDTO.setMobile(excel.getPhone());
		userDTO.setNickName(excel.getNickname());
		userDTO.setName(excel.getName());
		userDTO.setEmail(excel.getEmail());
		// 批量导入初始密码为手机号
		userDTO.setPassword(userDTO.getMobile());
		// 根据部门名称查询部门ID
		userDTO.setDeptId(deptOptional.get().getDeptId());
		// 插入岗位名称
		List<Long> postIdList = postCollList.stream().map(SysPost::getPostId).collect(Collectors.toList());
		userDTO.setPost(postIdList);
		// 根据角色名称查询角色ID
		List<Long> roleIdList = roleCollList.stream().map(SysRole::getRoleId).collect(Collectors.toList());
		userDTO.setRole(roleIdList);
		// 插入用户
		this.saveUser(userDTO);
	}

	/**
	 * 注册用户 赋予用户默认角色
	 * @param userDto 用户信息
	 * @return success/false
	 */
	
	@Transactional(rollbackFor = Exception.class)
	public ResponseResult<Boolean> registerUser(UserDTO userDto) {
		// 判断用户名是否存在
		SysUser sysUser = this.entityRepository.findByUsername(userDto.getUsername());
		if (sysUser != null) {
			String message = MsgUtils.getMessage(ErrorCodes.SYS_USER_USERNAME_EXISTING, userDto.getUsername());
			return ResponseResult.failed(message);
		}
		return ResponseResult.ok(saveUser(userDto));
	}

	/**
	 * 锁定用户
	 * @param username 用户名
	 * @return
	 */
	
	@CacheEvict(value = CacheConstants.USER_DETAILS, key = "#username")
	public ResponseResult<Boolean> lockUser(String username) {
		SysUser sysUser = entityRepository.findByUsername(username);

		if (Objects.nonNull(sysUser)) {
			sysUser.setStatus(Status.LOCKED);//com.rapidark.cloud.platform.common.core.constant.CommonConstants.STATUS_LOCK);
			entityRepository.save(sysUser);
		}
		return ResponseResult.ok();
	}

	/**
	 * 修改密码
	 * @param userDto 用户信息
	 * @return
	 */
	
	@CacheEvict(value = CacheConstants.USER_DETAILS, key = "#userDto.username")
	public ResponseResult changePassword(UserDTO userDto) {
		SysUser sysUser = entityRepository.findById(SecurityUtils.getUser().getId()).orElseThrow();
//		return ResponseResult.failed("用户不存在");


		if (StrUtil.isEmpty(userDto.getPassword())) {
			return ResponseResult.failed("原密码不能为空");
		}

		if (!ENCODER.matches(userDto.getPassword(), sysUser.getPassword())) {
			log.info("原密码错误，修改个人信息失败:{}", userDto.getUsername());
			return ResponseResult.failed(MsgUtils.getMessage(ErrorCodes.SYS_USER_UPDATE_PASSWORDERROR));
		}

		if (StrUtil.isEmpty(userDto.getNewpassword1())) {
			return ResponseResult.failed("新密码不能为空");
		}
		String password = ENCODER.encode(userDto.getNewpassword1());

		sysUser.setPassword(password);
		this.entityRepository.save(sysUser);

		return ResponseResult.ok();
	}

	/**
	 * 校验密码
	 * @param password 密码明文
	 * @return
	 */
	
	public ResponseResult checkPassword(String password) {
		SysUser sysUser = entityRepository.findById(SecurityUtils.getUser().getId()).orElseThrow();

		if (!ENCODER.matches(password, sysUser.getPassword())) {
			log.info("原密码错误");
			return ResponseResult.failed("密码输入错误");
		}
		else {
			return ResponseResult.ok();
		}
	}
}
