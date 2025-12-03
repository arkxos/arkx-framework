package io.arkx.framework.data.mybatis.interceptor;

import io.arkx.framework.data.mybatis.model.EntityMap;

public interface EnumConvertInterceptor {

    boolean convert(EntityMap map, String key, Object v);

}
