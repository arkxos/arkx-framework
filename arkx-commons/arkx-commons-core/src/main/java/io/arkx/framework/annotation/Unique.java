package io.arkx.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**   
 * @class org.ark.framework.orm.annotation.Unique
 * 字段唯一注解
 * @author Darkness
 * @date 2012-10-29 下午09:41:55 
 * @version V1.0   
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.FIELD })
public @interface Unique {

	/**
	 * 字段描述
	 * 
	 * @author Darkness
	 * @date 2013-3-13 上午11:38:39 
	 * @version V1.0
	 */
	public String value();
}

