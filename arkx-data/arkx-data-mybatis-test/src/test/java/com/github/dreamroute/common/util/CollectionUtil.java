package com.github.dreamroute.common.util;

import static java.util.Optional.ofNullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * 描述：{@link java.util.Optional}辅助类
 *
 * @author w.dehi.2022-05-10
 */
public class CollectionUtil {
    private CollectionUtil() {
    }

    public static <T> boolean isEmpty(Collection<T> source) {
        return source == null || source.isEmpty();
    }

    public static <T> boolean isNotEmpty(Collection<T> source) {
        return !isEmpty(source);
    }

    public static <K, V> boolean isEmpty(Map<K, V> source) {
        return source == null || source.isEmpty();
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> source) {
        return !isEmpty(source);
    }

    public static <T> boolean isEmpty(T[] source) {
        return source == null || source.length == 0 || Arrays.stream(source).allMatch(Objects::isNull);
    }

    public static <T> boolean isNotEmpty(T[] source) {
        return !isEmpty(source);
    }

    public static <T> List<T> nonNull(List<T> source) {
        return ofNullable(source).orElseGet(ArrayList::new);
    }

    public static <T> Stream<T> nonNullStream(List<T> source) {
        return nonNull(source).stream();
    }

    public static <T> Set<T> nonNull(Set<T> source) {
        return ofNullable(source).orElseGet(HashSet::new);
    }

    public static <T> Stream<T> nonNullStream(Set<T> source) {
        return nonNull(source).stream();
    }

    @SafeVarargs
    public static <T> List<T> newArrayList(T... init) {
        return new ArrayList<>(Arrays.asList(init));
    }

    public static <T> List<T> newArrayList(Collection<? extends T> c) {
        return new ArrayList<>(c);
    }

    public static <T> List<T> newArrayListWithCapacity(int capacity) {
        return new ArrayList<>(capacity);
    }

}
