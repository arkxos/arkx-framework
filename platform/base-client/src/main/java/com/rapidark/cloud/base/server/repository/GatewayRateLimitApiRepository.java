package com.rapidark.cloud.base.server.repository;

import com.rapidark.cloud.base.client.model.RateLimitApi;
import com.rapidark.cloud.base.client.model.entity.GatewayRateLimitApi;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface GatewayRateLimitApiRepository extends BaseRepository<GatewayRateLimitApi, Long> {

    @SqlToyQuery
    List<RateLimitApi> selectRateLimitApi();

    @SqlToyQuery
    List<GatewayRateLimitApi> queryByPolicyId(@Param("policyId") Long policyId);

    void deleteByPolicyId(Long policyId);

    void deleteByApiId(Long apiId);
}
