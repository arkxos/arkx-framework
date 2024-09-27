package com.rapidark.cloud.base.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rapidark.cloud.base.client.model.entity.GatewayAccessLogs;
import com.rapidark.cloud.base.server.mapper.GatewayLogsMapper;
import com.rapidark.common.model.PageParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 网关访问日志
 *
 * @author liuyadu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GatewayAccessLogsService {

    @Autowired
    private GatewayLogsMapper gatewayLogsMapper;

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    public IPage<GatewayAccessLogs> findListPage(PageParams pageParams) {
        GatewayAccessLogs query = pageParams.mapToObject(GatewayAccessLogs.class);
        QueryWrapper<GatewayAccessLogs> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(ObjectUtils.isNotEmpty(query.getBizId()), GatewayAccessLogs::getBizId, query.getBizId())
                .eq(ObjectUtils.isNotEmpty(query.getBizStatus()), GatewayAccessLogs::getBizStatus, query.getBizStatus())
                .like(ObjectUtils.isNotEmpty(query.getPath()), GatewayAccessLogs::getPath, query.getPath())
                .eq(ObjectUtils.isNotEmpty(query.getIp()), GatewayAccessLogs::getIp, query.getIp())
                .eq(ObjectUtils.isNotEmpty(query.getServiceId()), GatewayAccessLogs::getServiceId, query.getServiceId());
        queryWrapper.orderByDesc("request_time");
        return gatewayLogsMapper.selectPage(pageParams, queryWrapper);
    }

}
