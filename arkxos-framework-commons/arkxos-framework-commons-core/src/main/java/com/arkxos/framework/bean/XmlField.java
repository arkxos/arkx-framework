package com.arkxos.framework.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**   
 * 
 * @author Darkness
 * @date 2013-11-5 上午10:26:04 
 * @version V1.0   
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.FIELD })
public @interface XmlField {

	String refer() default "";
	
	boolean parentIdField() default false;

	boolean spelling() default false;

	String prefix() default "";
}
