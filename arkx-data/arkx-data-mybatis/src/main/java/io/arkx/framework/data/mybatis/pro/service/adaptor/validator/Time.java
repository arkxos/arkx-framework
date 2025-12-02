package io.arkx.framework.data.mybatis.pro.service.adaptor.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.arkx.framework.data.mybatis.pro.service.adaptor.validator.Time.List;
import io.arkx.framework.data.mybatis.pro.service.adaptor.validator.impl.TimeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * @author w.dehai
 *
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Repeatable(List.class)
@Constraint(validatedBy = { TimeValidator.class })
public @interface Time {

	String message() default "时间不正确, 范围1972-01-01 00:00:00 ~ 2037-12-31 00:00:00";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
	@Retention(RUNTIME)
	@Documented
	@interface List {

		Time[] value();

	}

}
