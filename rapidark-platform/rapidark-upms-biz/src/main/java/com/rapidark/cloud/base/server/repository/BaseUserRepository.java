package com.rapidark.cloud.base.server.repository;

import com.rapidark.cloud.base.client.model.entity.BaseUser;
import com.rapidark.framework.data.jpa.BaseRepository;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface BaseUserRepository extends BaseRepository<BaseUser, Long> {

    BaseUser findByUserName(String username);

}
