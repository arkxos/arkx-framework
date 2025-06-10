package io.arkx.framework.performance.monitor2.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 备注
 * @author Darkness
 * @date 2013-12-5 下午04:24:57
 * @version V1.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IngorePerformanceLog {
	String value() default "";
}
