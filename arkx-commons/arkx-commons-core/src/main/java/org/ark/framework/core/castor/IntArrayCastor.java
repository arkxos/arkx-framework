package org.ark.framework.core.castor;

/**
 * int[]转换器
 *
 * @author Darkness
 * @date 2013-3-26 下午07:47:01
 * @version V1.0
 */
public class IntArrayCastor extends AbstractInnerCastor {

    private static IntArrayCastor singleton = new IntArrayCastor();

    public static IntArrayCastor getInstance() {
        return singleton;
    }

    @Override
    public boolean canCast(Class<?> type) {
        return (Integer[].class == type) || (int.class == type);
    }

    @Override
    public Object cast(Object obj, Class<?> type) {
        if (obj == null) {
            return null;
        }

        if ((obj instanceof int[])) {
            return (int[]) obj;
        }

        if (obj.getClass().isArray()) {
            Object[] os = (Object[]) obj;
            int[] arr = new int[os.length];
            for (int i = 0; i < os.length; i++) {
                arr[i] = ((Integer) IntCastor.getInstance().cast(os[i], type)).intValue();
            }
            return arr;
        }

        if (((obj instanceof String)) && (DoubleArrayCastor.isNumberArray(obj.toString()))) {
            double[] ds = DoubleArrayCastor.toDoubleArray(obj.toString());
            int[] arr = new int[ds.length];
            for (int i = 0; i < ds.length; i++) {
                arr[i] = Double.valueOf(ds[i]).intValue();
            }
            return arr;
        }

        return new int[]{((Integer) IntCastor.getInstance().cast(obj, type)).intValue()};
    }

}
