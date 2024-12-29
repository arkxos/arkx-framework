package com.arkxit.data.jpa.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.annotation.QueryAnnotation;

/**
 * 
 * @author Darkness
 * @date 2020年10月25日 下午1:39:38
 * @version V1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@QueryAnnotation
@Documented
public @interface TemplateQueryObject {
	String value() default "";
}
