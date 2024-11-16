package com.rapidark.framework.common.mybatis;

public interface EnumConvertInterceptor {
    boolean convert(EntityMap map, String key, Object v);
}
