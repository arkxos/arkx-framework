package io.arkx.framework.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义资源服务，接口扫描只扫描资源服务器的api列表
 * @author darkness
 * @version 1.0
 * @date 2022/5/15 0:05
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ArkResourceServer {
}
