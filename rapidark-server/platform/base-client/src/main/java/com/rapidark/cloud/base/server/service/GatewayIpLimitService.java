package com.rapidark.cloud.base.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rapidark.cloud.base.client.model.IpLimitApi;
import com.rapidark.cloud.base.client.model.entity.GatewayIpLimit;
import com.rapidark.cloud.base.client.model.entity.GatewayIpLimitApi;
import com.rapidark.cloud.base.server.mapper.GatewayIpLimitApisMapper;
import com.rapidark.cloud.base.server.mapper.GatewayIpLimitMapper;
import com.rapidark.cloud.base.server.service.GatewayIpLimitService;
import com.rapidark.common.model.PageParams;
import com.rapidark.common.mybatis.base.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 *网关IP访问控制
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GatewayIpLimitService extends BaseServiceImpl<GatewayIpLimitMapper, GatewayIpLimit> {
    @Autowired
    private GatewayIpLimitMapper gatewayIpLimitMapper;
    @Autowired
    private GatewayIpLimitApisMapper gatewayIpLimitApisMapper;


    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    public IPage<GatewayIpLimit> findListPage(PageParams pageParams) {
        GatewayIpLimit query = pageParams.mapToObject(GatewayIpLimit.class);
        QueryWrapper<GatewayIpLimit> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .likeRight(ObjectUtils.isNotEmpty(query.getPolicyName()), GatewayIpLimit::getPolicyName, query.getPolicyName())
                .eq(ObjectUtils.isNotEmpty(query.getPolicyType()), GatewayIpLimit::getPolicyType, query.getPolicyType());
        queryWrapper.orderByDesc("create_time");
        return gatewayIpLimitMapper.selectPage((IPage<GatewayIpLimit>)pageParams, queryWrapper);
    }

    /**
     * 查询白名单
     *
     * @return
     */
    public List<IpLimitApi> findBlackList() {
        List<IpLimitApi> list = gatewayIpLimitApisMapper.selectIpLimitApi(0);
        return list;
    }

    /**
     * 查询黑名单
     *
     * @return
     */
    public List<IpLimitApi> findWhiteList() {
        List<IpLimitApi> list = gatewayIpLimitApisMapper.selectIpLimitApi(1);
        return list;
    }

    /**
     * 查询策略已绑定API列表
     *
     * @return
     */
    public List<GatewayIpLimitApi> findIpLimitApiList(Long policyId) {
        QueryWrapper<GatewayIpLimitApi> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(GatewayIpLimitApi::getPolicyId, policyId);
        List<GatewayIpLimitApi> list = gatewayIpLimitApisMapper.selectList(queryWrapper);
        return list;
    }

    /**
     * 获取IP限制策略
     *
     * @param policyId
     * @return
     */
    public GatewayIpLimit getIpLimitPolicy(String policyId) {
        return gatewayIpLimitMapper.selectById(policyId);
    }

    /**
     * 添加IP限制策略
     *
     * @param policy
     */
    public GatewayIpLimit addIpLimitPolicy(GatewayIpLimit policy) {
        policy.setCreateTime(new Date());
        policy.setUpdateTime(policy.getCreateTime());
        gatewayIpLimitMapper.insert(policy);
        return policy;
    }

    /**
     * 更新IP限制策略
     *
     * @param policy
     */
    public GatewayIpLimit updateIpLimitPolicy(GatewayIpLimit policy) {
        policy.setUpdateTime(new Date());
        gatewayIpLimitMapper.updateById(policy);
        return policy;
    }

    /**
     * 删除IP限制策略
     *
     * @param policyId
     */
    public void removeIpLimitPolicy(String policyId) {
        clearIpLimitApisByPolicyId(policyId);
        gatewayIpLimitMapper.deleteById(policyId);
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
                gatewayIpLimitApisMapper.insert(item);
            }
        }
    }

    /**
     * 清空绑定的API
     *
     * @param policyId
     */
    public void clearIpLimitApisByPolicyId(String policyId) {
        QueryWrapper<GatewayIpLimitApi> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(GatewayIpLimitApi::getPolicyId, policyId);
        gatewayIpLimitApisMapper.delete(queryWrapper);
    }

    /**
     * API解除所有策略
     *
     * @param apiId
     */
    public void clearIpLimitApisByApiId(String apiId) {
        QueryWrapper<GatewayIpLimitApi> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(GatewayIpLimitApi::getApiId, apiId);
        gatewayIpLimitApisMapper.delete(queryWrapper);
    }
}
