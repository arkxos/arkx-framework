package io.arkx.framework.data.fasttable.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Darkness
 * @date 2016年11月9日 上午11:09:54
 * @version V1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FastColumn {

    int length() default -1;

    String name() default "";

}
