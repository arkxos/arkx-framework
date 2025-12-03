package org.ark.framework.core.castor;

import io.arkx.framework.commons.util.StringUtil;

/**
 * string[]类型转换器
 *
 * @author Darkness
 * @date 2013-3-26 下午08:33:31
 * @version V1.0
 */
public class StringArrayCastor extends AbstractInnerCastor {

    private static StringArrayCastor singleton = new StringArrayCastor();

    public static StringArrayCastor getInstance() {
        return singleton;
    }

    public boolean canCast(Class<?> type) {
        return String[].class == type;
    }

    public Object cast(Object obj, Class<?> type) {
        if (obj == null) {
            return null;
        }
        if ((obj instanceof String[]))
            return (String[]) obj;

        if (obj.getClass().isArray()) {
            Object[] os = (Object[]) obj;
            String[] arr = new String[os.length];
            for (int i = 0; i < os.length; i++) {
                arr[i] = (os[i] == null ? null : os[i].toString());
            }
            return arr;
        }

        if (((obj instanceof String)) && (obj.toString().indexOf(",") > 0)) {
            return StringUtil.splitEx(obj.toString(), ",");
        }
        return new String[]{obj.toString()};
    }

}
