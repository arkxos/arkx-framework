package com.rapidark.platform.system.service;

import com.rapidark.platform.system.repository.BaseAuthorityUserRepository;
import com.rapidark.framework.data.jpa.service.BaseService;
import com.rapidark.platform.system.api.entity.BaseAuthorityUser;
import org.springframework.stereotype.Service;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/30 15:33
 */
@Service
public class BaseAuthorityUserService extends BaseService<BaseAuthorityUser, Long, BaseAuthorityUserRepository> {
}
