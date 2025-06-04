package io.arkx.framework.cosyui.web.mvc.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.arkxos.framework.Config;
import com.arkxos.framework.Current;
import io.arkx.framework.commons.collection.ConcurrentMapx;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.ServletUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.commons.util.UuidUtil;
import io.arkx.framework.config.UploadMaxSize;
import io.arkx.framework.core.method.IMethodLocator;
import io.arkx.framework.core.method.MethodLocatorUtil;
import io.arkx.framework.cosyui.control.UploadAction;
import io.arkx.framework.cosyui.control.UploadUI;
import io.arkx.framework.cosyui.web.mvc.IURLHandler;
import com.arkxos.framework.data.db.DataCollection;
import com.arkxos.framework.data.jdbc.Session;
import com.arkxos.framework.data.jdbc.SessionFactory;
import com.arkxos.framework.i18n.LangMapping;
import com.arkxos.framework.security.PrivCheck;
import com.arkxos.framework.security.VerifyCheck;
import com.arkxos.framework.security.exception.PrivException;
import io.arkx.framework.thirdparty.commons.fileupload.FileItem;
import io.arkx.framework.thirdparty.commons.fileupload.FileItemFactory;
import io.arkx.framework.thirdparty.commons.fileupload.disk.DiskFileItemFactory;
import io.arkx.framework.thirdparty.commons.fileupload.servlet.ServletFileUpload;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 上传控件数据提交处理者
 * 
 */
public class UploadHandler implements IURLHandler {
	public static final String ID = "com.arkxos.framework.core.UploadHandler";

	// 多文件上传时需要先放到Map中，最后一个文件上传后才调用method
	private static ConcurrentMapx<String, TaskFiles> uploadFileMap = new ConcurrentMapx<String, TaskFiles>(5000);

	@Override
	public boolean match(String url) {
		return url.startsWith("/ZUploader.zhtml");
	}

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "Upload Invoke Processor";
	}

	@Override
	public boolean handle(String url, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (!ServletFileUpload.isMultipartContent(request)) {
			LogUtil.warn("RequestContent is not MultipartContent,please check FormAttribute:enctype=multipart/...");
			return true;
		}
		response.setContentType("text/html; charset=" + Config.getGlobalCharset());
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out = response.getWriter();
		FileItemFactory fileFactory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(fileFactory);
		upload.setHeaderEncoding(Config.getGlobalCharset());
		upload.setSizeMax(UploadMaxSize.getValue());
		
		Session session = null;
		try {
			session = SessionFactory.openSessionInThread();
			session.beginTransaction();
			
			Mapx<String, String> params = ServletUtil.getParameterMap(request);
			
			List<?> items = upload.parseRequest(request);
			HashMap<String, String> fields = new HashMap<>();
			ArrayList<FileItem> files = new ArrayList<>();
			Iterator<?> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					fields.put(item.getFieldName(), item.getString(Config.getGlobalCharset()));
				} else {
					String OldFileName = item.getName();
					long size = item.getSize();
					if ((OldFileName == null || OldFileName.equals("")) && size == 0) {
						continue;
					} else {
						LogUtil.info("-----UploadFileName:-----" + OldFileName);
						files.add(item);
					}
				}
			}
			String taskID = fields.get("_ZUploder_TaskID");
			if(StringUtil.isEmpty(taskID)) {
				taskID = UuidUtil.base58Uuid();
			}
			String totalStr = fields.get("_ZUploader_Total");

			// 必须先置入状态
			UploadUI.setTask(taskID, LangMapping.get("Framework.Upload.Status"));

//			// 处理Firefox下的Session问题
//			String ids = fields.get("_SessionID");
//			if (StringUtil.isNotEmpty(ids)) {
//				HttpSession session = request.getSession();
//				String[] arr = ids.split("\\,");
//				HttpSession sessionOld = null;
//				for (String sessionID : arr) {
//					if (session.getId().equals(sessionID)) {
//						break;
//					}
//					sessionOld = HttpSessionListenerFacade.getSession(sessionID);
//					if (sessionOld != null) {
//						break;
//					}
//				}
//				if (sessionOld != null) {
//					// 从有效session中复制数据到新的session
//					Enumeration<?> en = sessionOld.getAttributeNames();
//					while (en.hasMoreElements()) {
//						String n = (String) en.nextElement();
//						session.setAttribute(n, sessionOld.getAttribute(n));
//					}
//					UserData u = SessionListener.getUserDataFromSession(session);
//					if (u != null) {
//						User.setCurrent(u);
//					}
//				}
//			}

			int total = 1;//files.size();
			if (ObjectUtil.notEmpty(totalStr)) {
				total = Integer.parseInt(totalStr);
			}
			TaskFiles uploadedFiles = null;
			checkTimeout();
			uploadedFiles = uploadFileMap.get(taskID);
			if (uploadedFiles == null) {
				uploadedFiles = new TaskFiles();
				uploadFileMap.put(taskID, uploadedFiles);
			}
			uploadedFiles.LastTime = System.currentTimeMillis();
			uploadedFiles.Files.addAll(files);
			if (total <= uploadedFiles.Files.size()) {
				String method = fields.get("_Method");
				if(StringUtil.isEmpty(method)) {
					method = params.getString("_Method");
				}
				IMethodLocator m = MethodLocatorUtil.find(method);
				Current.getRequest().putAll(fields);
				try {
					PrivCheck.check(m);
				} catch (PrivException e) {				
					e.printStackTrace();
					
					uploadFileMap.remove(taskID);
					//在这儿约定550为权限校验失败返回的状态码
					UploadUI.setTask(taskID, "Error 550");
					for (FileItem file : uploadedFiles.Files) {
						file.delete();// 删除清理掉临时文件
					}
					throw e;
				}

				// 参数检查
				if (!VerifyCheck.check(m)) {
					String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
					LogUtil.warn(message);

					uploadFileMap.remove(taskID);
					for (FileItem file : uploadedFiles.Files) {
						file.delete();// 删除清理掉临时文件
					}
					return true;// 参数检查未通过，则不继续执行
				}

				UploadAction ua = new UploadAction();
				ua.setItems(uploadedFiles.Files);
				ua.setCookies(Current.getCookies());
		        ua.setRequest(request);
		        ua.setResponse(response);
		        
				m.execute(ua);

				for (FileItem file : uploadedFiles.Files) {
					file.delete();// 删除清理掉临时文件
				}
				uploadFileMap.remove(taskID);
				UploadUI.setTask(taskID, "Finished");// 必须是“Finished”
				
				String responseFormat = fields.get("responseFormat");
				if(StringUtil.isEmpty(responseFormat)) {
					responseFormat = params.getString("responseFormat");
				}
				if("json".equals(responseFormat)) {
					response.getWriter().write(Current.getResponse().toJSON());// 将结果返回给页面
				} else {
					response.getWriter().write(Current.getResponse().toXML());// 将结果返回给页面
				}
				
			} else {
				String responseFormat = fields.get("responseFormat");
				if(StringUtil.isEmpty(responseFormat)) {
					responseFormat = params.getString("_Method");
				}
				if("json".equals(responseFormat)) {
					response.getWriter().write(new DataCollection().toJSON());// 输出空的数据集
				} else {
					response.getWriter().write(new DataCollection().toXML());// 输出空的数据集
				}
			}
			
			session.commit();
		} catch (Exception ex) {
			session.rollback();
			ex.printStackTrace();
		} finally {
			SessionFactory.clearCurrentSession();
		}
		
		out.flush();
		out.close();
		
		return true;
	}

	private static void checkTimeout() {// 不需要再加锁，外面已经加锁
		long yesterday = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
		for (Entry<String, TaskFiles> entry : uploadFileMap.entrySet()) {
			String id = entry.getKey();
			TaskFiles tf = entry.getValue();
			if (tf == null) {
				continue;
			}
			if (tf.LastTime < yesterday) {
				uploadFileMap.remove(id);
			}
		}
	}

	private static class TaskFiles {
		public long LastTime;// 最后活动时间
		public ArrayList<FileItem> Files = new ArrayList<FileItem>();
	}

	@Override
	public void init() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public int getOrder() {
		return 9998;
	}

}
