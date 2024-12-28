package com.rapidark.framework.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 自定义注解表示,加在类上表示是一个JpaDto类
 * @author darkness
 * @version 1.0
 * @date 2021/6/1 17:22
 */
@Documented
@Component
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JpaDto {

}
