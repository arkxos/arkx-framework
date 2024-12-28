package com.rapidark.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验证注解
 * 用于标明请求参数的校验规则。<br>
 * 默认检查跨站脚本和SQL注入攻击，如果某些项（例如文章内容）不需要检查，则使用ignore声明。<br>
 * 如果某些项需要使用特定的校验规则(例如Email)，则需要使用Rules加以声明。
 * 
 * @author Darkness
 * @date 2012-8-7 下午9:36:23
 * @version V1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Verify {
	/**
	 * 所有键值都不校验
	 */
	boolean ignoreAll() default false;

	/**
	 * 指定哪些键值不需要校验
	 */
	String ignoredKeys() default "";

	/**
	 * 指定校验规则
	 */
	String[] value() default {};
}
