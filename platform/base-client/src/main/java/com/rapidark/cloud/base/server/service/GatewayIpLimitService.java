package com.rapidark.cloud.base.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rapidark.cloud.base.client.model.IpLimitApi;
import com.rapidark.cloud.base.client.model.entity.GatewayIpLimit;
import com.rapidark.cloud.base.client.model.entity.GatewayIpLimitApi;
import com.rapidark.cloud.base.server.repository.GatewayIpLimitApiRepository;
import com.rapidark.cloud.base.server.repository.GatewayIpLimitRepository;
import com.rapidark.cloud.base.server.service.GatewayIpLimitService;
import com.rapidark.cloud.gateway.formwork.base.BaseService;
import com.rapidark.common.model.PageParams;
import com.rapidark.common.mybatis.base.service.impl.BaseServiceImpl;
import com.rapidark.common.utils.CriteriaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 *网关IP访问控制
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GatewayIpLimitService extends BaseService<GatewayIpLimit, String, GatewayIpLimitRepository> {

    @Autowired
    private GatewayIpLimitApiRepository gatewayIpLimitApiRepository;


    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    public Page<GatewayIpLimit> findListPage(PageParams pageParams) {
        GatewayIpLimit query = pageParams.mapToObject(GatewayIpLimit.class);
        CriteriaQueryWrapper<GatewayIpLimit> queryWrapper = new CriteriaQueryWrapper();
        queryWrapper
                .likeRight(ObjectUtils.isNotEmpty(query.getPolicyName()), GatewayIpLimit::getPolicyName, query.getPolicyName())
                .eq(ObjectUtils.isNotEmpty(query.getPolicyType()), GatewayIpLimit::getPolicyType, query.getPolicyType()+"");
//        queryWrapper.orderByDesc("create_time");
        Pageable pageable = PageRequest.of(pageParams.getPage(), pageParams.getLimit(),
                Sort.by(Sort.Direction.DESC, "createTime"));
        return findAllByCriteria(queryWrapper, pageable);
    }

    /**
     * 查询白名单
     *
     * @return
     */
    public List<IpLimitApi> findBlackList() {
        List<IpLimitApi> list = gatewayIpLimitApiRepository.selectIpLimitApi(0);
        return list;
    }

    /**
     * 查询黑名单
     *
     * @return
     */
    public List<IpLimitApi> findWhiteList() {
        List<IpLimitApi> list = gatewayIpLimitApiRepository.selectIpLimitApi(1);
        return list;
    }

    /**
     * 查询策略已绑定API列表
     *
     * @return
     */
    public List<GatewayIpLimitApi> findIpLimitApiList(Long policyId) {
        List<GatewayIpLimitApi> list = gatewayIpLimitApiRepository.queryByPolicyId(policyId);
        return list;
    }

    /**
     * 获取IP限制策略
     *
     * @param policyId
     * @return
     */
    public GatewayIpLimit getIpLimitPolicy(String policyId) {
        return findById(policyId);
    }

    /**
     * 添加IP限制策略
     *
     * @param policy
     */
    public GatewayIpLimit addIpLimitPolicy(GatewayIpLimit policy) {
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
    public GatewayIpLimit updateIpLimitPolicy(GatewayIpLimit policy) {
        policy.setUpdateTime(LocalDateTime.now());
        save(policy);
        return policy;
    }

    /**
     * 删除IP限制策略
     *
     * @param policyId
     */
    public void removeIpLimitPolicy(String policyId) {
        clearIpLimitApisByPolicyId(policyId);
        deleteById(policyId);
    }

    /**
     * 绑定API, 一个API只能绑定一个策略
     *
     * @param policyId
     * @param apis
     */
    public void addIpLimitApis(String policyId, String... apis) {
        // 先清空策略已有绑定
        clearIpLimitApisByPolicyId(policyId);
        if (apis != null && apis.length > 0) {
            for (String apiId : apis) {
                // 先api解除所有绑定, 一个API只能绑定一个策略
                clearIpLimitApisByApiId(apiId);
                GatewayIpLimitApi item = new GatewayIpLimitApi();
                item.setApiId(apiId);
                item.setPolicyId(policyId);
                // 重新绑定策略
                gatewayIpLimitApiRepository.save(item);
            }
        }
    }

    /**
     * 清空绑定的API
     *
     * @param policyId
     */
    public void clearIpLimitApisByPolicyId(String policyId) {
        gatewayIpLimitApiRepository.deleteByPolicyId(policyId);
    }

    /**
     * API解除所有策略
     *
     * @param apiId
     */
    public void clearIpLimitApisByApiId(String apiId) {
        gatewayIpLimitApiRepository.deleteByApiId(apiId);
    }
}
