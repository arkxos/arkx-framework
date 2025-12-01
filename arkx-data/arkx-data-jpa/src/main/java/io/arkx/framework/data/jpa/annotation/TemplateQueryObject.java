package io.arkx.framework.data.jpa.annotation;

import org.springframework.data.annotation.QueryAnnotation;

import java.lang.annotation.*;

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
