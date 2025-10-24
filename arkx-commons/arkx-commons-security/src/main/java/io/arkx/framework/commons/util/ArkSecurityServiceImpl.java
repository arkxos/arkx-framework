package io.arkx.framework.commons.util;

import io.arkx.framework.commons.ArkSecurityService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
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
