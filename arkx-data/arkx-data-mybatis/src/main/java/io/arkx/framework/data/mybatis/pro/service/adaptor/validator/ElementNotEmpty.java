package io.arkx.framework.data.mybatis.pro.service.adaptor.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.arkx.framework.data.mybatis.pro.service.adaptor.validator.impl.ElementNotEmptyValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * @author w.dehai
 *
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Repeatable(ElementNotEmpty.List.class)
@Constraint(validatedBy = {ElementNotEmptyValidator.class})
public @interface ElementNotEmpty {

    String message() default "子元素不能为空";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        ElementNotEmpty[] value();

    }

}
