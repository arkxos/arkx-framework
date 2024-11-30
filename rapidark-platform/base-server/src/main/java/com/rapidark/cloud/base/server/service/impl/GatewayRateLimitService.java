package com.rapidark.cloud.base.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rapidark.cloud.base.client.model.RateLimitApi;
import com.rapidark.cloud.base.client.model.entity.GatewayRateLimit;
import com.rapidark.cloud.base.client.model.entity.GatewayRateLimitApi;
import com.rapidark.cloud.base.server.repository.GatewayRateLimitApiRepository;
import com.rapidark.cloud.base.server.repository.GatewayRateLimitRepository;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.framework.common.model.PageParams;
import com.rapidark.framework.common.utils.CriteriaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liuyadu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GatewayRateLimitService extends BaseService<GatewayRateLimit, Long, GatewayRateLimitRepository> {

    @Autowired
    private GatewayRateLimitApiRepository gatewayRateLimitApiRepository;

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    public Page<GatewayRateLimit> findListPage(PageParams pageParams) {
        GatewayRateLimit query = pageParams.mapToObject(GatewayRateLimit.class);
        CriteriaQueryWrapper<GatewayRateLimit> queryWrapper = new CriteriaQueryWrapper();
        queryWrapper
                .likeRight(ObjectUtils.isNotEmpty(query.getPolicyName()), GatewayRateLimit::getPolicyName, query.getPolicyName())
                .eq(ObjectUtils.isNotEmpty(query.getPolicyType()), GatewayRateLimit::getPolicyType, query.getPolicyType());
//        queryWrapper.orderByDesc("create_time");
        Pageable pageable = PageRequest.of(pageParams.getPage(), pageParams.getLimit(),
                Sort.by(Sort.Direction.DESC, "createTime"));
        return findAllByCriteria(queryWrapper, pageable);
    }

    /**
     * 查询接口流量限制
     *
     * @return
     */
    public List<RateLimitApi> findRateLimitApiList() {
        List<RateLimitApi> list = gatewayRateLimitApiRepository.selectRateLimitApi();
        return list;
    }

    /**
     * 查询策略已绑定API列表
     *
     * @param policyId
     * @return
     */
    public List<GatewayRateLimitApi> findRateLimitApiList(Long policyId) {
        QueryWrapper<GatewayRateLimitApi> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(GatewayRateLimitApi::getPolicyId, policyId);
        List<GatewayRateLimitApi> list = gatewayRateLimitApiRepository.queryByPolicyId(policyId);
        return list;
    }

    /**
     * 获取IP限制策略
     *
     * @param policyId
     * @return
     */
    public GatewayRateLimit getRateLimitPolicy(Long policyId) {
        return findById(policyId);
    }

    /**
     * 添加IP限制策略
     *
     * @param policy
     */
    public GatewayRateLimit addRateLimitPolicy(GatewayRateLimit policy) {
        policy.setCreateTime(LocalDateTime.now());
        policy.setUpdateTime(policy.getCreateTime());
        save(policy);
        return policy;
    }

    /**
     * 更新IP限制策略
     *
     * @param policy
     */
    public GatewayRateLimit updateRateLimitPolicy(GatewayRateLimit policy) {
        policy.setUpdateTime(LocalDateTime.now());
        save(policy);
        return policy;
    }

    /**
     * 删除IP限制策略
     *
     * @param policyId
     */
    public void removeRateLimitPolicy(Long policyId) {
        clearRateLimitApisByPolicyId(policyId);
        deleteById(policyId);
    }

    /**
     * 绑定API, 一个API只能绑定一个策略
     *
     * @param policyId
     * @param apis
     */
    public void addRateLimitApis(Long policyId, String... apis) {
        // 先清空策略已有绑定
        clearRateLimitApisByPolicyId(policyId);
        if (apis != null && apis.length > 0) {
            for (String apiId : apis) {
                // 先api解除所有绑定, 一个API只能绑定一个策略
                clearRateLimitApisByApiId(Long.valueOf(apiId));
                GatewayRateLimitApi item = new GatewayRateLimitApi();
                item.setApiId(Long.valueOf(apiId));
                item.setPolicyId(policyId);
                // 重新绑定策略
                gatewayRateLimitApiRepository.save(item);
            }
        }
    }

    /**
     * 清空绑定的API
     *
     * @param policyId
     */
    public void clearRateLimitApisByPolicyId(Long policyId) {
        gatewayRateLimitApiRepository.deleteByPolicyId(policyId);
    }

    /**
     * API解除所有策略
     *
     * @param apiId
     */
    public void clearRateLimitApisByApiId(Long apiId) {
        gatewayRateLimitApiRepository.deleteByApiId(apiId);
    }
}
