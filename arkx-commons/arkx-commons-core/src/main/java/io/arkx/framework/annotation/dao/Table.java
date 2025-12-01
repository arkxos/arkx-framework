package io.arkx.framework.annotation.dao;

import java.lang.annotation.*;

/**
 * 用来标识DAO类对应的数据库表名
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Table {
	/**
	 * @return 表名
	 */
	String value();
}
