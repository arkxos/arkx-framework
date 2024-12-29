package com.arkxos.framework.cosyui.tag;

import java.util.ArrayList;
import java.util.List;

import com.arkxos.framework.FrameworkPlugin;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.cosyui.template.TagAttr;
import com.arkxos.framework.cosyui.template.exception.TemplateRuntimeException;
import com.arkxos.framework.cosyui.web.mvc.handler.ActionHandler;
import com.arkxos.framework.cosyui.zhtml.ZhtmlExecuteContext;
import com.arkxos.framework.extend.plugin.PluginManager;
import com.arkxos.framework.security.exception.PrivException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.tagext.BodyTag;

/**
 * 在页面中调用ZAction方法　
 * 
 */
public class ActionTag extends ArkTag {
	private String method;

	public String getMethod() {
		return method;
	}

	@Override
	public String getTagName() {
		return "action";
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		try {
			if (pageContext instanceof ZhtmlExecuteContext) {
				ZhtmlExecuteContext httpContext = (ZhtmlExecuteContext) pageContext;
				HttpServletRequest request = httpContext.getRequest();
				if (ObjectUtil.notEmpty(method)) {
					String requestURI = request.getRequestURI();
					String context = request.getContextPath();
					String url = requestURI.substring(context.length(), requestURI.length());// 以/开头
					ActionHandler up = (ActionHandler) PluginManager.getInstance().getPluginConfig(FrameworkPlugin.ID).getExtendItems()
							.get(ActionHandler.ID).getInstance();
					up.handle(url, request, httpContext.getResponse());
				}
			}
		} catch (PrivException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BodyTag.EVAL_BODY_BUFFERED;
	}

	@Override
	public int doEndTag() throws TemplateRuntimeException {
		if (pageContext instanceof ZhtmlExecuteContext) {
			ZhtmlExecuteContext httpContext = (ZhtmlExecuteContext) pageContext;
			HttpServletRequest request = httpContext.getRequest();
			if (ObjectUtil.equal("true", request.getAttribute("ZACTION_SKIPPAGE"))) {
				return SKIP_PAGE;
			}
		}
		return EVAL_PAGE;

	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<TagAttr>();
		list.add(new TagAttr("method", true));
		return list;
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.Tag.ActionTagName}";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

}
