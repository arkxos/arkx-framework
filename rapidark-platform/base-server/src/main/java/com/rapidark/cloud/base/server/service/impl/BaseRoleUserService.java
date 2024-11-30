package com.rapidark.cloud.base.server.service.impl;

import com.rapidark.cloud.base.client.model.entity.BaseRoleUser;
import com.rapidark.cloud.base.server.repository.BaseRoleUserRepository;
import com.rapidark.framework.data.jpa.service.BaseService;
import org.springframework.stereotype.Service;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/30 15:33
 */
@Service
public class BaseRoleUserService extends BaseService<BaseRoleUser, Long, BaseRoleUserRepository> {
}
