package org.ark.framework.infrastructure;

import java.util.HashMap;
import java.util.Map;

import org.ark.framework.infrastructure.repositoryframework.IUnitOfWorkRepository;

import io.arkx.framework.data.jdbc.Entity;


/**
 * @class org.ark.framework.infrastructure.UnitOfWork
 * @author Darkness
 * @date 2012-9-25 下午7:15:37
 * @version V1.0
 */
public class UnitOfWork implements IUnitOfWork {
	
	@SuppressWarnings("rawtypes")
	private Map<Entity, IUnitOfWorkRepository> addedEntities;
	@SuppressWarnings("rawtypes")
	private Map<Entity, IUnitOfWorkRepository> changedEntities;
	@SuppressWarnings("rawtypes")
	private Map<Entity, IUnitOfWorkRepository> deletedEntities;

	@SuppressWarnings("rawtypes")
	public UnitOfWork() {
		this.addedEntities = new HashMap<Entity, IUnitOfWorkRepository>();
		this.changedEntities = new HashMap<Entity, IUnitOfWorkRepository>();
		this.deletedEntities = new HashMap<Entity, IUnitOfWorkRepository>();
	}

	// #region IUnitOfWork Members

	@SuppressWarnings("rawtypes")
	public void registerAdded(Entity entity, IUnitOfWorkRepository repository) {
		this.addedEntities.put(entity, repository);
	}

	@SuppressWarnings("rawtypes")
	public void registerChanged(Entity entity, IUnitOfWorkRepository repository) {
		this.changedEntities.put(entity, repository);
	}

	@SuppressWarnings("rawtypes")
	public void registerRemoved(Entity entity, IUnitOfWorkRepository repository) {
		this.deletedEntities.put(entity, repository);
	}

	@SuppressWarnings("unchecked")
	public void commit() {
		for (Entity entity : this.deletedEntities.keySet()) {
			this.deletedEntities.get(entity).persistDeletedItem(entity);
		}

		for (Entity entity : this.addedEntities.keySet()) {
			this.addedEntities.get(entity).persistDeletedItem(entity);
		}

		for (Entity entity : this.changedEntities.keySet()) {
			this.changedEntities.get(entity).persistDeletedItem(entity);
		}

		this.deletedEntities.clear();
		this.addedEntities.clear();
		this.changedEntities.clear();
	}

	// #endregion
}
