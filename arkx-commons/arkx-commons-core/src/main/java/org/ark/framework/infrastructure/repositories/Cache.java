package org.ark.framework.infrastructure.repositories;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @class org.ark.framework.infrastructure.repositories.Cache 缓存注解
 *
 * @author Darkness
 * @date 2012-8-7 下午9:35:59
 * @version V1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface Cache {

    public abstract boolean value() default true;

}
