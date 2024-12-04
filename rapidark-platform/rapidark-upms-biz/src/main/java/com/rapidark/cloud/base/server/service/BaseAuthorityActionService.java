package com.rapidark.cloud.base.server.service;

import com.rapidark.platform.system.api.entity.BaseAuthorityAction;
import com.rapidark.cloud.base.server.repository.BaseAuthorityActionRepository;
import com.rapidark.framework.data.jpa.service.BaseService;
import org.springframework.stereotype.Service;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/30 15:33
 */
@Service
public class BaseAuthorityActionService extends BaseService<BaseAuthorityAction, Long, BaseAuthorityActionRepository> {
}
