package org.ark.framework.jaf.tag;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.Html2Util;
import io.arkx.framework.commons.util.ObjectUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.ark.framework.jaf.Current;
import org.ark.framework.jaf.PlaceHolderContext;
import org.ark.framework.security.PrivCheck;

import java.lang.reflect.Method;


/**
 * @class org.ark.framework.jaf.tag.InitTag
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:52:25 
 * @version V1.0
 */
public class InitTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;
	private String method;

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
			HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
			if (ObjectUtil.notEmpty(this.method)) {
				Method m = Current.prepareMethod(request, response, this.method, null);
				if (!PrivCheck.check(m, request, response)) {
					return TagSupport.SKIP_PAGE;
				}
				Object o = Current.invokeMethod(m, null);
				if(o!=null && (o instanceof Mapx)) {
					Current.getResponse().putAll((Mapx)o);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return TagSupport.EVAL_BODY_AGAIN;
	}

	public int doAfterBody() throws JspException {

		String content = getBodyContent().getString();
		try {
			PlaceHolderContext context = PlaceHolderContext.getInstance(null, this.pageContext);
			content = Html2Util.replacePlaceHolder(content, context, true, false);
			getPreviousOut().print(content);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return TagSupport.EVAL_PAGE;
	}
}