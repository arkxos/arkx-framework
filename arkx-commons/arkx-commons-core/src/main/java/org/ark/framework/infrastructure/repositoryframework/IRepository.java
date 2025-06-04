package org.ark.framework.infrastructure.repositoryframework;

import java.util.List;

import io.arkx.framework.data.jdbc.Entity;


/**
 * @class org.ark.framework.infrastructure.repositoryframework.IRepository
 * @author Darkness
 * @date 2012-9-25 下午6:49:10
 * @version V1.0
 */
public interface IRepository<T extends Entity> {
	
	T findBy(Object key);
	
	List<T> findAll();

	void add(T item);

	T get(Object key);

	T set(Object key, T entity);

	void remove(T item);
}
