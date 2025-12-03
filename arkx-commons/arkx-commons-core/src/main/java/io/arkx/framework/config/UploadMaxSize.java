package io.arkx.framework.config;

import io.arkx.framework.Config;
import io.arkx.framework.commons.util.NumberUtil;

/**
 * 最大文件上传大小，以字节为单位。<br>
 *
 */
public class UploadMaxSize implements IApplicationConfigItem {

    public static final String ID = "UploadMaxSize";

    public static final int DEFAULT = 2 * 1024 * 1024 * 1024;

    private static int max = -1;

    @Override
    public String getExtendItemID() {
        return ID;
    }

    @Override
    public String getExtendItemName() {
        return "Maxiumn file size of upload";
    }

    public static int getValue() {
        if (max < 0) {
            String str = Config.getValue("App." + ID);
            if (NumberUtil.isInt(str)) {
                max = Integer.parseInt(str);
            } else {
                max = DEFAULT;
            }
        }
        return max;
    }

}
