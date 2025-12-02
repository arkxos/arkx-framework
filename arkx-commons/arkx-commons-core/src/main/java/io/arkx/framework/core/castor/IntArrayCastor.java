package io.arkx.framework.core.castor;

import java.util.Collection;

/**
 * 整型数组转换器
 *
 */
public class IntArrayCastor extends AbstractCastor {
    private static IntArrayCastor singleton = new IntArrayCastor();

    public static IntArrayCastor getInstance() {
        return singleton;
    }

    private IntArrayCastor() {
    }

    @Override
    public boolean canCast(Class<?> type) {
        return Integer[].class == type || int[].class == type;
    }

    @Override
    public Object cast(Object obj, Class<?> type) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof int[]) {
            return obj;
        }

        if (obj.getClass().isArray()) {
            Object[] os = (Object[]) obj;
            int[] arr = new int[os.length];
            for (int i = 0; i < os.length; i++) {
                arr[i] = (Integer) IntCastor.getInstance().cast(os[i], type);
            }
            return arr;
        } else if (obj instanceof String && DoubleArrayCastor.isNumberArray(obj.toString())) {
            double[] ds = DoubleArrayCastor.toDoubleArray(obj.toString());
            int[] arr = new int[ds.length];
            for (int i = 0; i < ds.length; i++) {
                arr[i] = Double.valueOf(ds[i]).intValue();
            }
            return arr;
        }
        if ((obj instanceof Collection)) {
            Collection<?> c = (Collection) obj;
            int[] arr = new int[c.size()];
            int i = 0;
            for (Object o : c) {
                arr[(i++)] = ((Integer) IntCastor.getInstance().cast(o, type)).intValue();
            }
            return arr;
        }
        return new int[]{(Integer) IntCastor.getInstance().cast(obj, type)};
    }
}
