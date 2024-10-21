package com.rapidark.cloud.base.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rapidark.cloud.base.client.constants.ResourceType;
import com.rapidark.cloud.base.client.model.*;
import com.rapidark.cloud.base.client.model.entity.*;
import com.rapidark.cloud.base.server.repository.*;
import com.rapidark.cloud.gateway.formwork.base.BaseService;
import com.rapidark.cloud.gateway.manage.service.GatewayOpenClientAppApiAuthorityService;
import com.rapidark.cloud.gateway.manage.repository.GatewayOpenClientAppApiAuthorityRepository;
import com.rapidark.framework.commons.constants.CommonConstants;
import com.rapidark.framework.commons.exception.OpenAlertException;
import com.rapidark.framework.commons.exception.OpenException;
import com.rapidark.framework.commons.security.OpenAuthority;
import com.rapidark.framework.commons.security.OpenHelper;
import com.rapidark.framework.commons.security.OpenSecurityConstants;
import com.rapidark.framework.commons.utils.CriteriaQueryWrapper;
import com.rapidark.framework.commons.utils.StringUtils;
import com.rapidark.framework.commons.utils.SystemIdGenerator;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 系统权限管理
 * 对菜单、操作、API等进行权限分配操作
 *
 * @author liuyadu
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseAuthorityService extends BaseService<BaseAuthority, Long, BaseAuthorityRepository> {

    @Autowired
    private BaseAuthorityRoleRepository baseAuthorityRoleRepository;
    @Autowired
    private BaseAuthorityUserRepository baseAuthorityUserRepository;
    @Autowired
    private GatewayOpenClientAppApiAuthorityRepository openClientAppApiAuthorityRepository;
    @Autowired
    private GatewayOpenClientAppApiAuthorityService openClientAppApiAuthorityService;
    @Autowired
    private BaseAuthorityActionRepository baseAuthorityActionRepository;
    @Autowired
    private BaseMenuQuery baseMenuQuery;
    @Autowired
    private BaseActionRepository baseActionRepository;
    @Autowired
    private BaseApiRepository baseApiRepository;
    @Autowired
    private BaseRoleService baseRoleService;
    @Autowired
    private BaseUserRepository baseUserRepository;

    @Autowired
    private RedisTokenStore redisTokenStore;
    @Autowired
    private OpenAppRepository openAppRepository;
    @Autowired
    private BaseAuthorityRoleService baseAuthorityRoleService;
    @Autowired
    private BaseAuthorityUserService baseAuthorityUserService;

    @Autowired
    private SystemIdGenerator systemIdGenerator;

    @Value("${spring.application.name}")
    private String DEFAULT_SERVICE_ID;

    /**
     * 获取访问权限列表
     *
     * @return
     */
    public List<AuthorityResource> findAuthorityResource() {
        List<AuthorityResource> list = Lists.newArrayList();
        // 已授权资源权限
        List<AuthorityResource> resourceList = entityRepository.selectAllAuthorityResource();
        if (resourceList != null) {
            list.addAll(resourceList);
        }
        return list;
    }

    /**
     * 获取菜单权限列表
     *
     * @return
     */
    public List<AuthorityMenu> findAuthorityMenu(Integer status, String serviceId) {
        Map<String, Object> map = Maps.newHashMap();
        if (!StringUtils.isEmpty(serviceId)) {
            map.put("serviceId", serviceId);
        }
        map.put("status", status);
        List<AuthorityMenu> authorities = entityRepository.selectAuthorityMenu(map);
        authorities.sort(Comparator.comparing(BaseMenu::getPriority));
        return authorities;
    }

    /**
     * 获取API权限列表
     *
     * @param serviceId
     * @return
     */
    public List<AuthorityApi> findAuthorityApi(String serviceId) {
        Map map = Maps.newHashMap();
        map.put("serviceId", serviceId);
        map.put("status", 1);
        List<AuthorityApi> authorities = entityRepository.selectAuthorityApi(map);
        return authorities;
    }

    /**
     * 查询功能按钮权限列表
     *
     * @param actionId
     * @return
     */
    public List<BaseAuthorityAction> findAuthorityAction(Long actionId) {
        return baseAuthorityActionRepository.queryByActionId(actionId);
    }


    /**
     * 保存或修改权限
     *
     * @param resourceId
     * @param resourceType
     * @return 权限Id
     */
    public BaseAuthority saveOrUpdateAuthority(Long resourceId, ResourceType resourceType) {
        BaseAuthority baseAuthority = getAuthority(resourceId, resourceType);
        String authority = null;
        if (baseAuthority == null) {
            baseAuthority = new BaseAuthority();
        }
        if (ResourceType.menu.equals(resourceType)) {
            BaseMenu menu = baseMenuQuery.getMenu(resourceId);
            authority = OpenSecurityConstants.AUTHORITY_PREFIX_MENU + menu.getMenuCode();
            baseAuthority.setMenuId(resourceId);
            baseAuthority.setStatus(menu.getStatus());
        }
        if (ResourceType.action.equals(resourceType)) {
            BaseAction operation = baseActionRepository.findById(resourceId).get();
            authority = OpenSecurityConstants.AUTHORITY_PREFIX_ACTION + operation.getActionCode();
            baseAuthority.setActionId(resourceId);
            baseAuthority.setStatus(operation.getStatus());
        }
        if (ResourceType.api.equals(resourceType)) {
            BaseApi api = baseApiRepository.findById(resourceId).get();
            authority = OpenSecurityConstants.AUTHORITY_PREFIX_API + api.getApiCode();
            baseAuthority.setApiId(resourceId);
            baseAuthority.setStatus(api.getStatus());
        }
        if (authority == null) {
            return null;
        }
        // 设置权限标识
        baseAuthority.setAuthority(authority);
        if (baseAuthority.getAuthorityId() == null) {
            baseAuthority.setCreateTime(LocalDateTime.now());
            baseAuthority.setUpdateTime(baseAuthority.getCreateTime());
            // 新增权限
            entityRepository.save(baseAuthority);
        } else {
            // 修改权限
            baseAuthority.setUpdateTime(LocalDateTime.now());
            entityRepository.save(baseAuthority);
        }
        return baseAuthority;
    }

    /**
     * 移除权限
     *
     * @param resourceId
     * @param resourceType
     * @return
     */
    public void removeAuthority(Long resourceId, ResourceType resourceType) {
        if (isGranted(resourceId, resourceType)) {
            throw new OpenAlertException(String.format("资源已被授权,不允许删除!取消授权后,再次尝试!"));
        }
        CriteriaQueryWrapper<BaseAuthority> queryWrapper = buildQueryWrapper(resourceId, resourceType);
        List<BaseAuthority> data = findAllByCriteria(queryWrapper);
        for (BaseAuthority datum : data) {
            entityRepository.delete(datum);
        }
    }

    /**
     * 获取权限
     *
     * @param resourceId
     * @param resourceType
     * @return
     */
    public BaseAuthority getAuthority(Long resourceId, ResourceType resourceType) {
        if (resourceId == null || resourceType == null) {
            return null;
        }
        CriteriaQueryWrapper<BaseAuthority> queryWrapper = buildQueryWrapper(resourceId, resourceType);
        return findOneByCriteria(queryWrapper);
    }

    /**
     * 是否已被授权
     *
     * @param resourceId
     * @param resourceType
     * @return
     */
    public Boolean isGranted(Long resourceId, ResourceType resourceType) {
        BaseAuthority authority = getAuthority(resourceId, resourceType);
        if (authority == null || authority.getAuthorityId() == null) {
            return false;
        }

        BaseAuthorityRole roleQueryWrapper = new BaseAuthorityRole();
        roleQueryWrapper.setAuthorityId(authority.getAuthorityId());
        long roleGrantedCount = baseAuthorityRoleService.count(roleQueryWrapper);

        BaseAuthorityUser userQueryWrapper = new BaseAuthorityUser();
        userQueryWrapper.setAuthorityId(authority.getAuthorityId());
        long userGrantedCount = baseAuthorityUserService.count(userQueryWrapper);

        GatewayOpenClientAppApiAuthority openClientAppApiAuthority = new GatewayOpenClientAppApiAuthority();
        openClientAppApiAuthority.setAuthorityId(authority.getAuthorityId());
        long appGrantedCount = openClientAppApiAuthorityService.count(openClientAppApiAuthority);

        return roleGrantedCount > 0 || userGrantedCount > 0 || appGrantedCount > 0;
    }

    /**
     * 构建权限对象
     *
     * @param resourceId
     * @param resourceType
     * @return
     */
    private CriteriaQueryWrapper<BaseAuthority> buildQueryWrapper(Long resourceId, ResourceType resourceType) {
        CriteriaQueryWrapper<BaseAuthority> queryWrapper = new CriteriaQueryWrapper<>();
        if (ResourceType.menu.equals(resourceType)) {
            queryWrapper.eq(BaseAuthority::getMenuId, resourceId);
        }
        if (ResourceType.action.equals(resourceType)) {
            queryWrapper.eq(BaseAuthority::getActionId, resourceId);
        }
        if (ResourceType.api.equals(resourceType)) {
            queryWrapper.eq(BaseAuthority::getApiId, resourceId);
        }
        return queryWrapper;
    }


    /**
     * 移除应用权限
     *
     * @param appId
     */
    public void removeAuthorityApp(String appId) {
        openClientAppApiAuthorityService.deleteByAppId(appId);
    }

    /**
     * 移除功能按钮权限
     *
     * @param actionId
     */
    public void removeAuthorityAction(Long actionId) {
        baseAuthorityActionRepository.deleteByActionId(actionId);
    }


    /**
     * 角色授权
     *
     * @param roleId       角色ID
     * @param expireTime   过期时间,null表示长期,不限制
     * @param authorityIds 权限集合
     * @return
     */
    public void addAuthorityRole(Long roleId, Date expireTime, String... authorityIds) {
        if (roleId == null) {
            return;
        }
        // 清空角色已有授权
        QueryWrapper<BaseAuthorityRole> roleQueryWrapper = new QueryWrapper();
        roleQueryWrapper.lambda().eq(BaseAuthorityRole::getRoleId, roleId);
        baseAuthorityRoleRepository.deleteByRoleId(roleId);
        BaseAuthorityRole authority = null;
        if (authorityIds != null && authorityIds.length > 0) {
            for (String id : authorityIds) {
                authority = new BaseAuthorityRole();
                authority.setAuthorityId(Long.valueOf(id));
                authority.setRoleId(roleId);
                authority.setExpireTime(expireTime);
                authority.setCreateTime(LocalDateTime.now());
                authority.setUpdateTime(authority.getCreateTime());
                // 批量添加授权
                baseAuthorityRoleRepository.save(authority);
            }
        }
    }

    /**
     * 用户授权
     *
     * @param userId       用户ID
     * @param expireTime   过期时间,null表示长期,不限制
     * @param authorityIds 权限集合
     * @return
     */
    public void addAuthorityUser(Long userId, Date expireTime, String... authorityIds) {
        if (userId == null) {
            return;
        }
        Optional<BaseUser> userOptional = baseUserRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return;
        }
        BaseUser user = userOptional.get();
        if (CommonConstants.ROOT.equals(user.getUserName())) {
            throw new OpenAlertException("默认用户无需授权!");
        }
        // 获取用户角色列表
        List<String> roleIds = baseRoleService.getUserRoleIds(userId);
        // 清空用户已有授权
        // 清空角色已有授权
        QueryWrapper<BaseAuthorityUser> userQueryWrapper = new QueryWrapper();
        userQueryWrapper.lambda().eq(BaseAuthorityUser::getUserId, userId);
        baseAuthorityUserRepository.deleteByUserId(userId);
        BaseAuthorityUser authority = null;
        if (authorityIds != null && authorityIds.length > 0) {
            for (String id : authorityIds) {
                if (roleIds != null && roleIds.size() > 0) {
                    // 防止重复授权
                    if (isGrantedByRoleIds(Long.valueOf(id), roleIds.toArray(new String[roleIds.size()]))) {
                        continue;
                    }
                }
                authority = new BaseAuthorityUser();
                authority.setAuthorityId(Long.valueOf(id));
                authority.setUserId(userId);
                authority.setExpireTime(expireTime);
                authority.setCreateTime(LocalDateTime.now());
                authority.setUpdateTime(authority.getCreateTime());
                baseAuthorityUserRepository.save(authority);
            }
        }
    }

    /**
     * 应用授权
     *
     * @param appId  客户端ID
     * @param appSystemCode 应用系统代码
     * @param expireTime   过期时间,null表示长期,不限制
     * @param authorityIds 权限集合
     * @return
     */
    @CacheEvict(value = {"apps"}, key = "'client:'+#appId")
    public void addAuthorityApp(String appId, String appSystemCode, LocalDateTime expireTime, String... authorityIds) {
        if (appId == null) {
            return;
        }
        Optional<OpenApp> openClientOptional = openAppRepository.findById(appId);
        if (openClientOptional.isEmpty()) {
            return;
        }
        OpenApp openApp = openClientOptional.get();
        // 清空应用已有授权
        openClientAppApiAuthorityService.deleteByAppIdAndAppSystemCode(appId, appSystemCode);

        if (authorityIds != null && authorityIds.length > 0) {
            for (String authorityId : authorityIds) {
                GatewayOpenClientAppApiAuthority authority = new GatewayOpenClientAppApiAuthority();
                authority.setId(systemIdGenerator.generate());
                authority.setAppId(appId);
                authority.setAppSystemCode(appSystemCode);
                authority.setAuthorityId(Long.valueOf(authorityId));
                authority.setExpireTime(expireTime);
                openClientAppApiAuthorityService.save(authority);
            }
        }
        // 获取应用最新的权限列表
        List<OpenAuthority> authorities = findAuthorityByApp(appId, "");
        // 动态更新tokenStore客户端
        OpenHelper.updateOpenClientAuthorities(redisTokenStore, openApp.getApiKey(), authorities);
    }

    /**
     * 应用授权-添加单个权限
     *
     * @param appId
     * @param appSystemCode
     * @param expireTime
     * @param authorityId
     */
//    @CacheEvict(value = {"apps"}, key = "'client:'+#appId")
    public void addAuthorityApp(String appId, String appSystemCode, LocalDateTime expireTime, String authorityId) {
        GatewayOpenClientAppApiAuthority authority = new GatewayOpenClientAppApiAuthority();
        authority.setId(systemIdGenerator.generate());
        authority.setAppId(appId);
        authority.setAppSystemCode(appSystemCode);
        authority.setAuthorityId(Long.valueOf(authorityId));
        authority.setExpireTime(expireTime);
        openClientAppApiAuthorityService.save(authority);
    }

    /**
     * 添加功能按钮权限
     *
     * @param actionId
     * @param authorityIds
     * @return
     */
    public void addAuthorityAction(Long actionId, String... authorityIds) {
        if (ObjectUtils.isEmpty(actionId)) {
            return;
        }
        // 移除操作已绑定接口
        removeAuthorityAction(actionId);
        if (authorityIds != null && authorityIds.length > 0) {
            for (String id : authorityIds) {
                Long authorityId = Long.valueOf(id);
                BaseAuthorityAction authority = new BaseAuthorityAction();
                authority.setActionId(actionId);
                authority.setAuthorityId(authorityId);
                authority.setCreateTime(LocalDateTime.now());
                authority.setUpdateTime(authority.getCreateTime());
                baseAuthorityActionRepository.save(authority);
            }
        }
    }

    /**
     * 获取应用已授权权限
     *
     * @param appId
     * @return
     */
    public List<OpenAuthority> findAuthorityByApp(String appId, String appSystemCode) {
        List<OpenAuthority> authorities = Lists.newArrayList();
        List<OpenAuthority> list = openClientAppApiAuthorityRepository.queryAuthoritysByAppIdAndAppSystemCode(appId, appSystemCode);
        if (list != null) {
            authorities.addAll(list);
        }
        return authorities;
    }

    /**
     * 获取角色已授权权限
     *
     * @param roleId
     * @return
     */
    public List<OpenAuthority> findAuthorityByRole(Long roleId) {
        return baseAuthorityRoleRepository.selectAuthorityByRole(roleId);
    }

    /**
     * 获取所有可用权限
     *
     * @param type = null 查询全部  type = 1 获取菜单和操作 type = 2 获取API
     * @return
     */
    public List<OpenAuthority> findAuthorityByType(String type) {
        Map map = Maps.newHashMap();
        map.put("type", type);
        map.put("status", 1);
        return entityRepository.selectAuthorityAll(map);
    }

    /**
     * 获取用户已授权权限
     *
     * @param userId
     * @param root   超级管理员
     * @return
     */
    public List<OpenAuthority> findAuthorityByUser(Long userId, Boolean root) {
        if (root) {
            // 超级管理员返回所有
            return findAuthorityByType("1");
        }
        List<OpenAuthority> authorities = Lists.newArrayList();
        List<BaseRole> rolesList = baseRoleService.getUserRoles(userId);
        if (rolesList != null) {
            for (BaseRole role : rolesList) {
                // 加入角色已授权
                List<OpenAuthority> roleGrantedAuthority = findAuthorityByRole(role.getRoleId());
                if (roleGrantedAuthority != null && roleGrantedAuthority.size() > 0) {
                    authorities.addAll(roleGrantedAuthority);
                }
            }
        }
        // 加入用户特殊授权
        List<OpenAuthority> userGrantedAuthority = baseAuthorityUserRepository.selectAuthorityByUser(userId);
        if (userGrantedAuthority != null && userGrantedAuthority.size() > 0) {
            authorities.addAll(userGrantedAuthority);
        }
        // 权限去重
        HashSet h = new HashSet(authorities);
        authorities.clear();
        authorities.addAll(h);
        return authorities;
    }

    /**
     * 获取用户已授权权限详情
     *
     * @param userId
     * @param root   超级管理员
     * @return
     */
    public List<AuthorityMenu> findAuthorityMenuByUser(Long userId, Boolean root) {
        return findAuthorityMenuByUser(userId, root, null);
    }

    public List<AuthorityMenu> findAuthorityMenuByUser(Long userId, Boolean root, String serviceId) {
        if (root) {
            // 超级管理员返回所有
            return findAuthorityMenu(1, serviceId);
        }
        // 用户权限列表
        List<AuthorityMenu> authorities = Lists.newArrayList();
        List<BaseRole> rolesList = baseRoleService.getUserRoles(userId);
        if (rolesList != null) {
            for (BaseRole role : rolesList) {
                // 加入角色已授权
                List<AuthorityMenu> roleGrantedAuthority = baseAuthorityRoleRepository.selectAuthorityMenuByRole(role.getRoleId(), serviceId);
                if (roleGrantedAuthority != null && roleGrantedAuthority.size() > 0) {
                    authorities.addAll(roleGrantedAuthority);
                }
            }
        }
        // 加入用户特殊授权
        List<AuthorityMenu> userGrantedAuthority = baseAuthorityUserRepository.selectAuthorityMenuByUser(userId, serviceId);
        if (userGrantedAuthority != null && userGrantedAuthority.size() > 0) {
            authorities.addAll(userGrantedAuthority);
        }
        // 权限去重
        HashSet h = new HashSet(authorities);
        authorities.clear();
        authorities.addAll(h);
        //根据优先级从小到大排序
        authorities.sort((AuthorityMenu h1, AuthorityMenu h2) -> h1.getPriority().compareTo(h2.getPriority()));
        return authorities;
    }

    /**
     * 检测权限是否被多个角色授权
     *
     * @param authorityId
     * @param roleIds
     * @return
     */
    public Boolean isGrantedByRoleIds(Long authorityId, String... roleIds) {
        if (roleIds == null || roleIds.length == 0) {
            throw new OpenException("roleIds is empty");
        }
        CriteriaQueryWrapper<BaseAuthorityRole> roleQueryWrapper = new CriteriaQueryWrapper();
        roleQueryWrapper
                .in(BaseAuthorityRole::getRoleId, Arrays.asList(roleIds))
                .eq(BaseAuthorityRole::getAuthorityId, authorityId);
        int count = baseAuthorityRoleService.findAllByCriteria(roleQueryWrapper).size();
        return count > 0;
    }

    /**
     * 清理无效数据
     *
     * @param serviceId
     * @param codes
     */
    public void clearInvalidApi(String serviceId, Collection<String> codes) {
        if (StringUtils.isBlank(serviceId)) {
            return;
        }
//        List<String> invalidApiIds = baseApiMapper
//            .selectObjs(new QueryWrapper<BaseApi>()
//                    .select("api_id").eq("service_id", serviceId)
//                    .notIn(codes != null && !codes.isEmpty(), "api_code", codes))
//                .stream().filter(Objects::nonNull)
//                .map(e -> e.toString())
//                .collect(Collectors.toList());
//        if (invalidApiIds != null) {
//            // 防止删除默认api
//            invalidApiIds.remove("1");
//            invalidApiIds.remove("2");
//            // 获取无效的权限
//            if (invalidApiIds.isEmpty()) {
//                return;
//            }
//            List<String> invalidAuthorityIds = listObjs(new QueryWrapper<BaseAuthority>().select("authority_id").in("api_id", invalidApiIds), e -> e.toString());
//            if (invalidAuthorityIds != null && !invalidAuthorityIds.isEmpty()) {
//                // 移除关联数据
//                openClientAppApiAuthorityRepository.deleteByAuthorityIds(invalidAuthorityIds);
//                baseAuthorityActionMapper.delete(new QueryWrapper<BaseAuthorityAction>().in("authority_id", invalidAuthorityIds));
//                baseAuthorityRoleMapper.delete(new QueryWrapper<BaseAuthorityRole>().in("authority_id", invalidAuthorityIds));
//                baseAuthorityUserMapper.delete(new QueryWrapper<BaseAuthorityUser>().in("authority_id", invalidAuthorityIds));
//                // 移除权限数据
//                this.removeByIds(invalidAuthorityIds);
//                // 移除接口资源
//                baseApiMapper.deleteBatchIds(invalidApiIds);
//            }
//        }
    }
}
