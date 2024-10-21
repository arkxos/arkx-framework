package org.ark.framework.infrastructure.repositoryframework;

import com.rapidark.framework.data.jdbc.Entity;

/**
 * @class org.ark.framework.infrastructure.repositoryframework.IUnitOfWorkRepository
 * @author Darkness
 * @date 2012-9-25 下午7:14:42
 * @version V1.0
 */
public interface IUnitOfWorkRepository<T extends Entity> {
	
	void persistNewItem(T item);

	void persistUpdatedItem(T item);

	void persistDeletedItem(T item);
}
