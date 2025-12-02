package io.arkx.framework.data.common.sqltoy;

import java.lang.annotation.*;

import org.springframework.data.annotation.QueryAnnotation;

/**
 * @author Darkness
 * @date 2020年10月25日 下午3:56:44
 * @version V1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@QueryAnnotation
@Documented
public @interface SqlToyQuery {

    String value() default "";
}
