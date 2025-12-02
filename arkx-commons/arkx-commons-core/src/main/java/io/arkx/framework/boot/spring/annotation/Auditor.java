package io.arkx.framework.boot.spring.annotation;

import java.lang.annotation.*;

/**
 * @author Darkness
 * @date 2019-08-18 12:22:05
 * @version V1.0
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditor {

}
