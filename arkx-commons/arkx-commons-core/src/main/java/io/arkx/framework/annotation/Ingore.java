package io.arkx.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @class org.ark.framework.orm.annotation.Ingore
 * @author Darkness
 * @date 2012-10-29 下午09:41:55
 * @version V1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface Ingore {

}
