package com.rapidark.cloud.base.server.repository;

import com.rapidark.cloud.base.client.model.entity.BaseApi;
import com.rapidark.cloud.base.client.model.entity.BaseUser;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:23
 */
public interface BaseUserRepository extends BaseRepository<BaseUser, String> {

    BaseUser findByUserName(String username);

}
