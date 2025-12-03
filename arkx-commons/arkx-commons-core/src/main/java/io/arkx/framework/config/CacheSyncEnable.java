package io.arkx.framework.config;

import io.arkx.framework.Config;

/**
 * 是否启用缓存同步，集群时必须启用。
 *
 */
public class CacheSyncEnable implements IApplicationConfigItem {

    public static final String ID = "CacheSyncEnable";

    @Override
    public String getExtendItemID() {
        return ID;
    }

    @Override
    public String getExtendItemName() {
        return "Cache sync enabled";
    }

    public static String getValue() {
        return Config.getValue("App." + ID);
    }

}
