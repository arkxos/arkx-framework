package com.rapidark.cloud.base.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.base.client.model.entity.BaseAccount;
import com.rapidark.cloud.base.client.model.entity.BaseAccountLogs;
import com.rapidark.cloud.base.server.repository.BaseAccountLogsRepository;
import com.rapidark.cloud.base.server.repository.BaseAccountRepository;
import com.rapidark.framework.commons.utils.CriteriaQueryWrapper;
import com.rapidark.cloud.gateway.formwork.base.BaseService;
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
public class BaseAccountService extends BaseService<BaseAccount, Long, BaseAccountRepository> {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BaseAccountLogsRepository baseAccountLogsRepository;


    /**
     * 根据主键获取账号信息
     *
     * @param accountId
     * @return
     */
    public BaseAccount getAccountById(Long accountId) {
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
    public BaseAccount getAccount(String account, String accountType, String domain) {
        BaseAccount criteria = new BaseAccount();
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
    public BaseAccount register(Long userId, String account, String password, String accountType, Integer status, String domain, String registerIp) {
        if (isExist(account, accountType, domain)) {
            // 账号已被注册
            throw new RuntimeException(String.format("account=[%s],domain=[%s]", account, domain));
        }
        //加密
        String encodePassword = passwordEncoder.encode(password);
        BaseAccount baseAccount = new BaseAccount(userId, account, encodePassword, accountType, domain, registerIp);
        baseAccount.setCreateTime(LocalDateTime.now());
        baseAccount.setUpdateTime(baseAccount.getCreateTime());
        baseAccount.setStatus(status);
        entityRepository.save(baseAccount);
        return baseAccount;
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
        QueryWrapper<BaseAccount> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(BaseAccount::getAccount, account)
                .eq(BaseAccount::getAccountType, accountType)
                .eq(BaseAccount::getDomain, domain);
        BaseAccount example = new BaseAccount();
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
    public void updateStatus(Long accountId, Integer status) {
        BaseAccount baseAccount = findById(accountId);
        baseAccount.setUpdateTime(LocalDateTime.now());
        baseAccount.setStatus(status);
        entityRepository.save(baseAccount);
    }

    /**
     * 根据用户更新账户状态
     *
     * @param userId
     * @param domain
     * @param status
     */
    public void updateStatusByUserId(Long userId, String domain, Integer status) {
        if (status == null) {
            return;
        }
        BaseAccount example = new BaseAccount();
        example.setUserId(userId);
        example.setDomain(domain);
        BaseAccount baseAccount = findOneByExample(example);
        baseAccount.setUpdateTime(LocalDateTime.now());
        baseAccount.setStatus(status);

        entityRepository.save(baseAccount);
    }

    /**
     * 重置用户密码
     *
     * @param userId
     * @param domain
     * @param password
     */
    public void updatePasswordByUserId(Long userId, String domain, String password) {

        CriteriaQueryWrapper<BaseAccount> criteria = new CriteriaQueryWrapper<>();
        criteria.in(BaseAccount::getAccountType, BaseConstants.ACCOUNT_TYPE_USERNAME, BaseConstants.ACCOUNT_TYPE_EMAIL, BaseConstants.ACCOUNT_TYPE_MOBILE)
                .eq(BaseAccount::getUserId, userId)
                .eq(BaseAccount::getDomain, domain);
        List<BaseAccount> data = findAllByCriteria(criteria);
        for (BaseAccount entity : data) {
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
        CriteriaQueryWrapper<BaseAccount> wrapper = new CriteriaQueryWrapper();
        wrapper.eq(BaseAccount::getUserId, userId)
               .eq(BaseAccount::getDomain, domain);

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
