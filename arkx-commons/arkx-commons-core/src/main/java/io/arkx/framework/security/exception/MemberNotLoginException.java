package io.arkx.framework.security.exception;

/**
 * 会员未登录异常
 *
 */
public class MemberNotLoginException extends PrivException {

	private static final long serialVersionUID = 1L;

	public MemberNotLoginException(String message) {
		super(message);
	}

}
