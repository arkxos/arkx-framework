package com.arkxos.framework.cosyui.web.mvc.handler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;

import com.arkxos.framework.Config;
import com.arkxos.framework.Current;
import com.arkxos.framework.commons.util.LogUtil;
import com.arkxos.framework.config.UploadMaxSize;
import com.arkxos.framework.core.method.IMethodLocator;
import com.arkxos.framework.core.method.MethodLocatorUtil;
import com.arkxos.framework.cosyui.control.UploadAction;
import com.arkxos.framework.cosyui.web.mvc.IURLHandler;
import com.arkxos.framework.data.jdbc.Session;
import com.arkxos.framework.data.jdbc.SessionFactory;
import com.arkxos.framework.security.PrivCheck;
import com.arkxos.framework.security.VerifyCheck;
import com.arkxos.framework.thirdparty.commons.fileupload.servlet.ServletFileUpload;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ZAction处理者
 */
public class ActionHandler implements IURLHandler {
	public static final String ID = "com.arkxos.framework.core.ActionHandler";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "ZAction URL Processor";
	}

	@Override
	public boolean match(String url) {
		int i = url.indexOf('?');
		if (i > 0) {
			url = url.substring(0, i);
		}
		if (url.endsWith(".zaction")) {
			return true;
		}
		if (url.endsWith("/")) {
			return false;
		}
		i = url.lastIndexOf('/');
		if (i > 0) {
			url = url.substring(i + 1);
		}
		if (url.indexOf('.') < 0) {// 无后缀也匹配
			return true;
		}
		return false;
	}

	@Override
	public boolean handle(String url, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Session session = null;
		try {
			session = SessionFactory.openSessionInThread();
			session.beginTransaction();
			
			if (url.endsWith(".zaction")) {
				url = url.substring(0, url.length() - 8);
			}
			if (url.endsWith("/")) {
				url = url.substring(0, url.length() - 1);
			}
			try {
				IMethodLocator method = MethodLocatorUtil.find(url.substring(1));
				if (method == null) {
					return false;
				}
				boolean success = invoke(request, response, method);
				
				session.commit();
				
				return success;
			} catch (Exception e) {
				e.printStackTrace();
				
				session.rollback();
				
				return false;
			}
		} finally {
			
			SessionFactory.clearCurrentSession();
//			BlockingTransaction.clearTransactionBinding();// 检测是否有未被关闭的阻塞型事务连接
		}
	}

	public static boolean invoke(HttpServletRequest request, HttpServletResponse response, IMethodLocator method) throws ServletException,
			IOException {
		PrivCheck.check(method);
		// 参数检查
		if (!VerifyCheck.check(method)) {
			String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
			LogUtil.warn(message);
			Current.getResponse().setFailedMessage(message);
			response.getWriter().write(Current.getResponse().toXML());
			return true;
		}
		
		ZAction action;
		
		if (ServletFileUpload.isMultipartContent(request)) {
			UploadAction ua = new UploadAction();
			ua.setItems(prepareUploadAction(request));
			ua.setCookies(Current.getCookies());
			ua.setRequest(request);
			ua.setResponse(response);
			action = ua;
		} else {
			action = new ZAction();
			action.setCookies(Current.getCookies());
			action.setRequest(request);
			action.setResponse(response);
		}
		try {
			if (Config.getServletMajorVersion() == 2 && Config.getServletMinorVersion() == 3) {
				response.setContentType("text/html;charset=" + Config.getGlobalCharset());
			} else {
				response.setContentType("text/html");
				response.setCharacterEncoding(Config.getGlobalCharset());
			}
			method.execute(new Object[] { action });
			if (!action.isBinaryMode()) {// 没有重定向，则输出内容
				try {
					response.getWriter().print(action.getContent());
				} catch (Exception e) {
					e.printStackTrace();
				}// 不需要输出异常
			}
		} finally {
			if (action.isBinaryMode()) {
				response.getOutputStream().close();
			}
		}
		return true;
	}
	
	private static ArrayList prepareUploadAction(HttpServletRequest request) {
		DiskFileItemFactory fileFactory = DiskFileItemFactory.builder().get();
		JakartaServletFileUpload upload = new JakartaServletFileUpload(fileFactory);
//		upload.setHeaderEncoding(Config.getGlobalCharset());
		upload.setSizeMax(UploadMaxSize.getValue());
		try {
			List items = upload.parseRequest(request);
			HashMap fields = new HashMap();
			ArrayList files = new ArrayList();
			for (Iterator iter = items.iterator(); iter.hasNext();) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					fields.put(item.getFieldName(), item.getString(Charset.forName(Config.getGlobalCharset())));
				} else {
					String OldFileName = item.getName();
					long size = item.getSize();
					if (OldFileName != null && !OldFileName.equals("") || size != 0L) {
						LogUtil.info((new StringBuilder("-----UploadFileName:-----")).append(OldFileName).toString());
						files.add(item);
					}
				}
			}

			Current.getRequest().putAll(fields);
			return files;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void init() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public int getOrder() {
		return 9999;
	}

}
