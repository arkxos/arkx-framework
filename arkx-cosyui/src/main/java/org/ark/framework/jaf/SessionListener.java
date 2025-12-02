package org.ark.framework.jaf;

import java.util.ArrayList;
import java.util.Map;

import io.arkx.framework.Account;
import io.arkx.framework.Config;
import io.arkx.framework.Constant;
import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.extend.ExtendManager;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * @class org.ark.framework.SessionListener session 监听器，存储、获取当前在线用户的信息，操作当前在线用户
 * @author Darkness
 * @date 2012-8-6 下午10:12:38
 * @version V1.0
 */
public class SessionListener implements HttpSessionListener {

	/**
	 * session被创建调用： 当前在线人数加一，将当前sessioin加入到session列表中，执行session创建后的扩展点
	 */
	public void sessionCreated(HttpSessionEvent arg0) {
		// Config.OnlineUserCount += 1;
		ExtendManager.invoke("org.ark.framework.AfterSessionCreate", new Object[] { arg0.getSession() });
	}

	/**
	 * session销毁调用： 当前在线人数减一，删除用户缓存数据，执行session销毁前扩展点，从session列表中删除当前session
	 */
	public void sessionDestroyed(HttpSessionEvent arg0) {
		// Config.OnlineUserCount -= 1;
		Account.UserData u = getUserDataFromSession(arg0.getSession());
		if (u != null) {
			// if ((u.isLogin()) && (Config.LoginUserCount > 0)) {
			// Config.LoginUserCount -= 1;
			// }

			if (Config.isDebugMode()) {
				FileUtil.delete(Config.getContextRealPath() + "WEB-INF/cache/" + u.getSessionID());
			}
		}
		ExtendManager.invoke("org.ark.framework.BeforeSessionDestory", new Object[] { arg0.getSession() });
	}

	/**
	 * 强制其他用户退出，只保留当前用户在线
	 *
	 * @author Darkness
	 * @date 2012-8-6 下午10:22:44
	 * @version V1.0
	 */
	public static void forceExit() {
		Map<String, HttpSession> map = io.arkx.framework.cosyui.web.mvc.SessionListener.getMap();
		for (Object k : map.keySet().toArray()) {
			if (k.equals(Account.getSessionID())) {
				continue;
			}
			map.get(k).invalidate();
		}
	}

	/**
	 * 强制特定用户退出
	 *
	 * @author Darkness
	 * @date 2012-8-6 下午10:23:48
	 * @version V1.0
	 */
	public static void forceExit(String sid) {
		HttpSession session = io.arkx.framework.cosyui.web.mvc.SessionListener.getMap().get(sid);
		session.invalidate();
	}

	/**
	 * 获取在线用户信息
	 *
	 * @author Darkness
	 * @date 2012-8-6 下午10:24:01
	 * @version V1.0
	 */
	public static Account.UserData[] getUsers() {
		ArrayList<Account.UserData> arr = new ArrayList<Account.UserData>();
		for (HttpSession session : io.arkx.framework.cosyui.web.mvc.SessionListener.getMap().values()) {
			Account.UserData u = getUserDataFromSession(session);
			if (u != null) {
				arr.add(u);
			}
		}
		return arr.toArray(new Account.UserData[arr.size()]);
	}

	/**
	 * 获取特定状态的在线用户信息
	 *
	 * @author Darkness
	 * @date 2012-8-6 下午10:24:21
	 * @version V1.0
	 */
	public static Account.UserData[] getUsers(String status) {
		ArrayList<Account.UserData> arr = new ArrayList<Account.UserData>();
		for (HttpSession session : io.arkx.framework.cosyui.web.mvc.SessionListener.getMap().values()) {
			Account.UserData u = getUserDataFromSession(session);
			if ((u == null) || (!status.equalsIgnoreCase(u.getStatus())))
				continue;
			arr.add(u);
		}

		return arr.toArray(new Account.UserData[arr.size()]);
	}

	/**
	 * 获取特定状态的用户名列表
	 *
	 * @author Darkness
	 * @date 2012-8-6 下午10:25:00
	 * @version V1.0
	 */
	public static ArrayList<String> getUserNames(String status) {
		Account.UserData[] arr = getUsers(status);
		ArrayList<String> userNameArr = new ArrayList<String>(arr.length);
		for (int i = 0; i < arr.length; i++) {
			userNameArr.add(arr[i].getUserName());
		}
		return userNameArr;
	}

	/**
	 * 获取指定用户的信息
	 *
	 * @author Darkness
	 * @date 2012-8-6 下午10:25:17
	 * @version V1.0
	 */
	public static Account.UserData getUser(String userName) {
		Account.UserData[] users = getUsers();
		for (int i = 0; i < users.length; i++) {
			if (userName.equals(users[i].getUserName())) {
				return users[i];
			}
		}
		return null;
	}

	public static Account.UserData getUserDataFromSession(HttpSession session) {
		Object o = session.getAttribute(Constant.UserAttrName);
		if (o != null) {
			if (!(o instanceof Account.UserData)) {
				return null;
			}
			return (Account.UserData) o;
		}
		return null;
	}

}
