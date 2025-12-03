package org.ark.framework.core.castor;

import io.arkx.framework.commons.util.ObjectUtil;

/**
 * int 类型转换器
 *
 * @author Darkness
 * @date 2013-3-26 下午07:44:06
 * @version V1.0
 */
public class IntCastor extends AbstractInnerCastor {

    private static IntCastor singleton = new IntCastor();

    public static IntCastor getInstance() {
        return singleton;
    }

    @Override
    public boolean canCast(Class<?> type) {
        return (Integer.class == type) || (Integer.TYPE == type);
    }

    @Override
    public Object cast(Object obj, Class<?> type) {
        if (obj == null) {
            return 0;
        }

        if (obj instanceof Number)
            return ((Number) obj).intValue();

        try {
            String str = obj.toString();
            if (ObjectUtil.empty(str)) {
                return 0;
            }
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}
