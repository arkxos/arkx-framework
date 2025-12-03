package io.arkx.framework.data.db.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.arkx.framework.data.db.common.type.ProductTypeEnum;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Product {

    ProductTypeEnum value();

}
