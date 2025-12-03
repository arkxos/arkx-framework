package io.arkx.framework.config;

import io.arkx.framework.Config;

/**
 * 配置当前中间件的默认servlet的名称。
 *
 */
public class DefaultServletName implements IApplicationConfigItem {

    public static final String ID = "DefaultServletName";

    @Override
    public String getExtendItemID() {
        return ID;
    }

    @Override
    public String getExtendItemName() {
        return "Default servlet's name in current container";
    }

    public static String getValue() {
        return Config.getValue("App." + ID);
    }

}
