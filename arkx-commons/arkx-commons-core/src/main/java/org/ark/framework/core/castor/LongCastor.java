package org.ark.framework.core.castor;

import io.arkx.framework.commons.util.ObjectUtil;

/**
 * long类型转化器
 *
 * @author Darkness
 * @date 2013-3-26 下午08:08:46
 * @version V1.0
 */
public class LongCastor extends AbstractInnerCastor {

    private static LongCastor singleton = new LongCastor();

    public static LongCastor getInstance() {
        return singleton;
    }

    @Override
    public boolean canCast(Class<?> type) {
        return (Long.class == type) || (Long.TYPE == type);
    }

    @Override
    public Object cast(Object obj, Class<?> type) {
        if (obj == null) {
            return 0;
        }
        if ((obj instanceof Number))
            return ((Number) obj).longValue();
        try {
            String str = obj.toString();
            if (ObjectUtil.empty(str)) {
                return 0;
            }
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
