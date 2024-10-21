package com.rapidark.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @class org.ark.framework.orm.annotation.Entity
 * @author Darkness
 * @date 2012-9-15 上午9:50:12
 * @version V1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.TYPE })
public @interface Entity {

	String name();
}
