package org.ark.framework.jaf.controls;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.ark.framework.jaf.Current;
import org.ark.framework.security.PrivCheck;
import org.ark.framework.security.VerifyCheck;

import io.arkx.framework.annotation.Priv;
import io.arkx.framework.annotation.Verify;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.cosyui.web.UIFacade;
import io.arkx.framework.i18n.LangMapping;


/**
 * @class org.ark.framework.jaf.controls.UploadUI
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:45:54 
 * @version V1.0
 */
public class UploadUI extends UIFacade {
	private static Object mutex = new Object();

	private static Mapx<String, TaskStatus> uploadTaskMap = new Mapx<String, TaskStatus>(5000);

	@Priv(login = false)
	@Verify(ignoreAll = true)
	public void submit() {
		String method = $V("_ARK_METHOD");
		this.Request.remove("_ARK_METHOD");

		Method m = Current.findMethod(method, new Class[] { UploadAction.class });
		if (!PrivCheck.check(m, this.Request, this.Response)) {
			return;
		}

		if (!VerifyCheck.check(m)) {
			String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
			LogUtil.warn(message);
			Current.getResponse().setFailedMessage(message);
			return;
		}
		Current.invokeMethod(m, new Object[] { new UploadAction() });
	}

	@Priv(login = false)
	public void getTaskStatus() {
		String taskID = $V("TaskID");
		$S("Status", getTaskStatus(taskID));
	}

	public static String getTaskStatus(String taskID) {
		String Status = "";
		synchronized (mutex) {
			if (uploadTaskMap.get(taskID) != null)
				Status = ((TaskStatus) uploadTaskMap.get(taskID)).StatusStr;
			else {
				Status = LangMapping.get("Framework.Upload.Status");
			}
		}
		return Status;
	}

	public static void removeTask(String taskID) {
		synchronized (mutex) {
			uploadTaskMap.remove(taskID);
		}
	}

	public static void setTask(String taskID, String Status) {
		synchronized (mutex) {
			checkTimeout();
			if (uploadTaskMap.get(taskID) != null) {
				TaskStatus ts = (TaskStatus) uploadTaskMap.get(taskID);
				ts.LastTime = System.currentTimeMillis();
				ts.StatusStr = Status;
			} else {
				TaskStatus ts = new TaskStatus();
				ts.StatusStr = Status;
				ts.LastTime = System.currentTimeMillis();
				uploadTaskMap.put(taskID, ts);
			}
		}
	}

	private static void checkTimeout() {
		ArrayList<String> arr = uploadTaskMap.keyArray();
		long yesterday = System.currentTimeMillis() - 86400000L;
		for (String id : arr) {
			TaskStatus ts = (TaskStatus) uploadTaskMap.get(id);
			if (ts == null) {
				continue;
			}
			if (ts.LastTime < yesterday)
				uploadTaskMap.remove(id);
		}
	}

	private static class TaskStatus {
		public long LastTime;
		public String StatusStr;
	}
}