package io.arkx.framework.commons;

import java.util.List;

/**
 * @author Nobody
 * @version 1.0
 * @date 2025-10-21 15:41
 * @since 1.0
 */
public interface ArkSecurityService {

    List<Long> getCurrentUserDataScope();

    String getCurrentUsername();

}
