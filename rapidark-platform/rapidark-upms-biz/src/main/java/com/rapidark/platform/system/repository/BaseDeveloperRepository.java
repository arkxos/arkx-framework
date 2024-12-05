package com.rapidark.platform.system.repository;

import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.platform.system.api.entity.BaseDeveloper;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/24 14:26
 */
public interface BaseDeveloperRepository extends BaseRepository<BaseDeveloper, Long> {

    Optional<BaseDeveloper> findByUserName(@Param("userName") String userName);

}
