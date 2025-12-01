package io.arkx.framework.boot.spring;

import io.arkx.framework.commons.exception.ServiceException;
import io.arkx.framework.core.JsonResult;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ServiceExceptionControllerAdvice {

	/**
	 * 全局异常捕捉处理
	 * 
	 * @param ex
	 * @return
	 */
	@ResponseBody
	@ExceptionHandler(value = Exception.class)
	public JsonResult errorHandler(Exception ex) {
		ex.printStackTrace();
		try {
			Session session = SessionFactory.currentSession();
			if(session != null) {
				session.rollback();
				SessionFactory.clearCurrentSession();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JsonResult.createErrorResult(ex.getMessage());
	}

	/**
	 * 拦截捕捉自定义异常 MyException.class
	 * 
	 * @param ex
	 * @return
	 */
	@ResponseBody
	@ExceptionHandler(value = ServiceException.class)
	public JsonResult myErrorHandler(ServiceException ex) {
		ex.printStackTrace();
		try {
			Session session = SessionFactory.currentSession();
			if(session != null) {
				session.rollback();
				SessionFactory.clearCurrentSession();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JsonResult.createErrorResult(ex.getMessage());
	}
}
