package com.rapidark.platform.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.platform.system.repository.BaseAccountLogsRepository;
import com.rapidark.platform.system.repository.SysAccountRepository;
import com.rapidark.framework.common.utils.CriteriaQueryWrapper;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.framework.data.jpa.entity.Status;
import com.rapidark.platform.system.api.entity.BaseAccountLogs;
import com.rapidark.platform.system.api.entity.SysAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 系统用户登录账号管理
 * 支持多账号登陆
 * @author darkness
 * @date 2022/5/27 11:52
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseAccountService extends BaseService<SysAccount, Long, SysAccountRepository> {

//    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BaseAccountLogsRepository baseAccountLogsRepository;


    /**
     * 根据主键获取账号信息
     *
     * @param accountId
     * @return
     */
    public SysAccount getAccountById(Long accountId) {
        return findById(accountId);
    }

    /**
     * 获取账号信息
     *
     * @param account
     * @param accountType
     * @param domain
     * @return
     */
    public SysAccount getAccount(String account, String accountType, String domain) {
        SysAccount criteria = new SysAccount();
        criteria.setAccount(account);
        criteria.setAccountType(accountType);
        criteria.setDomain(domain);
        return findOneByExample(criteria);
    }

    /**
     * 注册账号
     *
     * @param userId
     * @param account
     * @param password
     * @param accountType
     * @param status
     * @param domain
     * @param registerIp
     * @return
     */
    public SysAccount register(Long userId, String account, String password, String accountType, Status status, String domain, String registerIp) {
        if (isExist(account, accountType, domain)) {
            // 账号已被注册
            throw new RuntimeException(String.format("account=[%s],domain=[%s]", account, domain));
        }
        //加密
        String encodePassword = passwordEncoder.encode(password);
        SysAccount sysAccount = new SysAccount(userId, account, encodePassword, accountType, domain, registerIp);
        sysAccount.setCreateTime(LocalDateTime.now());
        sysAccount.setUpdateTime(sysAccount.getCreateTime());
        sysAccount.setStatus(status);
        entityRepository.save(sysAccount);
        return sysAccount;
    }


    /**
     * 检测账号是否存在
     *
     * @param account
     * @param accountType
     * @param domain
     * @return
     */
    public Boolean isExist(String account, String accountType, String domain) {
        QueryWrapper<SysAccount> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(SysAccount::getAccount, account)
                .eq(SysAccount::getAccountType, accountType)
                .eq(SysAccount::getDomain, domain);
        SysAccount example = new SysAccount();
        example.setAccount(account);
        example.setAccountType(accountType);
        example.setDomain(domain);
        long count = count(example);
        return count > 0 ? true : false;
    }

    /**
     * 删除账号
     *
     * @param accountId
     * @return
     */
    public void removeAccount(Long accountId) {
        deleteById(accountId);
    }


    /**
     * 更新账号状态
     *
     * @param accountId
     * @param status
     */
    public void updateStatus(Long accountId, Status status) {
        SysAccount sysAccount = findById(accountId);
        sysAccount.setUpdateTime(LocalDateTime.now());
        sysAccount.setStatus(status);
        entityRepository.save(sysAccount);
    }

    /**
     * 根据用户更新账户状态
     *
     * @param userId
     * @param domain
     * @param status
     */
    public void updateStatusByUserId(Long userId, String domain, Status status) {
        if (status == null) {
            return;
        }
        SysAccount example = new SysAccount();
        example.setUserId(userId);
        example.setDomain(domain);
        SysAccount sysAccount = findOneByExample(example);
        sysAccount.setUpdateTime(LocalDateTime.now());
        sysAccount.setStatus(status);

        entityRepository.save(sysAccount);
    }

    /**
     * 重置用户密码
     *
     * @param userId
     * @param domain
     * @param password
     */
    public void updatePasswordByUserId(Long userId, String domain, String password) {

        CriteriaQueryWrapper<SysAccount> criteria = new CriteriaQueryWrapper<>();
        criteria.in(SysAccount::getAccountType, BaseConstants.ACCOUNT_TYPE_USERNAME, BaseConstants.ACCOUNT_TYPE_EMAIL, BaseConstants.ACCOUNT_TYPE_MOBILE)
                .eq(SysAccount::getUserId, userId)
                .eq(SysAccount::getDomain, domain);
        List<SysAccount> data = findAllByCriteria(criteria);
        for (SysAccount entity : data) {
            entity.setPassword(passwordEncoder.encode(password));
            entity.setUpdateTime(LocalDateTime.now());
            save(entity);
        }
    }

    /**
     * 根据用户ID删除账号
     *
     * @param userId
     * @param domain
     * @return
     */
    public void removeAccountByUserId(Long userId, String domain) {
        CriteriaQueryWrapper<SysAccount> wrapper = new CriteriaQueryWrapper();
        wrapper.eq(SysAccount::getUserId, userId)
               .eq(SysAccount::getDomain, domain);

        deleteByCriteria(wrapper);
    }


    /**
     * 添加登录日志
     *
     * @param log
     */
    public void addLoginLog(BaseAccountLogs log) {
        BaseAccountLogs accountLogs = new BaseAccountLogs();
        accountLogs.setAccountId(log.getAccountId());
        accountLogs.setUserId(log.getUserId());

        long count = baseAccountLogsRepository.count(Example.of(accountLogs));
        log.setLoginTime(new Date());
        log.setLoginNums(count + 1);
        baseAccountLogsRepository.save(log);
    }
}
