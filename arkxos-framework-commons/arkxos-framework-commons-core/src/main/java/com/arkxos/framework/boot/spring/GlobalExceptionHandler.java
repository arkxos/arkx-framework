//package com.rapidark.framework.boot.spring;
//
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import com.rapidark.framework.commons.util.StringUtil;
//import com.rapidark.framework.core.JsonResult;
//import com.rapidark.framework.data.jdbc.Session;
//import com.rapidark.framework.data.jdbc.SessionFactory;
//
//import lombok.extern.slf4j.Slf4j;
//
///**  
// * 
// * @author darkness  
// * @date 2018-08-26 14:54:13
// * @version 1.0  
// * @since 4.0
// */
//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//	@ExceptionHandler(Throwable.class)
//	public JsonResult handle(Throwable throwable) {
//		try {
//			log.debug("handle exception: " + throwable.getMessage());
//			throwable.printStackTrace();
//			Session session = SessionFactory.currentSession();
//			if(session != null) {
//				try {
//					session.rollback();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				session.close();
//				SessionFactory.clearCurrentSession();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		String message = throwable.getMessage();
//		if(StringUtil.isEmpty(message)) {
//			message = throwable.getClass().getName();
//		}
//		return JsonResult.createErrorResult(message);
//	}
//	
//}
package com;


