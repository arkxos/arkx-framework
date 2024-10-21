package org.ark.framework.infrastructure.repositoryframework;

import org.ark.framework.infrastructure.IUnitOfWork;

import com.rapidark.framework.data.jdbc.Entity;

/**
 * @class org.ark.framework.infrastructure.repositoryframework.RepositoryBase
 * @author Darkness
 * @date 2012-9-25 下午7:16:50
 * @version V1.0
 */
public abstract class RepositoryBase<T extends Entity> implements IRepository<T>, IUnitOfWorkRepository<T> {
	
	private IUnitOfWork unitOfWork;

	protected RepositoryBase() {
	}

	protected RepositoryBase(IUnitOfWork unitOfWork) {
		this.unitOfWork = unitOfWork;
	}

	// #region IRepository<T> Members

	public abstract T findBy(Object key);

	public void add(T item) {
		if (this.unitOfWork != null) {
			this.unitOfWork.registerAdded(item, this);
		}
	}

	public void remove(T item) {
		if (this.unitOfWork != null) {
			this.unitOfWork.registerRemoved(item, this);
		}
	}

	public T get(Object key) {
		return this.findBy(key);
	}

	public T set(Object key, T value) {
		if (this.findBy(key) == null) {
			this.add(value);
		} else {
			this.unitOfWork.registerChanged(value, this);
		}
		return value;
	}

	// #endregion
}
