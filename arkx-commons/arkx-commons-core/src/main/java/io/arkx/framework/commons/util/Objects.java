package io.arkx.framework.commons.util;

import java.util.ArrayList;

public class Objects {

    @SafeVarargs
    public static <T> ArrayList<T> newArrayList(T... elements) {
        ArrayList<T> result = new ArrayList<>();
        if (elements != null) {
            for (T t : elements) {
                result.add(t);
            }
        }
        return result;
    }

    @SafeVarargs
    public static <T> T[] newArray(T... elements) {
        return elements;
    }

}
