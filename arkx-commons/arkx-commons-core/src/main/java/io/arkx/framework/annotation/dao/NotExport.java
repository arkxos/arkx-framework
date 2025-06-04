package io.arkx.framework.annotation.dao;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识在导出数据库时是否导出相应的DAO类对应的数据表的数据
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface NotExport {
	/**
	 * @return 是否导出
	 */
	boolean value() default true;

}
