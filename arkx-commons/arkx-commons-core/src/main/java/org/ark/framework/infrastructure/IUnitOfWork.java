package org.ark.framework.infrastructure;

import org.ark.framework.infrastructure.repositoryframework.IUnitOfWorkRepository;

import io.arkx.framework.data.jdbc.Entity;

/**
 * @class org.ark.framework.infrastructure.IUnitOfWork
 * @author Darkness
 * @date 2012-9-25 下午7:14:02
 * @version V1.0
 */
public interface IUnitOfWork {
	
	<T extends Entity> void registerAdded(Entity entity, IUnitOfWorkRepository<T> repository);

	<T extends Entity> void registerChanged(Entity entity, IUnitOfWorkRepository<T> repository);

	<T extends Entity> void registerRemoved(Entity entity, IUnitOfWorkRepository<T> repository);

	void commit();
}
