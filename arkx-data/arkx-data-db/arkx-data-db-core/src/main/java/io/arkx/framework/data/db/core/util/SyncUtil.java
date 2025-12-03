package io.arkx.framework.data.db.core.util;

/**
 * @author Nobody
 * @date 2025-07-04 16:24
 * @since 1.0
 */
public class SyncUtil {

    public static boolean isShardDbSync(String dbSyncMode) {
        return "SHARD_TO_MASTER".equalsIgnoreCase(dbSyncMode);
    }

}
