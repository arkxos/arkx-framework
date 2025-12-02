package io.arkx.framework.config;

import io.arkx.framework.Config;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;

public class ContainerStartUser implements IApplicationConfigItem {
    public static final String ID = "ContainerStartUser";

    public String getExtendItemID() {
        return ID;
    }

    public String getExtendItemName() {
        return "Container start user";
    }

    public static String getValue() {
        return Config.getValue("App." + ID);
    }

    public static void isMatch() {
        String v = getValue();
        if (StringUtil.isEmpty(v)) {
            return;
        }
        String user = System.getProperty("user.name");
        if (ObjectUtil.equal(user, getValue())) {
            return;
        }
        LogUtil.error(
                "Container start user not match the config value in " + Config.getContextRealPath() + "framework.xml!");
        System.exit(1);
    }
}
