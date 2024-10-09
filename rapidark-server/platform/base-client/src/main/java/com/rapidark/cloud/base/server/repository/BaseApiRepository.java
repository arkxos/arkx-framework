package com.rapidark.cloud.base.server.repository;

import com.rapidark.cloud.base.client.model.entity.BaseApi;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface BaseApiRepository extends BaseRepository<BaseApi, String> {

    @SqlToyQuery
    List<BaseApi> queryByServiceId(@Param("serviceId") String serviceId);

    BaseApi findByApiCode(String apiCode);
}
