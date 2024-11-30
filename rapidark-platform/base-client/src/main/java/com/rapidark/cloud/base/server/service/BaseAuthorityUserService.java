package com.rapidark.cloud.base.server.service;

import com.rapidark.cloud.base.client.model.entity.BaseAuthorityRole;
import com.rapidark.cloud.base.client.model.entity.BaseAuthorityUser;
import com.rapidark.cloud.base.server.repository.BaseAuthorityRoleRepository;
import com.rapidark.cloud.base.server.repository.BaseAuthorityUserRepository;
import com.rapidark.cloud.gateway.formwork.base.BaseService;
import org.springframework.stereotype.Service;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/30 15:33
 */
@Service
public class BaseAuthorityUserService extends BaseService<BaseAuthorityUser, Long, BaseAuthorityUserRepository> {
}
