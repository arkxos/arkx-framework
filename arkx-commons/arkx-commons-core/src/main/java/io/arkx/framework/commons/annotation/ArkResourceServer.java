package io.arkx.framework.commons.annotation;

import java.lang.annotation.*;

/**
 * 定义资源服务，接口扫描只扫描资源服务器的api列表
 *
 * @author darkness
 * @version 1.0
 * @date 2022/5/15 0:05
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ArkResourceServer {

}
