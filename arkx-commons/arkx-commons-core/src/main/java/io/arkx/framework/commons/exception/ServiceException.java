package io.arkx.framework.commons.exception;

/**
 * @class org.ark.framework.jaf.ServiceException 自定义异常状态 码 new
 * ServiceException("reduplicateUserNameException", "用户名不能重复"); var serviceExceptionCode =
 * response.get("serviceExceptionCode"); Assert.equals("reduplicateUserNameException",
 * serviceExceptionCode);
 * @author Darkness
 * @date 2012-9-15 下午3:49:58
 * @version V1.0
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String code = "serviceException";

	public static enum ExceptionType {

		Error, Warning

	}

	private ExceptionType exceptionType = ExceptionType.Error;

	public ServiceException(String msg) {
		super(msg);
	}

	public ServiceException(String code, String msg) {
		super(msg);
		this.code = code;
	}

	public ServiceException(Throwable throwable) {
		super(throwable);
	}

	public ServiceException(String msg, ExceptionType exceptionType) {
		super(msg);
		this.exceptionType = exceptionType;
	}

	public ExceptionType getExceptionType() {
		return this.exceptionType;
	}

	public String getCode() {
		return this.code;
	}

}
