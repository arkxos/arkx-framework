package io.arkx.framework.cosyui.zhtml;

import java.io.IOException;

import com.arkxos.framework.Account;
import com.arkxos.framework.Config;
import com.arkxos.framework.Current;
import com.arkxos.framework.Member;
import io.arkx.framework.cosyui.expression.IVariableResolver;
import io.arkx.framework.cosyui.template.AbstractExecuteContext;
import io.arkx.framework.cosyui.template.ITemplateManagerContext;
import io.arkx.framework.cosyui.template.TemplateWriter;
import com.arkxos.framework.i18n.LangMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Zhtml执行上下文
 * 
 */
public class ZhtmlExecuteContext extends AbstractExecuteContext implements IVariableResolver {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ITemplateManagerContext managerContext;

	public ZhtmlExecuteContext(ITemplateManagerContext managerContext, HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.managerContext = managerContext;
		try {
			if (response != null) {
				out = new TemplateWriter(response.getWriter());
			} else {
				out = new TemplateWriter();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	@Override
	public ITemplateManagerContext getManagerContext() {
		return managerContext;
	}

	public boolean execute(String url) {
		boolean flag = managerContext.getTemplateManager().execute(url, this);
		out.flush();
		out.close();
		return flag;
	}

	@Override
	public ZhtmlExecuteContext getIncludeContext() {
		ZhtmlExecuteContext context = new ZhtmlExecuteContext(managerContext, request, response);
		context.currentTag = null;
		context.variables = variables.clone();
		context.out = getOut();
		return context;
	}

	@Override
	public Object resolveGlobalVariable(String var) {
		if (var == null) {
			return null;
		}
		String lowerVar = var.toLowerCase();
		if (lowerVar.equals("lang")) {
			return LangMapping.getInstance().getAllValue(this.getLanguage());
		} else if (lowerVar.equals("user")) {
			return Account.getCurrent();
		} else if (lowerVar.equals("member")) {
			return Member.getCurrent();
		} else if (lowerVar.equals("config")) {
			return Config.getMapx();
		} else if (lowerVar.equals("current")) {
			return Current.getCurrentData().values;
		} else if (lowerVar.equals("request")) {
			return Current.getRequest();
		} else if (lowerVar.equals("response")) {
			return Current.getResponse();
		} else if (lowerVar.equals("context")) {
			return this;
		}
		Object v = getRootVariable(var);
		if (v != null) {
			return v;
		}
		if (v == null) {
			v = Current.getResponse().get(var);
		}
		if (v == null) {
			v = Current.get(var);
		}
		if (v == null) {
			v = Current.getRequest().get(var);// Request中的值优先级应该最低，以防止某些情况下用户通过URL传入值覆盖程序本来应该输出的值
		}
		return v;
	}
}
