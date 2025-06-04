package io.arkx.framework.cosyui.web.mvc;

import io.arkx.framework.Current;
import io.arkx.framework.commons.exception.ServiceException;
import io.arkx.framework.core.IExceptionCatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Darkness
 * @date 2017年4月22日 下午2:29:26
 * @version 1.0
 * @since 1.0 
 */
public class ServiceExceptionCatcher implements IExceptionCatcher {

	@Override
	public String getExtendItemID() {
		return "ServiceExceptionCatcher";
	}

	@Override
	public String getExtendItemName() {
		return "ServiceExceptionCatcher";
	}

	@Override
	public Class<?>[] getTargetExceptionClass() {
		return new Class<?>[] { ServiceException.class };
	}

	@Override
	public void doCatch(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
		if (e instanceof ServiceException) {
			ServiceException serviceException = (ServiceException)e;
			Current.getResponse().setFailedMessage(serviceException.getMessage());
			Current.getResponse().put("serviceExceptionCode", serviceException.getCode());
		}
	}

}
