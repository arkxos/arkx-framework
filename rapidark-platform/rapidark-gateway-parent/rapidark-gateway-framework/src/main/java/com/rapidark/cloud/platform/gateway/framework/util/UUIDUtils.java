package com.rapidark.cloud.platform.gateway.framework.util;

import java.util.UUID;

/**
 * @Description
 * @Author JL
 * @Date 2021/09/27
 * @Version V1.0
 */
public class UUIDUtils {

    /**
     * 获取不带-的UUID字符串
     * @return
     */
    public static String getUUIDString(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

}
