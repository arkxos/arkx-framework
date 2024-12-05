package com.rapidark.cloud.base.server.repository;

import com.rapidark.platform.system.api.entity.IpLimitApi;
import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import com.rapidark.platform.system.api.entity.GatewayIpLimitApi;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface GatewayIpLimitApiRepository extends BaseRepository<GatewayIpLimitApi, Long> {

    @SqlToyQuery
    List<IpLimitApi> selectIpLimitApi(@Param("policyType") int policyType);

    @SqlToyQuery
    List<GatewayIpLimitApi> queryByPolicyId(@Param("policyId") Long policyId);

    void deleteByPolicyId(Long policyId);

    void deleteByApiId(Long apiId);
}
