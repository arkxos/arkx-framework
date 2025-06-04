package com.arkxos.framework.annotation.dao;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识DAO对应数据库表的索引
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Indexes {
	
	/**
	 * @return 索引信息，格式为idx1=id,name;idx2=innercode;idx3=orderflag
	 */
	String value() default "";
	
}
