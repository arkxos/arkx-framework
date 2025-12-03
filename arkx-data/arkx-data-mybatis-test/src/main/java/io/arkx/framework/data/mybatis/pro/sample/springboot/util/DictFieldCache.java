package io.arkx.framework.data.mybatis.pro.sample.springboot.util;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.arkx.framework.data.mybatis.pro.sample.springboot.util.anno.Dict;

import cn.hutool.core.util.ReflectUtil;

/**
 * 字典属性缓存：被@Dict标记的属性缓存
 *
 * @author w.dehai
 *
 */
public class DictFieldCache {

    private DictFieldCache() {
    }

    /** 缓存带有@Dict的对象属性 **/
    private static final ConcurrentMap<Class<?>, List<Field>> DICT_MAP = new ConcurrentHashMap<>();

    public static List<Field> findForClass(Class<?> cls) {
        return DICT_MAP.computeIfAbsent(cls, c -> {
            Field[] fields = ReflectUtil.getFields(c);
            return stream(ofNullable(fields).orElseGet(() -> new Field[0]))
                    .filter(field -> field.isAnnotationPresent(Dict.class)).peek(field -> field.setAccessible(true))
                    .collect(toList());
        });
    }

}
