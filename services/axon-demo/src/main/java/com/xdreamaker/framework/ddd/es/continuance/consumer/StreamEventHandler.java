package com.xdreamaker.framework.ddd.es.continuance.consumer;

import java.lang.annotation.*;

/**
 * @author darkness
 * @date 2021/6/27 0:04
 * @version 1.0
 */
@Target( {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StreamEventHandler {

    String[] payloadTypes() default {""};

    String[] types();

}
