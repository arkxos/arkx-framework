package com.rapidark.cloud.base.server.repository;

import com.rapidark.cloud.base.client.model.entity.OpenApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/5/25 11:18
 */
public interface OpenAppRepository extends JpaRepository<OpenApp, String>, JpaSpecificationExecutor<OpenApp> {

    Optional<OpenApp> findByIp(@Param("ip") String ip);

}
