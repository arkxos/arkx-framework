package com.rapidark.cloud.base.server.mapper;

import com.rapidark.cloud.base.client.model.IpLimitApi;
import com.rapidark.cloud.base.client.model.entity.GatewayIpLimitApi;
import com.rapidark.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuyadu
 */
@Repository
public interface GatewayIpLimitApisMapper extends SuperMapper<GatewayIpLimitApi> {
    List<IpLimitApi> selectIpLimitApi(@Param("policyType") int policyType);
}
