package com.rapidark.cloud.gateway.manage.repository;

import com.rapidark.cloud.base.client.model.entity.BaseDeveloper;
import com.rapidark.cloud.base.server.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/24 14:26
 */
public interface BaseDeveloperRepository extends BaseRepository<BaseDeveloper, String> {

    Optional<BaseDeveloper> findByUserName(@Param("userName") String userName);

}
