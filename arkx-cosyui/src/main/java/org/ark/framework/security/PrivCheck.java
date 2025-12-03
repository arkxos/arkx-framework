package org.ark.framework.security;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

import org.ark.framework.jaf.Current;
import org.ark.framework.jaf.PlaceHolderContext;

import io.arkx.framework.Account;
import io.arkx.framework.Config;
import io.arkx.framework.Member;
import io.arkx.framework.annotation.Priv;
import io.arkx.framework.commons.util.Html2Util;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.web.RequestData;
import io.arkx.framework.cosyui.web.ResponseData;
import io.arkx.framework.extend.ExtendManager;
import io.arkx.framework.extend.action.AfterPrivCheckFailedAction;
import io.arkx.framework.extend.action.PrivExtendAction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @class org.ark.framework.security.PrivCheck 权限检测
 * @author Darkness
 * @date 2012-8-5 下午7:32:13
 * @version V1.0
 */
public class PrivCheck {

    public static boolean check(Method m, HttpServletRequest request, HttpServletResponse response) {
        if (true) {
            return true;
        }
        if ("false".equals(Current.getVariable(PrivTag.PrivCheckAttr))) {
            return false;
        }
        if (!check(m)) {
            try {
                String noPrivPage = null;
                boolean flag = m.isAnnotationPresent(Priv.class);
                if (flag) {
                    Priv priv = m.getAnnotation(Priv.class);

                    if ((priv.loginType() == Priv.LoginType.User) && (!Account.isLogin())) {
                        response.sendRedirect(Config.getContextPath() + Config.getLoginPage());
                        return false;
                    }

                    noPrivPage = Config.getContextPath() + "NoPrivilege.zhtml?Method=" + m.getDeclaringClass().getName()
                            + "." + m.getName() + "&login=" + priv.login() + "&loginType=" + priv.loginType()
                            + "&userType=" + priv.userType() + "&value=" + priv.value();
                } else {
                    noPrivPage = Config.getContextPath() + "NoPrivilege.zhtml?Method=" + m.getDeclaringClass().getName()
                            + "." + m.getName();
                }

                String referer = request.getRequestURI();
                if (StringUtil.isNotEmpty(request.getQueryString())) {
                    referer = referer + "?" + request.getQueryString();
                }
                referer = URLEncoder.encode(referer, Config.getGlobalCharset());
                response.sendRedirect(noPrivPage + "&Referer=" + referer);

                log("Privilege check failed:" + m.getDeclaringClass().getName() + "." + m.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public static boolean check(Method m, RequestData request, ResponseData response) {
        if (true) {
            return true;
        }
        if ("false".equals(Current.getVariable(PrivTag.PrivCheckAttr))) {
            return false;
        }
        if (!check(m)) {
            String noPrivPage = null;
            boolean flag = m.isAnnotationPresent(Priv.class);
            if (flag) {
                Priv priv = m.getAnnotation(Priv.class);

                if ((priv.loginType() == Priv.LoginType.User) && (!Account.isLogin())) {
                    response.put("_ARK_SCRIPT",
                            "window.location.href='" + Config.getContextPath() + Config.getLoginPage() + "';");
                    return false;
                }
                noPrivPage = Config.getContextPath() + "NoPrivilege.zhtml?Method=" + m.getDeclaringClass().getName()
                        + "." + m.getName() + "&login=" + priv.login() + "&loginType=" + priv.loginType() + "&userType="
                        + priv.userType() + "&value=" + priv.value();
            } else {
                noPrivPage = Config.getContextPath() + "NoPrivilege.zhtml?Method=" + m.getDeclaringClass().getName()
                        + "." + m.getName();
            }

            String referer = request.getHeaders().getString("referer");
            response.put("_ARK_SCRIPT", "window.location.href='" + noPrivPage + "&Referer=" + referer + "';");
            return false;
        }
        return true;
    }

    public static boolean check(Method m) {
        if (true) {
            return true;
        }
        if (m == null) {
            return false;
        }
        if ("false".equals(Current.getVariable(PrivTag.PrivCheckAttr))) {
            return false;
        }

        boolean flag = m.isAnnotationPresent(Priv.class);
        if (flag) {
            Priv priv = m.getAnnotation(Priv.class);
            String parentPriv = "";
            if (m.getClass().isAnnotationPresent(Priv.class)) {
                parentPriv = m.getClass().getAnnotation(Priv.class).value();
            }
            return check(priv.login(), priv.loginType(), priv.userType(), priv.value(), parentPriv,
                    Current.getPlaceHolderContext());
        }
        return false;
    }

    public static boolean check(boolean login, Priv.LoginType loginType, String userType, String priv,
            PlaceHolderContext context) {
        return check(login, loginType, userType, priv, "", context);
    }

    /**
     * 检测是否通过
     *
     * 检查规则： 1、不需要登录，返回true 2、loginType 为 Priv.LoginType.User，User没有登录，返回false
     * 3、loginType 为 Priv.LoginType.Member，Member没有登录，返回false
     *
     * @param login
     *            是否需要登录
     * @param loginType
     *            Priv.LoginType
     * @author Darkness
     * @date 2012-8-5 下午7:32:59
     * @version V1.0
     */
    public static boolean check(boolean login, Priv.LoginType loginType, String userType, String priv,
            String parentPriv, PlaceHolderContext context) {
        if (!login) {
            return true;
        }
        if (login) {
            if ((loginType == Priv.LoginType.User) && (!Account.isLogin())) {
                log("Privilege check failed:User isn't logined");
                return false;
            }
            if ((loginType == Priv.LoginType.Member) && (!Member.isLogin())) {
                log("Privilege check failed:Member isn't logined");
                return false;
            }

            if (ObjectUtil.notEmpty(userType)) {
                if ((loginType == Priv.LoginType.User) && (ObjectUtil.notEqual(Account.getType(), userType))) {
                    LogUtil.warn("Privilege check failed:User.UserType is not applicable:" + userType);
                    return false;
                }

                if ((loginType == Priv.LoginType.Member) && (ObjectUtil.notEqual(Member.getType(), userType))) {
                    LogUtil.warn("Privilege check failed:Member.UserType is not applicable:" + userType);
                    return false;
                }
            }
            return check(context, priv, parentPriv);
        }
        return false;
    }

    private static void log(String message) {
        LogUtil.warn(message);

        ExtendManager.invoke(AfterPrivCheckFailedAction.ID, new Object[]{message});
    }

    public static boolean check(String priv) {
        return check(Current.getPlaceHolderContext(), priv);
    }

    public static boolean check(PlaceHolderContext context, String priv) {
        return check(context, priv, "");
    }

    /**
     * @author Darkness
     * @date 2012-8-5 下午7:30:39
     * @version V1.0
     */
    public static boolean check(PlaceHolderContext context, String priv, String parentPriv) {
        if (ObjectUtil.empty(priv)) {
            return true;
        }
        String[] items = StringUtil.splitEx(priv, "||");
        for (String item : items) {
            item = item.trim();
            if (ObjectUtil.empty(item)) {
                continue;
            }
            if (!ObjectUtil.empty(parentPriv)) {
                item = parentPriv + "." + item;
            }
            if ((context != null) && (item.indexOf("${") >= 0)) {
                item = Html2Util.replacePlaceHolder(item, context, false, false);
            }
            Object[] arr = ExtendManager.invoke(PrivExtendAction.ExtendPointID, new Object[]{item});
            boolean allowFlag = false;
            int itemFlag = 0;
            if (arr != null) {
                for (Object obj : arr) {
                    Integer flag = (Integer) obj;
                    if (flag.intValue() == -1) {
                        itemFlag = flag.intValue();
                        break;
                    }
                    if (flag.intValue() == 1) {
                        allowFlag = true;
                    }
                }
            }
            if ((itemFlag != -1) && (allowFlag)) {
                return true;
            }
        }
        return false;
    }

}
