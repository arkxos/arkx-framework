package io.arkx.framework.cosyui.control;

import java.util.Map.Entry;

import io.arkx.framework.Constant;
import io.arkx.framework.WebCurrent;
import io.arkx.framework.annotation.Priv;
import io.arkx.framework.annotation.Verify;
import io.arkx.framework.commons.collection.CacheMapx;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.core.method.IMethodLocator;
import io.arkx.framework.core.method.MethodLocatorUtil;
import io.arkx.framework.cosyui.web.UIFacade;
import io.arkx.framework.security.PrivCheck;
import io.arkx.framework.security.VerifyCheck;

/**
 * 上传控件服务器端响应UI类
 *
 */
public class UploadUI extends UIFacade {

    private static CacheMapx<String, TaskStatus> uploadTaskMap = new CacheMapx<>(5000);

    /**
     * 如果有文件上传，则从UploadHandler调用后台方法，如果没有，则从本方法调用后台方法。
     */
    @Priv(login = false)
    @Verify(ignoreAll = true)
    public void submit() {
        String method = $V(Constant.Method);
        Request.remove(Constant.Method);

        IMethodLocator m = MethodLocatorUtil.find(method);
        PrivCheck.check(m);
        // 参数检查
        if (!VerifyCheck.check(m)) {
            String message = "Verify check failed:method=" + method + ",data=" + WebCurrent.getRequest();
            LogUtil.warn(message);
            WebCurrent.getResponse().setFailedMessage(message);
            return;
        }
        m.execute(new UploadAction());
    }

    @Priv(login = false)
    @Verify(ignoreAll = true)
    public void getSessionID() {
        String sessionID = WebCurrent.getRequest().getSessionID();
        $S("sessionID", sessionID);
    }

    @Priv(login = false)
    public void getTaskStatus(String TaskID) {
        String taskID = TaskID;
        $S("StatusMsg", _getTaskStatus(taskID));
    }

    public static String _getTaskStatus(String taskID) {
        String Status = "";
        if (uploadTaskMap.get(taskID) != null) {
            Status = uploadTaskMap.get(taskID).StatusStr;
        } else {
            // Status = LangMapping.get("Framework.Upload.Status");
            Status = "Error";
        }
        return Status;
    }

    public static void removeTask(String taskID) {
        uploadTaskMap.remove(taskID);
    }

    public static void setTask(String taskID, String Status) {
        checkTimeout();
        if (uploadTaskMap.get(taskID) != null) {
            TaskStatus ts = uploadTaskMap.get(taskID);
            ts.LastTime = System.currentTimeMillis();
            ts.StatusStr = Status;
        } else {
            TaskStatus ts = new TaskStatus();
            ts.StatusStr = Status;
            ts.LastTime = System.currentTimeMillis();
            uploadTaskMap.put(taskID, ts);
        }
    }

    private static void checkTimeout() {// 不需要再加锁，外面已经加锁
        long yesterday = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
        for (Entry<String, TaskStatus> entry : uploadTaskMap.entrySet()) {
            String id = entry.getKey();
            TaskStatus ts = entry.getValue();
            if (ts == null) {
                continue;
            }
            if (ts.LastTime < yesterday) {
                uploadTaskMap.remove(id);
            }
        }
    }

    private static class TaskStatus {

        public long LastTime;

        public String StatusStr;

    }

}
