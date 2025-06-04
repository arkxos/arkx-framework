package com.arkxos.framework.data.mybatis.interceptor;

import com.arkxos.framework.data.mybatis.model.EntityMap;

public interface EnumConvertInterceptor {
    boolean convert(EntityMap map, String key, Object v);
}
