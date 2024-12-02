package com.rapidark.cloud.base.server.service;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.rapidark.cloud.base.client.model.entity.GatewayAccessLogs;
import com.rapidark.cloud.base.server.repository.GatewayAccessLogsRepository;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.framework.common.utils.CriteriaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class GatewayAccessLogsService extends BaseService<GatewayAccessLogs, Long, GatewayAccessLogsRepository> {

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    public Page<GatewayAccessLogs> findListPage(PageParams pageParams) {
        GatewayAccessLogs query = pageParams.mapToObject(GatewayAccessLogs.class);
        CriteriaQueryWrapper<GatewayAccessLogs> queryWrapper = new CriteriaQueryWrapper<>();
        queryWrapper
                .eq(ObjectUtils.isNotEmpty(query.getBizId()), GatewayAccessLogs::getBizId, query.getBizId())
                .eq(ObjectUtils.isNotEmpty(query.getBizStatus()), GatewayAccessLogs::getBizStatus, query.getBizStatus()+"")
//                .like(ObjectUtils.isNotEmpty(query.getPath()), GatewayAccessLogs::getPath, query.getPath())
                .eq(ObjectUtils.isNotEmpty(query.getIp()), GatewayAccessLogs::getIp, query.getIp())
                .eq(ObjectUtils.isNotEmpty(query.getServiceId()), GatewayAccessLogs::getServiceId, query.getServiceId());
//        queryWrapper.orderByDesc("request_time");
        Pageable pageable = PageRequest.of(pageParams.getPage(), pageParams.getLimit(),
                Sort.by(Sort.Direction.DESC, "requestTime"));
        return findAllByCriteria(queryWrapper, pageable);
    }

}
