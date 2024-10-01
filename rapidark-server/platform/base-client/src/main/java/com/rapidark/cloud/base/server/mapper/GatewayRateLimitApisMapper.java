package com.rapidark.cloud.base.server.mapper;

import com.rapidark.cloud.base.client.model.RateLimitApi;
import com.rapidark.cloud.base.client.model.entity.GatewayRateLimitApi;
import com.rapidark.common.mybatis.base.mapper.SuperMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuyadu
 */
@Repository
public interface GatewayRateLimitApisMapper extends SuperMapper<GatewayRateLimitApi> {
    List<RateLimitApi> selectRateLimitApi();
}
