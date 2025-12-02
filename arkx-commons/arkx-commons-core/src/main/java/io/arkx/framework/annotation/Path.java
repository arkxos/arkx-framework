package io.arkx.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @class org.ark.framework.jaf.annotation.Path 路径注解
 *
 * @author Darkness
 * @date 2012-8-7 下午9:35:40
 * @version V1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD})
public @interface Path {
    public abstract String value();
}
