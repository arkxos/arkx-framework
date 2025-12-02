package io.arkx.framework.core.castor;

import io.arkx.framework.commons.util.StringUtil;

/**
 * 字符串数组类型转换器
 *
 */
public class StringArrayCastor extends AbstractCastor {
    private static StringArrayCastor singleton = new StringArrayCastor();

    public static StringArrayCastor getInstance() {
        return singleton;
    }

    private StringArrayCastor() {
    }

    @Override
    public boolean canCast(Class<?> type) {
        return String[].class == type;
    }

    @Override
    public Object cast(Object obj, Class<?> type) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String[]) {
            return obj;
        } else if (obj.getClass().isArray()) {
            Object[] os = (Object[]) obj;
            String[] arr = new String[os.length];
            for (int i = 0; i < os.length; i++) {
                arr[i] = os[i] == null ? null : os[i].toString();
            }
            return arr;
        } else if (obj instanceof String && obj.toString().indexOf(",") > 0) {
            return StringUtil.splitEx(obj.toString(), ",");
        } else {
            return new String[]{obj.toString()};
        }
    }

}
