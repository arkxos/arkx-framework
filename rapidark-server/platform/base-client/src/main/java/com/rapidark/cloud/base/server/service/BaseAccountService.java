package com.rapidark.cloud.base.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.base.client.model.entity.BaseAccount;
import com.rapidark.cloud.base.client.model.entity.BaseAccountLogs;
import com.rapidark.cloud.base.server.mapper.BaseAccountLogsMapper;
import com.rapidark.cloud.base.server.repository.BaseAccountRepository;
import com.rapidark.common.utils.CriteriaQueryWrapper;
import com.rapidark.cloud.base.server.service.query.AccountQueryCriteria;
import com.rapidark.cloud.base.server.service.query.AccountTypeInQueryCriteria;
import com.rapidark.cloud.gateway.formwork.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
public class BaseAccountService extends BaseService<BaseAccount, String, BaseAccountRepository> {

    @Autowired
    private BaseAccountLogsMapper baseAccountLogsMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * 根据主键获取账号信息
     *
     * @param accountId
     * @return
     */
    public BaseAccount getAccountById(String accountId) {
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
        AccountQueryCriteria criteria = new AccountQueryCriteria();
        criteria.setAccount(account);
        criteria.setAccountType(accountType);
        criteria.setDomain(domain);
        return findOneByCriteria(criteria);
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
    public BaseAccount register(String userId, String account, String password, String accountType, Integer status, String domain, String registerIp) {
        if (isExist(account, accountType, domain)) {
            // 账号已被注册
            throw new RuntimeException(String.format("account=[%s],domain=[%s]", account, domain));
        }
        //加密
        String encodePassword = passwordEncoder.encode(password);
        BaseAccount baseAccount = new BaseAccount(userId, account, encodePassword, accountType, domain, registerIp);
        baseAccount.setCreateTime(new Date());
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
    public void removeAccount(String accountId) {
        deleteById(accountId);
    }


    /**
     * 更新账号状态
     *
     * @param accountId
     * @param status
     */
    public void updateStatus(String accountId, Integer status) {
        BaseAccount baseAccount = findById(accountId);
        baseAccount.setUpdateTime(new Date());
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
    public void updateStatusByUserId(String userId, String domain, Integer status) {
        if (status == null) {
            return;
        }
        BaseAccount example = new BaseAccount();
        example.setUserId(userId);
        example.setDomain(domain);
        BaseAccount baseAccount = findOneByExample(example);
        baseAccount.setUpdateTime(new Date());
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
    public void updatePasswordByUserId(String userId, String domain, String password) {

        CriteriaQueryWrapper<BaseAccount> criteria = new CriteriaQueryWrapper<>();
        criteria.in(BaseAccount::getAccountType, BaseConstants.ACCOUNT_TYPE_USERNAME, BaseConstants.ACCOUNT_TYPE_EMAIL, BaseConstants.ACCOUNT_TYPE_MOBILE)
                .eq(BaseAccount::getUserId, userId)
                .eq(BaseAccount::getDomain, domain);
        List<BaseAccount> data = findAllByCriteria(criteria);
        for (BaseAccount entity : data) {
            entity.setPassword(passwordEncoder.encode(password));
            entity.setUpdateTime(new Date());
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
    public void removeAccountByUserId(String userId, String domain) {
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
        QueryWrapper<BaseAccountLogs> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(BaseAccountLogs::getAccountId, log.getAccountId())
                .eq(BaseAccountLogs::getUserId, log.getUserId());
        int count = baseAccountLogsMapper.selectCount(queryWrapper);
        log.setLoginTime(new Date());
        log.setLoginNums(count + 1);
        baseAccountLogsMapper.insert(log);
    }
}
