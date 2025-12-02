package io.arkx.framework;

import io.arkx.framework.cosyui.web.mvc.SessionListener;

import jakarta.servlet.http.HttpSession;

/**
 * @author Nobody
 * @date 2025-06-04 20:29
 * @since 1.0
 */
public class MyWebConfig {

	/**
	 * @return 己登录的后台用户数
	 */
	public static int getLoginUserCount() {
		int count = 0;
		for (HttpSession session : SessionListener.getMap().values()) {
			Account.UserData ud = SessionListener.getUserDataFromSession(session);
			if (ud != null && ud.isLogin()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * @return 己登录的会员数
	 */
	public static int getLoginMemberCount() {
		int count = 0;
		for (HttpSession session : SessionListener.getMap().values()) {
			Account.UserData ud = SessionListener.getUserDataFromSession(session);
			if (ud != null && ud.getMemberData() != null && ud.getMemberData().isLogin) {
				count++;
			}
		}
		return count;
	}

	public static int getOnlineUserCount() {
		return SessionListener.getMap().size();
	}

}
