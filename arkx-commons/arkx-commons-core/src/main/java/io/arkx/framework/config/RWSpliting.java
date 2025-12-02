package io.arkx.framework.config;

import io.arkx.framework.Config;
import io.arkx.framework.commons.util.ObjectUtil;

/**
 * 配置是否开启读写分离特性。<br>
 * 值为true则开启，为false时关闭。
 *
 */
public class RWSpliting implements IApplicationConfigItem {
    public static final String ID = "RWSpliting";

    @Override
    public String getExtendItemID() {
        return ID;
    }

    @Override
    public String getExtendItemName() {
        return "Read/Write Spliting Mode";
    }

    public static boolean getValue() {
        return ObjectUtil.isTrue(Config.getValue("App." + ID));
    }
}
