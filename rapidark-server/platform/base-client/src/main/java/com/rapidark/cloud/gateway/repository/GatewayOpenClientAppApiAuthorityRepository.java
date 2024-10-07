package com.rapidark.cloud.gateway.repository;

import com.rapidark.cloud.base.client.model.GatewayOpenClientAppApiAuthority;
import com.rapidark.common.security.OpenAuthority;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/1 16:10
 */
public interface GatewayOpenClientAppApiAuthorityRepository extends JpaRepository<GatewayOpenClientAppApiAuthority, String>, JpaSpecificationExecutor<GatewayOpenClientAppApiAuthority> {

    /**
     * 获取应用已授权权限
     *
     * @param appId
     * @return
     */
    @SqlToyQuery
    List<OpenAuthority> queryAuthoritysByAppIdAndAppSystemCode(@Param("appId") String appId, @Param("appSystemCode") String appSystemCode);

    void deleteByAppId(String appId);

    void deleteByAppIdAndAppSystemCode(String appId, String appSystemCode);

    @SqlToyQuery
    void deleteByAuthorityIds(List<String> invalidAuthorityIds);
}

