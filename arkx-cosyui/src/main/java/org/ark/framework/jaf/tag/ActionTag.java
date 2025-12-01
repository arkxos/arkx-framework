package org.ark.framework.jaf.tag;

import io.arkx.framework.commons.util.ObjectUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import org.ark.framework.jaf.ActionFilter;


/**
 * @class org.ark.framework.jaf.tag.ActionTag
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:51:06 
 * @version V1.0
 */
public class ActionTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	private String method;

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int doStartTag() throws JspException {
		try {
			HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
			HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
			if (ObjectUtil.notEmpty(this.method))
				ActionFilter.invoke(request, response, this.method);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 2;
	}

	public int doEndTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		if (ObjectUtil.equal("true", request.getAttribute("ZACTION_SKIPPAGE"))) {
			return 5;
		}
		return 6;
	}
}