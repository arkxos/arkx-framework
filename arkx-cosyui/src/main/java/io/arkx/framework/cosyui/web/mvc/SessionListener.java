package io.arkx.framework.cosyui.web.mvc;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.arkx.framework.Account;
import io.arkx.framework.Account.UserData;
import io.arkx.framework.Config;
import io.arkx.framework.Constant;
import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.extend.ExtendManager;
import io.arkx.framework.extend.action.AfterSessionCreateAction;
import io.arkx.framework.extend.action.BeforeSessionDestroyAction;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Session监听器，监听用户的增减情况，并提供用户相关的数据。
 *
 */
@WebListener
public class SessionListener implements HttpSessionListener {

    private static Map<String, HttpSession> map = new ConcurrentHashMap<>();

    public static Map<String, HttpSession> getMap() {
        return map;
    }

    public static HttpSession getSession(String id) {
        return (HttpSession) map.get(id);
    }

    public static void setSession(String id, HttpSession session) {
        map.put(id, session);
    }

    /**
     * 会话创建时执行本方法
     *
     * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession hs = event.getSession();
        map.put(hs.getId(), hs);

        ExtendManager.invoke(AfterSessionCreateAction.ExtendPointID, new Object[]{event.getSession()});
    }

    /**
     * 会话失效时执行本方法
     *
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        UserData u = SessionListener.getUserDataFromSession(event.getSession());
        if (u != null) {
            if (Config.isDebugMode()) {
                FileUtil.delete(Config.getContextRealPath() + "WEB-INF/cache/" + u.getSessionID());
            }
        }

        map.remove(event.getSession().getId());

        ExtendManager.invoke(BeforeSessionDestroyAction.ExtendPointID, new Object[]{event.getSession()});
    }

    /**
     * 踢出除自己以外的其他所有用户
     */
    public static void forceExit() {
        Map<String, HttpSession> map = getMap();
        for (Object k : map.keySet().toArray()) {
            if (k.equals(Account.getSessionID())) {
                continue;
            }
            map.get(k).invalidate();
        }
    }

    /**
     * 获取所有状态的用户
     *
     * @return
     */
    public static UserData[] getUsers() {
        ArrayList<UserData> arr = new ArrayList<>();
        for (HttpSession session : getMap().values()) {
            UserData u = SessionListener.getUserDataFromSession(session);
            if (u != null) {
                arr.add(u);
            }
        }
        return arr.toArray(new UserData[arr.size()]);
    }

    /**
     * 获取指定用户名的User对象
     *
     * @param userName
     *            用户名
     * @return
     */
    public static UserData getUser(String userName) {
        UserData[] users = getUsers();
        for (UserData user : users) {
            if (userName.equals(user.getUserName())) {
                return user;
            }
        }
        return null;
    }

    /**
     * 获取指定用户名的User对象
     *
     * @param userName
     *            用户名
     * @return
     */
    public static UserData[] getUsers(String userName) {
        ArrayList<UserData> result = new ArrayList<>(2);
        UserData[] users = getUsers();
        for (UserData user : users) {
            if (userName.equals(user.getUserName())) {
                result.add(user);
            }
        }
        return result.toArray(new UserData[result.size()]);
    }

    /**
     * 主要是为了防止类重新加载后Session中的UserData对象报ClassCastException
     */
    public static UserData getUserDataFromSession(HttpSession session) {
        if (session != null) {
            Object o = session.getAttribute(Constant.UserAttrName);
            if (o instanceof UserData) {
                return (UserData) o;
            }
        }
        return null;
    }

}
