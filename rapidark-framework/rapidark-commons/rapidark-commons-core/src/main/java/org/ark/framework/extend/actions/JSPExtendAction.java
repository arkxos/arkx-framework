package org.ark.framework.extend.actions;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.ark.framework.jaf.Current;
import org.ark.framework.jaf.zhtml.ZhtmlIncludeResponseWrapper;
import org.ark.framework.jaf.zhtml.ZhtmlManager;

import com.rapidark.framework.commons.util.ObjectUtil;
import com.rapidark.framework.cosyui.web.RequestData;
import com.rapidark.framework.extend.ExtendException;
import com.rapidark.framework.extend.IExtendAction;


/**
 * @class org.ark.framework.extend.actions.JSPExtendAction
 * jsp扩展行为
 * 
 * @author Darkness
 * @date 2012-8-7 下午9:34:19 
 * @version V1.0
 */
public abstract class JSPExtendAction implements IExtendAction {
	
	public Object execute(Object[] args) throws ExtendException {
		
		PageContext pageContext = (PageContext) args[0];
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		RequestData r = Current.initRequest(request);
		JSPContext context = new JSPContext(r);
		execute(context);
		if (!ObjectUtil.empty(context.getOut())) {
			try {
				pageContext.getOut().print(context.getOut());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (context.getIncludes().size() > 0) {
			for (String file : context.getIncludes()) {
				try {
					ZhtmlIncludeResponseWrapper responseWraper = new ZhtmlIncludeResponseWrapper(response, pageContext.getOut());
					if (!ZhtmlManager.execute(file, request, responseWraper, pageContext.getServletContext())) {
						if (!file.startsWith("/")) {
							file = "/" + file;
						}
						pageContext.include(file);
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new ExtendException(e.getMessage());
				}
			}
		}
		return null;
	}

	public abstract void execute(JSPContext context) throws ExtendException;
	
	@Override
	public boolean isUsable() {
		// TODO Auto-generated method stub
		return false;
	}
}