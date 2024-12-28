package com.rapidark.framework.data.mybatis.interceptor;

import com.rapidark.framework.data.mybatis.model.EntityMap;

public interface EnumConvertInterceptor {
    boolean convert(EntityMap map, String key, Object v);
}
