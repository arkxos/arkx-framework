package io.arkx.framework.commons.util;

import java.util.List;

import org.springframework.stereotype.Service;

import io.arkx.framework.commons.ArkSecurityService;

/**
 * @author Nobody
 * @version 1.0
 * @date 2025-10-21 15:42
 * @since 1.0
 */
@Service
public class ArkSecurityServiceImpl implements ArkSecurityService {

	@Override
	public List<Long> getCurrentUserDataScope() {
		return SecurityUtils.getCurrentUserDataScope();
	}

	@Override
	public String getCurrentUsername() {
		return SecurityUtils.getCurrentUsername();
	}

}
