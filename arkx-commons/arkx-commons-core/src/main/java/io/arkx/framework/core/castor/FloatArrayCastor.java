package io.arkx.framework.core.castor;

import java.util.Collection;

/**
 * 浮点数组类型转换器
 *
 */
public class FloatArrayCastor extends AbstractCastor {
    private static FloatArrayCastor singleton = new FloatArrayCastor();

    public static FloatArrayCastor getInstance() {
        return singleton;
    }

    private FloatArrayCastor() {
    }

    @Override
    public boolean canCast(Class<?> type) {
        return Float[].class == type || float[].class == type;
    }

    @Override
    public Object cast(Object obj, Class<?> type) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof float[]) {
            return obj;
        } else if (obj.getClass().isArray()) {
            Object[] os = (Object[]) obj;
            float[] arr = new float[os.length];
            for (int i = 0; i < os.length; i++) {
                arr[i] = (Float) FloatCastor.getInstance().cast(os[i], type);
            }
            return arr;
        } else if (obj instanceof String && DoubleArrayCastor.isNumberArray(obj.toString())) {
            double[] ds = DoubleArrayCastor.toDoubleArray(obj.toString());
            float[] arr = new float[ds.length];
            for (int i = 0; i < ds.length; i++) {
                arr[i] = Double.valueOf(ds[i]).floatValue();
            }
            return arr;
        }
        if ((obj instanceof Collection)) {
            Collection<?> c = (Collection) obj;
            float[] arr = new float[c.size()];
            int i = 0;
            for (Object o : c) {
                arr[(i++)] = ((Float) FloatCastor.getInstance().cast(o, type)).floatValue();
            }
            return arr;
        }
        return new float[]{(Float) FloatCastor.getInstance().cast(obj, type)};
    }
}
