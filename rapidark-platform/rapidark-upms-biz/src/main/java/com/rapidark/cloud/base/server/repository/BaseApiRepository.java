package com.rapidark.cloud.base.server.repository;

import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyQuery;
import com.rapidark.platform.system.api.entity.BaseApi;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface BaseApiRepository extends BaseRepository<BaseApi, Long> {

    @SqlToyQuery
    List<BaseApi> queryByServiceId(@Param("serviceId") String serviceId);

    BaseApi findByApiCode(String apiCode);
}
