package com.rapidark.framework.cosyui.web.mvc.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rapidark.framework.Config;
import com.rapidark.framework.Current;
import com.rapidark.framework.cosyui.template.exception.TemplateNotFoundException;
import com.rapidark.framework.cosyui.web.mvc.Dispatcher;
import com.rapidark.framework.cosyui.zhtml.ZhtmlExecuteContext;
import com.rapidark.framework.cosyui.zhtml.ZhtmlManagerContext;
import com.rapidark.framework.data.jdbc.Session;
import com.rapidark.framework.data.jdbc.SessionFactory;
import com.rapidark.framework.extend.ExtendManager;
import com.rapidark.framework.extend.action.AfterZhtmlExecuteAction;

/**
 * Zhtml页面处理者
 * 
 */
public class ZhtmlHandler extends AbstractHtmlHandler {
	public static final String ID = "com.rapidark.framework.core.ZhtmlHandler";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public boolean match(String url) {
		int i = url.indexOf("?");
		if (i > 0) {
			url = url.substring(0, i);
		}
		return url.endsWith(".zhtml");
	}

	@Override
	public String getExtendItemName() {
		return "Zhtml URL Processor";
	}

	@Override
	public boolean execute(String url, HttpServletRequest request, HttpServletResponse response) {
		int i = url.indexOf("?");
		if (i > 0) {
			url = url.substring(0, i);
		}
		if (!Config.isInstalled() && url.indexOf("install.zhtml") < 0 && url.indexOf("ajax/invoke") < 0) {
			Dispatcher.forward("/install.zhtml");
			return true;
		}
		if (url.indexOf("/ajax/invoke") > 0 && !url.equals("/ajax/invoke")) {// 页面初始化时会有这种情况
			Dispatcher.forward("/ajax/invoke");
			return true;
		}
		ZhtmlExecuteContext context = new ZhtmlExecuteContext(ZhtmlManagerContext.getInstance(), request, response);
		Current.setExecuteContext(context);
		
		Session session = null;
		try {
			session = SessionFactory.openSessionInThread();
			session.beginTransaction();
			
			if (!context.execute(url)) {
				return false;
			}
			
			session.commit();
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
			
			session.rollback();
			session.close();
			
			return false;
		} catch (Exception e) {
			session.rollback();
			session.close();
			throw e;
		} finally {
			SessionFactory.clearCurrentSession();
//			BlockingTransaction.clearTransactionBinding();// 检测是否有未被关闭的阻塞型事务连接
		}
		ExtendManager.invoke(AfterZhtmlExecuteAction.ExtendPointID, new Object[] { request, response });
		return true;
	}

	@Override
	public int getOrder() {
		return 9999;
	}
}
