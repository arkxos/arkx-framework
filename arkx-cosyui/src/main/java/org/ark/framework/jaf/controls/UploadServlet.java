package org.ark.framework.jaf.controls;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.core.FileItemFactory;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.ark.framework.jaf.Current;
import org.ark.framework.jaf.SessionListener;
import org.ark.framework.security.PrivCheck;
import org.ark.framework.security.VerifyCheck;

import io.arkx.framework.Account;
import io.arkx.framework.Config;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.data.db.DataCollection;
import io.arkx.framework.i18n.LangMapping;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * @author Darkness
 * @date 2013-1-31 下午12:45:41
 * @version V1.0
 */
public class UploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static String baseDir;
    private static boolean debug = false;

    private static Object mutex = new Object();

    private static Mapx<String, TaskFiles> uploadFileMap = new Mapx<String, TaskFiles>(5000);

    public void init() throws ServletException {
        baseDir = "Upload";
        if (baseDir == null)
            baseDir = "";
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (StringUtil.isEmpty(baseDir)) {
            baseDir = Config.getValue("UploadDir");
        }

        // Cookie cookie = new Cookie("JSESSIONID", "");
        // cookie.setMaxAge(0);
        // response.addCookie(cookie);

        response.setContentType("text/html; charset=" + Config.getGlobalCharset());
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        FileItemFactory fileFactory = DiskFileItemFactory.builder().get();
        JakartaServletFileUpload upload = new JakartaServletFileUpload(fileFactory);
        // upload.setHeaderEncoding("UTF-8");
        upload.setSizeMax(2 * 1024 * 1024 * 1000L);
        try {
            List<FileItem> items = upload.parseRequest(request);
            Mapx<String, String> fields = new Mapx<String, String>();
            ArrayList<FileItem> files = new ArrayList<FileItem>();
            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = iter.next();
                if (item.isFormField()) {
                    fields.put(item.getFieldName(), item.getString(Charset.forName("UTF-8")));
                } else {
                    String OldFileName = item.getName();
                    long size = item.getSize();
                    if (((OldFileName == null) || (OldFileName.equals(""))) && (size == 0L)) {
                        continue;
                    }
                    LogUtil.info("-----UploadFileName:-----" + OldFileName);
                    files.add(item);
                }
            }

            String taskID = fields.get("_ZUploder_TaskID");
            String totalStr = fields.get("_ZUploader_Total");

            UploadUI.setTask(taskID, LangMapping.get("Framework.Upload.Status"));

            String ids = fields.get("_SessionID");
            if (ObjectUtil.empty(ids)) {
                return;
            }
            HttpSession session = request.getSession();
            String[] arr = ids.split("\\,");
            HttpSession sessionOld = null;
            for (String sessionID : arr) {
                if (session.getId().equals(sessionID)) {
                    break;
                }
                sessionOld = io.arkx.framework.cosyui.web.mvc.SessionListener.getSession(sessionID);
                if (sessionOld != null) {
                    break;
                }
            }
            if (sessionOld != null) {
                Enumeration en = sessionOld.getAttributeNames();
                while (en.hasMoreElements()) {
                    String n = (String) en.nextElement();
                    session.setAttribute(n, sessionOld.getAttribute(n));
                }
                Account.UserData u = SessionListener.getUserDataFromSession(session);
                if (u != null) {
                    Account.setCurrent(u);
                }
            }

            int total = 1;
            if (ObjectUtil.notEmpty(totalStr)) {
                total = Integer.parseInt(totalStr);
            }
            TaskFiles uploadedFiles = null;
            synchronized (mutex) {
                checkTimeout();
                uploadedFiles = uploadFileMap.get(taskID);
                if (uploadedFiles == null) {
                    uploadedFiles = new TaskFiles();
                    uploadFileMap.put(taskID, uploadedFiles);
                }
                uploadedFiles.LastTime = System.currentTimeMillis();
            }
            uploadedFiles.Files.add(files.get(0));
            if (total == uploadedFiles.Files.size()) {
                String method = fields.get("_Method");
                Method m = Current.prepareMethod(request, response, method, new Class[]{UploadAction.class});
                Current.getRequest().putAll(fields);
                if (!PrivCheck.check(m, request, response)) {
                    uploadFileMap.remove(taskID);
                    for (FileItem file : uploadedFiles.Files) {
                        file.delete();
                    }
                    return;
                }

                if (!VerifyCheck.check(m)) {
                    String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
                    LogUtil.warn(message);

                    uploadFileMap.remove(taskID);
                    for (FileItem file : uploadedFiles.Files) {
                        file.delete();
                    }
                    return;
                }

                UploadAction ua = new UploadAction();
                ua.items = uploadedFiles.Files;
                Current.invokeMethod(m, new Object[]{ua});

                for (FileItem file : uploadedFiles.Files) {
                    file.delete();
                }
                synchronized (mutex) {
                    uploadFileMap.remove(taskID);
                }
                UploadUI.setTask(taskID, "Finished");
                response.getWriter().write(Current.getResponse().toXML());
            } else {
                response.getWriter().write(new DataCollection().toXML());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        out.flush();
        out.close();

        if (debug)
            LogUtil.info("--- END DOPOST ---");
    }

    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private static void checkTimeout() {
        ArrayList<String> arr = uploadFileMap.keyArray();
        long yesterday = System.currentTimeMillis() - 86400000L;
        for (String id : arr) {
            TaskFiles tf = (TaskFiles) uploadFileMap.get(id);
            if (tf == null) {
                continue;
            }
            if (tf.LastTime < yesterday)
                uploadFileMap.remove(id);
        }
    }

    private static class TaskFiles {
        public long LastTime;
        public ArrayList<FileItem> Files = new ArrayList<FileItem>();
    }
}
