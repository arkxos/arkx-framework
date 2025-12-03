package io.arkx.framework.cosyui.web.mvc.handler;

import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.arkx.framework.Config;
import io.arkx.framework.Constant;
import io.arkx.framework.WebCurrent;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.exception.ServiceException;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.commons.util.lang.ClassUtil;
import io.arkx.framework.config.LoginMethod;
import io.arkx.framework.core.JsonResult;
import io.arkx.framework.core.exception.UIMethodNotFoundException;
import io.arkx.framework.core.method.IMethodLocator;
import io.arkx.framework.core.method.MethodLocatorUtil;
import io.arkx.framework.cosyui.web.CookieData;
import io.arkx.framework.cosyui.web.RequestData;
import io.arkx.framework.cosyui.web.ResponseData;
import io.arkx.framework.cosyui.web.mvc.Dispatcher.DispatchException;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;
import io.arkx.framework.extend.ExtendManager;
import io.arkx.framework.extend.action.AfterUIMethodInvokeAction;
import io.arkx.framework.extend.action.BeforeUIMethodInvokeAction;
import io.arkx.framework.security.PrivCheck;
import io.arkx.framework.security.VerifyCheck;

import com.alibaba.fastjson.JSON;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Ajax请求处理者
 *
 */
public class AjaxHandler extends AbstractHtmlHandler {

    private Logger logger = LoggerFactory.getLogger(AjaxHandler.class);

    public static final String ID = "io.arkx.framework.core.AjaxHandler";

    @Override
    public String getExtendItemID() {
        return ID;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith("/ajax/invoke");
    }

    @Override
    public String getExtendItemName() {
        return "Ajax Server Invoke Processor";
    }

    @Override
    public boolean execute(String url, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // resultDataFormat可能为以下几个值之一
        // json,jsonp,html,xml,text,script,setWindowName,postWindowMessage
        String resultDataFormat = request.getParameter(Constant.DataFormat);
        String data = request.getParameter(Constant.Data);
        String method = request.getParameter(Constant.Method);
        if (resultDataFormat == null) {
            if (StringUtil.isEmpty(data)) {
                resultDataFormat = "json";
            } else {
                if (data.startsWith("<")) {
                    resultDataFormat = "xml";
                } else {
                    resultDataFormat = "json";
                }
            }
        }

        Session session = null;
        try {
            session = SessionFactory.openSessionInThread();
            session.beginTransaction();

            response.setContentType("text/html");

            if (Config.getServletMajorVersion() == 2 && Config.getServletMinorVersion() == 3) {
                response.setContentType("text/html;charset=utf-8");
            }
            request.setCharacterEncoding("UTF-8");

            if ("/index.zhtml".equals(request.getServletPath())) {// 重定向到Login.zhtml,以适应所有页面都打包到了ui.jar的情况
                response.sendRedirect("login.zhtml");
                return true;
            }
            IMethodLocator ml = MethodLocatorUtil.find(method);
            if (ml == null) {
                throw new UIMethodNotFoundException(method);
            }

            if ("".equals(url) || "/".equals(url)) {
                url = "/index.zhtml";
            }

            if (StringUtil.isEmpty(method)) {
                LogUtil.warn("Error in Server.sendRequest(),QueryString=" + request.getQueryString() + ",Referer="
                        + request.getHeader("referer"));
                return true;
            }

            PrivCheck.check(ml);

            // 参数检查
            if (!VerifyCheck.check(ml)) {
                String message = "Verify check failed:method=" + method + ",data=" + WebCurrent.getRequest();
                LogUtil.warn(message);
                WebCurrent.getResponse().setFailedMessage(message);
                write(resultDataFormat, response);
                return true;
            }

            // UIFacade方法执行前扩展
            ExtendManager.invoke(BeforeUIMethodInvokeAction.ExtendPointID, new Object[]{method});

            // 执行方法，并传入合适的参数
            Method m = ml.getMethod();
            Class<?>[] cs = m.getParameterTypes();
            Boolean isAction = false;
            ZAction action = null;

            JsonResult jsonResult = null;
            // 如果调用的是ZAction方法
            if (cs.length > 0 && (ArrayUtils.contains(cs, ZAction.class))) {// &&
                                                                            // cs[0].isAssignableFrom(ZAction.class))
                                                                            // {
                isAction = true;
                action = new ZAction();
                CookieData cookies = new CookieData(request);
                action.setCookies(cookies);
                action.setRequest(request);
                action.setResponse(response);
                ml.execute(new Object[]{action});
            } else {
                Object[] args = fixMethodParamsValue(m, null);
                Object result = ml.execute(args);
                if (result instanceof JsonResult) {
                    jsonResult = (JsonResult) result;
                }
            }

            // 登录后得确保产生session
            if (LoginMethod.isLoginMethod(ml.getName())) {
                if ((Config.isTomcat()) && (Config.getContainerVersion().startsWith("6"))) {
                    request.getSession(true);
                    StringBuilder sb = new StringBuilder();
                    sb.append("JSESSIONID=" + request.getSession().getId() + "; path=");
                    String path = request.getContextPath();
                    if (StringUtil.isEmpty(path)) {
                        sb.append('/');
                    } else {
                        sb.append(path.charAt(path.length() - 1) == '/' ? path.substring(0, path.length() - 1) : path);
                    }
                    sb.append("; HttpOnly");
                    response.setHeader("Set-Cookie", sb.toString());
                }
            }

            session.commit();

            // UIFacade方法执行后扩展
            ExtendManager.invoke(AfterUIMethodInvokeAction.ExtendPointID, method);

            if (jsonResult != null) {
                response.setContentType("application/json;charset=UTF-8");
                write(jsonResult, response);
            } else {
                // 将结果返回给页面
                if (isAction) {
                    write(resultDataFormat, response, action);
                } else {
                    write(resultDataFormat, response);
                }
            }
        } catch (ServiceException e) {
            // e.printStackTrace();
            logger.warn(e.getMessage());

            try {
                session.rollback();
                session.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            ServiceException serviceException = (ServiceException) e;
            WebCurrent.getResponse().setFailedMessage(serviceException.getMessage());
            WebCurrent.getResponse().put("serviceExceptionCode", serviceException.getCode());

            write(resultDataFormat, response);
        } catch (DispatchException e) {
            e.printStackTrace();

            try {
                session.rollback();
                session.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            ResponseData r = new ResponseData();
            String redirectURL = WebCurrent.getDispatcher().getRedirectURL();
            if (redirectURL == null) {
                redirectURL = WebCurrent.getDispatcher().getForwardURL();
            }
            r.put(Constant.ResponseScriptAttr, "window.location.href='" + Config.getContextPath() + redirectURL + "';");
            write(resultDataFormat, response);
        } catch (Exception e) {
            e.printStackTrace();

            try {
                session.rollback();
                session.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            WebCurrent.getResponse().setFailedMessage(e.getMessage());
            WebCurrent.getResponse().put("serviceExceptionCode", "500");

            write(resultDataFormat, response);
        } finally {
            SessionFactory.clearCurrentSession();
        }
        return true;
    }

    public static Object[] fixMethodParamsValue(Method m, Object[] args) {

        Class<?>[] cs = m.getParameterTypes();
        Object[] result = new Object[cs.length];

        String[] paramNames = ClassUtil.getMethodParamNames(m.getDeclaringClass(), m.getName());
        for (int j = 0; j < cs.length; j++) {
            // LogUtil.debug("方法第" + j + "个参数" + paramNames[j] + "[" + cs[j].getName() +
            // "]");

            boolean useArg = false;
            if ((args != null) && (args.length > j) && (cs[j].isInstance(args[j]))) {
                result[j] = args[j];
                useArg = true;

                // LogUtil.debug("匹配传进来的参数成功，直接采用传进来的参数值：" + args[j]);
            } else if (!useArg) {
                // LogUtil.debug("匹配失败，将从request中获取名称为[" + paramNames[j] + "]的值...");

                RequestData request = WebCurrent.getRequest();
                request.putAll(WebCurrent.getValues());
                Object[] keys = request.keySet().toArray();
                for (Object key : keys) {
                    if (paramNames[j].equalsIgnoreCase(key.toString())) {
                        Object paramValue = request.get(key);

                        LogUtil.debug("request中存在名称为[" + paramNames[j] + "]的值：" + paramValue);

                        result[j] = paramValue;
                        break;
                    }
                }

                // if (Schema.class.isAssignableFrom(cs[j])) {
                // Schema schema = null;
                // try {
                // schema = (Schema) cs[j].newInstance();
                // } catch (InstantiationException e) {
                // e.printStackTrace();
                // } catch (IllegalAccessException e) {
                // e.printStackTrace();
                // }
                // schema.setValue(getRequest());
                // result[j] = schema;
                // } else
                if (Mapx.class.isAssignableFrom(cs[j])) {
                    result[j] = request;
                }

                if (HttpServletRequest.class.isAssignableFrom(cs[j])) {
                    result[j] = request.getServletRequest();
                } else if (HttpServletResponse.class.isAssignableFrom(cs[j])) {
                    result[j] = WebCurrent.getResponse().getServletResponse();
                }
            }
        }

        return result;
    }

    private void write(String resultDataFormat, HttpServletResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (resultDataFormat.equalsIgnoreCase("setWindowName")) {
            response.setContentType("text/html");
            sb.append("<html><head>");
            sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=");
            sb.append(Config.getGlobalCharset());
            sb.append("\">");
            sb.append("</head><body>");
            sb.append("<script id=\"jsonData\" type=\"text/json\">");
            sb.append(WebCurrent.getResponse().toJSON());
            sb.append("</script>");
            sb.append("<script>window.name=document.getElementById('jsonData').innerHTML;</script>");
            sb.append("</body></html>");
        } else if (resultDataFormat.equalsIgnoreCase("json")) {
            response.setContentType("application/json");
            sb.append(WebCurrent.getResponse().toJSON());
        } else if (resultDataFormat.equalsIgnoreCase("xml")) {
            response.setContentType("text/xml");
            sb.append(WebCurrent.getResponse().toXML());
        }
        response.getWriter().write(sb.toString());
    }

    private void write(JsonResult jsonResult, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(JSON.toJSONString(jsonResult));
    }

    private void write(String resultDataFormat, HttpServletResponse response, ZAction action) throws IOException {
        if (resultDataFormat.equalsIgnoreCase("html")) {
            response.setContentType("text/html");
        } else if (resultDataFormat.equalsIgnoreCase("js")) {
            response.setContentType("text/javascript");
        } else if (resultDataFormat.equalsIgnoreCase("json")) {
            response.setContentType("application/json");
        }
        response.getWriter().print(action.getContent());
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
