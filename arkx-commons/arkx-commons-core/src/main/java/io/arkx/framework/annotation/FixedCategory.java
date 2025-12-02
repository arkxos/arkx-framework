package io.arkx.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Darkness
 * @date 2019-11-09 10:56:42
 * @version V1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface FixedCategory {

    String name() default "";

    String code();
}
