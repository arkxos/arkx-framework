package com.rapidark.framework.data.hibernate;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateSessionSupport {

	/**
	 * Autowired 自动装配 相当于get() set()
	 */
	@Autowired(required=false)
	protected SessionFactory sessionFactory;

	private Session session;

	protected HibernateSessionSupport() {
		super();
	}

	protected HibernateSessionSupport(Session aSession) {
		this();

		this.setSession(aSession);
	}

	public Session session() {
		Session actualSession = this.session;

		if (actualSession == null) {

			actualSession = sessionFactory.getCurrentSession();

			// This is not a lazy creation and should not be set on
			// this.session. Setting the session instance assumes that
			// you have used the single argument constructor for a single
			// use. If actualSession is set by the sessionProvider then
			// this instance is for use only by the current thread and
			// must not be retained for subsequent requests.
		}

		return actualSession;
	}

	protected void setSession(Session aSession) {
		this.session = aSession;
	}
	
	public static String uuid() {
		return UUID.randomUUID().toString();
	}

	public void flush() {
		session().flush();
	}

	public void clear() {
		session().clear();
	}

	/**
	 * 根据 id 查询信息
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T load(Class<T> c, String id) {
		Session session = session();
		return (T) session.get(c, id);
	}

	/**
	 * 获取所有信息
	 * 
	 * @param c
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public List getAllList(Class c) {
		String hql = "from " + c.getName();
		Session session = session();
		return session.createQuery(hql).list();
	}

	/**
	 * 获取总数量
	 * 
	 * @param c
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Long getTotalCount(Class c) {
		Session session = session();
		String hql = "select count(*) from " + c.getName();
		Long count = (Long) session.createQuery(hql).uniqueResult();
		session.close();
		return count != null ? count.longValue() : 0;
	}

	/**
	 * 保存
	 * 
	 * @param bean
	 * 
	 */
	public void save(Object bean) {
		try {
			Session session = session();
			session.save(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新
	 * 
	 * @param bean
	 * 
	 */
	public void update(Object bean) {
		Session session = session();
		session.update(bean);
	}

	/**
	 * 删除
	 * 
	 * @param bean
	 * 
	 */
	public void delete(Object bean) {
		Session session = session();
		session.delete(bean);
	}

	/**
	 * 根据ID删除
	 * 
	 * @param c 类
	 * 
	 * @param id ID
	 * 
	 */
	@SuppressWarnings({ "rawtypes" })
	public void delete(Class c, String id) {
		Session session = session();
		Object obj = session.get(c, id);
		session.delete(obj);
	}

	/**
	 * 批量删除
	 * 
	 * @param c 类
	 * 
	 * @param ids ID 集合
	 * 
	 */
	@SuppressWarnings({ "rawtypes" })
	public void delete(Class c, String... ids) {
		for (String id : ids) {
			Object obj = session().get(c, id);
			if (obj != null) {
				session().delete(obj);
			}
		}
	}
}
