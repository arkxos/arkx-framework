//package org.ark.framework.infrastructure.repositories;
//
//import java.util.List;
//
//import com.rapidark.framework.data.jdbc.Entity;
//
//
///**
// * @class org.ark.framework.infrastructure.repositories.CachedRepository
// * @author Darkness
// * @date 2013-1-25 下午03:13:01
// * @version V1.0
// */
//public class CachedRepository<T extends Entity> extends BaseRepository<T> {
//
//	protected List<T> entities;
//
//	protected static Object mutex = new Object();
//
//	@Override
//	public T findById(String id) {
//		for (T entity : findAll()) {
//			if (entity.getId().equals(id)) {
//				return entity;
//			}
//		}
//		return null;
//	}
//
//	@Override
//	public List<T> findAll() {
//
//		if (entities == null) {
//			entities = super.findAll();
//			findAllAfter();
//		}
//		return entities;
//	}
//	
//	protected void findAllAfter() {}
//
//	@Override
//	public T save(T entity) {
//
//		T _entity = super.save(entity);
//
//		reloadEntities();
//
//		return _entity;
//	}
//	
//	public void update(T entity) {
//		
//		super.update(entity);
//		
//		reloadEntities();
//	}
//
//	@Override
//	public int delete(String ids) {
//
//		int rows = super.delete(ids);
//
//		reloadEntities();
//
//		return rows;
//	}
//
//	public void reloadEntities() {
//		synchronized (mutex) {
//			entities = super.findAll();
//			findAllAfter();
//		}
//	}
//
//}
