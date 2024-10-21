package com.rapidark.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 别名注解
 * 用于标明UIFacade类中的方法和内部类的别名，以便于前台调用
 * 
 * @author Darkness
 * @date 2012-8-7 下午9:35:17
 * @version V1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Alias {
	/**
	 * 用于标注一个方法或者内部是否是一个独立的别名，即不继承UIFacade的别名设定
	 */
	boolean alone() default false;

	/**
	 * 方法的别名，可以使用'.'分隔路径，也可以使用'/'分隔路径
	 */
	String value();
}
