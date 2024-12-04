package com.rapidark.cloud.base.server.repository;

import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.platform.system.api.entity.OpenApp;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/5/25 11:18
 */
public interface OpenAppRepository extends BaseRepository<OpenApp, String> {

    Optional<OpenApp> findByIp(@Param("ip") String ip);

}
