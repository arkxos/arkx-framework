package io.arkx.framework.annotation;

import java.lang.annotation.*;

/**
 * 权限注解 用于声明执行方法所拥有的权限。<br>
 *
 * @author Darkness
 * @date 2012-8-7 下午9:35:59
 * @version V1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Priv {

	/*
	 * Manager表示后台用户，Member表示前台用户
	 */
	public enum LoginType {

		User, Member;

	}

	/**
	 * 当前操作是否要求己登录，默认要求己登录。
	 */
	boolean login() default true;

	/**
	 * 忽略权限校验
	 * @return
	 */
	boolean ingore() default false;

	/**
	 * 当前操作要求的用户类型，默认是后台用户
	 */
	LoginType loginType() default LoginType.User;

	/**
	 * 当前操作要求用户属性中具有某些值，便如RealName=Test
	 */
	String userType() default "";

	/**
	 * 权限类型，由业务系统通过扩展机制来处理
	 */
	String value() default "";

}
