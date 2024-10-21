package com.rapidark.framework.data.jdbc;

/**
 * Session session = SessionFactory.openSessionInThread(poolName);
	session.beginTransaction();
	
	SessionFactory.currentSession().commit();
			SessionFactory.clearCurrentSession();
		
 * @author Darkness
 * @date 2017年5月7日 下午3:11:21
 * @version 1.0
 * @since 1.0 
 */
public class SessionFactory {

	private static ThreadLocal<Session> current = new ThreadLocal<>();
	
	public static Session currentSession() {
		return current.get();
	}
	
	public static void clearCurrentSession() {
		current.set(null);
	}
	
	public static void setCurrentSession(Session session) {
		current.set(session);
	}
	
	public static Session openSession() {
		Session session = new Session();
		return session;
	}
	
	public static Session openSessionInThread(String poolName) {
		Session session = new Session(poolName);
		current.set(session);
		return session;
	}
	
	public static Session openSessionInThread() {
		Session session = new Session();
		current.set(session);
		return session;
	}
	
}
