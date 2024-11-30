package com.rapidark.cloud.base.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.base.client.model.UserAccount;
import com.rapidark.cloud.base.client.model.entity.BaseAccount;
import com.rapidark.cloud.base.client.model.entity.BaseAccountLogs;
import com.rapidark.cloud.base.client.model.entity.BaseDeveloper;
import com.rapidark.cloud.base.server.repository.BaseDeveloperRepository;
import com.rapidark.framework.common.utils.PageResult;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.cloud.gateway.manage.service.command.ChangeDeveloperPasswordCommand;
import com.rapidark.cloud.gateway.manage.service.command.UpdateDeveloperCommand;
import com.rapidark.framework.common.exception.OpenAlertException;
import com.rapidark.framework.common.model.PageParams;
import com.rapidark.framework.common.utils.StringUtils;
import com.rapidark.framework.data.jpa.entity.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 开发商管理
 * @author darkness
 * @date 2022/6/24 14:28
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseDeveloperService extends BaseService<BaseDeveloper, Long, BaseDeveloperRepository> {

    @Autowired
    private BaseAccountService baseAccountService;

    private final String ACCOUNT_DOMAIN = BaseConstants.ACCOUNT_DOMAIN_PORTAL;

    /**
     * 添加系统用户
     *
     * @param baseDeveloper
     * @return
     */
    public void addUser(BaseDeveloper baseDeveloper) {
        if (getDeveloperByUsername(baseDeveloper.getUserName()).isPresent()) {
            throw new OpenAlertException("用户名:" + baseDeveloper.getUserName() + "已存在!");
        }
        baseDeveloper.setCreateTime(LocalDateTime.now());
        baseDeveloper.setUpdateTime(baseDeveloper.getCreateTime());

        //保存系统用户信息
        entityRepository.save(baseDeveloper);

        //默认注册用户名账户
        baseAccountService.register(baseDeveloper.getId(), baseDeveloper.getUserName(), baseDeveloper.getPassword(), BaseConstants.ACCOUNT_TYPE_USERNAME, baseDeveloper.getStatus(), ACCOUNT_DOMAIN, null);

        if (StringUtils.matchEmail(baseDeveloper.getEmail())) {
            //注册email账号登陆
            baseAccountService.register(baseDeveloper.getId(), baseDeveloper.getEmail(), baseDeveloper.getPassword(), BaseConstants.ACCOUNT_TYPE_EMAIL, baseDeveloper.getStatus(), ACCOUNT_DOMAIN, null);
        }
        if (StringUtils.matchMobile(baseDeveloper.getMobile())) {
            //注册手机号账号登陆
            baseAccountService.register(baseDeveloper.getId(), baseDeveloper.getMobile(), baseDeveloper.getPassword(), BaseConstants.ACCOUNT_TYPE_MOBILE, baseDeveloper.getStatus(), ACCOUNT_DOMAIN, null);
        }
    }

    /**
     * 更新系统用户
     *
     * @param command
     * @return
     */
    public void updateUser(UpdateDeveloperCommand command) {
        if (command.getStatus() != null) {
            baseAccountService.updateStatusByUserId(command.getId(), ACCOUNT_DOMAIN, command.getStatus());
        }
        BaseDeveloper developer = findById(command.getId());
        BeanUtil.copyProperties(command, developer, CopyOptions.create().setIgnoreNullValue(true));
        entityRepository.save(developer);
    }

    /**
     * 添加第三方登录用户
     *
     * @param baseDeveloper
     * @param accountType
     */
    public void addUserThirdParty(BaseDeveloper baseDeveloper, String accountType) {
        if (!baseAccountService.isExist(baseDeveloper.getUserName(), accountType, ACCOUNT_DOMAIN)) {
            baseDeveloper.setType(BaseConstants.COMPANY_TYPE_ADMIN);
//            baseDeveloper.setCreateTime(new Date());
            baseDeveloper.setUpdateTime(baseDeveloper.getCreateTime());
            //保存系统用户信息
            entityRepository.save(baseDeveloper);
            // 注册账号信息
            baseAccountService.register(baseDeveloper.getId(), baseDeveloper.getUserName(), baseDeveloper.getPassword(), accountType, Status.ENABLED, ACCOUNT_DOMAIN, null);
        }
    }

    /**
     * 更新密码
     */
    public void updatePassword(ChangeDeveloperPasswordCommand command) {
        baseAccountService.updatePasswordByUserId(command.getUserId(), ACCOUNT_DOMAIN, command.getPassword());
    }

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    public PageResult<BaseDeveloper> findListPage(PageParams pageParams) {
        BaseDeveloper query = pageParams.mapToObject(BaseDeveloper.class);
        QueryWrapper<BaseDeveloper> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(ObjectUtils.isNotEmpty(query.getId()), BaseDeveloper::getId, query.getId())
                .eq(ObjectUtils.isNotEmpty(query.getType()), BaseDeveloper::getType, query.getType())
                .eq(ObjectUtils.isNotEmpty(query.getUserName()), BaseDeveloper::getUserName, query.getUserName())
                .eq(ObjectUtils.isNotEmpty(query.getMobile()), BaseDeveloper::getMobile, query.getMobile());
        queryWrapper.orderByDesc("create_time");
        return this.pageList(query, pageParams.getPage(), pageParams.getLimit());
    }

    /**
     * 查询列表
     *
     * @return
     */
    public List<BaseDeveloper> findAllList() {
        List<BaseDeveloper> list = this.findAll();
        return list;
    }

    /**
     * 依据系统用户Id查询系统用户信息
     *
     * @param userId
     * @return
     */
    public BaseDeveloper getUserById(Long userId) {
        return entityRepository.findById(userId).get();
    }

    /**
     * 依据登录名查询系统用户信息
     *
     * @param username
     * @return
     */
    public Optional<BaseDeveloper> getDeveloperByUsername(String username) {
        Optional<BaseDeveloper> developerOptional = entityRepository.findByUserName(username);
        return developerOptional;
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
        BaseAccount baseAccount = null;
        if (StringUtils.isNotBlank(loginType)) {
            baseAccount = baseAccountService.getAccount(account, loginType, ACCOUNT_DOMAIN);
        } else {
            // 非第三方登录

            //用户名登录
            baseAccount = baseAccountService.getAccount(account, BaseConstants.ACCOUNT_TYPE_USERNAME, ACCOUNT_DOMAIN);

            // 手机号登陆
            if (StringUtils.matchMobile(account)) {
                baseAccount = baseAccountService.getAccount(account, BaseConstants.ACCOUNT_TYPE_MOBILE, ACCOUNT_DOMAIN);
            }
            // 邮箱登陆
            if (StringUtils.matchEmail(account)) {
                baseAccount = baseAccountService.getAccount(account, BaseConstants.ACCOUNT_TYPE_EMAIL, ACCOUNT_DOMAIN);
            }
        }
        // 获取用户详细信息
        if (baseAccount != null) {
            //添加登录日志
            try {
                if (!StringUtils.isEmpty(ip)) {
                    BaseAccountLogs log = new BaseAccountLogs();
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
            // 复制账号信息
            UserAccount userAccount = new UserAccount();
            BeanUtils.copyProperties(userAccount, baseAccount);
            return userAccount;
        }
        return null;
    }
}
