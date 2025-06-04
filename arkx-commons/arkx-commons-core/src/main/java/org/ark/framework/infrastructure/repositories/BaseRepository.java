package org.ark.framework.infrastructure.repositories;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Supplier;

import io.arkx.framework.annotation.EntityAnnotationManager;
import io.arkx.framework.annotation.Unique;
import io.arkx.framework.cache.CacheManager;
import com.arkxos.framework.commons.exception.ServiceException;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.lang.ReflectionUtil;
import com.arkxos.framework.data.jdbc.BaseEntity;
import com.arkxos.framework.data.jdbc.Criteria;
import com.arkxos.framework.data.jdbc.Entity;
import com.arkxos.framework.data.jdbc.Restrictions;
import com.arkxos.framework.data.jdbc.Session;
import com.arkxos.framework.data.jdbc.SessionFactory;
import com.arkxos.framework.i18n.LangMapping;


/**
 * @class org.ark.framework.infrastructure.repositories.BaseRepository
 * @extends org.ark.framework.infrastructure.repositories.SqlRepositoryBase
 * Entity仓储基类
 * @author Darkness
 * @date 2012-10-29 下午08:36:13
 * @version V1.0
 */
public class BaseRepository<T extends Entity> extends SqlRepositoryBase<T> {

	private Class<T> genericClass;

	@SuppressWarnings("unchecked")
	protected Class<T> getGenericClass() {

		if (genericClass == null) {
			Type type = getClass().getGenericSuperclass();
			Type trueType = ((ParameterizedType) type).getActualTypeArguments()[0];
			genericClass = (Class<T>) trueType;
		}
		return genericClass;
	}
	
	private Session session;
	
	public Session getSession() {
		if(session == null) {
			return SessionFactory.currentSession();
		}
		return session;
	}
	
	public void setSession(Session session) {
		this.session = session;
	}

	protected String getTableName() {

		return EntityAnnotationManager.getTableName(getGenericClass());
	}

	/**
	 * 是否启用缓存
	 * 
	 * @author Darkness
	 * @date 2012-12-18 下午04:00:32 
	 * @version V1.0
	 */
	private boolean needCache() {
		
		return EntityCacheManager.isUseCache(getGenericClass());
	}
	
	/**
	 * 根据id查询Entity
	 * @method findById
	 * @param {String} id
	 * @return {Entity}
	 * 
	 * @author Darkness
	 * @date 2013-2-1 上午11:22:33 
	 * @version V1.0
	 */
	@SuppressWarnings("unchecked")
	public T findById(String id) {
		
		String entityName = getGenericClass().getName();
		
		if(needCache()) {
			if(CacheManager.contains(OrmEntityCache.ProviderID, entityName, id)) {
				return (T)CacheManager.get(OrmEntityCache.ProviderID, entityName, id);
			}
		}
		
		T entity = buildEntityFromSql("SELECT * FROM " + getTableName() + " WHERE ID='" + id + "'");
		
		if(needCache() && entity != null) {
			CacheManager.set(OrmEntityCache.ProviderID, entityName, id, entity);
		}
		
		return entity;
	}

	/**
	 * 保存Entity如果没设置id，默认设置一个值
	 * @method save
	 * @param {Entity} entity
	 * @return {Entity}
	 * @author Darkness
	 * @date 2012-11-20 上午10:30:20 
	 * @version V1.0
	 */
	public T save(T entity) {
		
		// 获取unique注解的字段
		List<Field> uniqueFields = EntityAnnotationManager.getUniqueFields(entity.getClass());
		// 检测字段是否唯一
		for (Field field : uniqueFields) {
			Object fieldValue = ReflectionUtil.getFieldValue(entity, field.getName());

			Criteria criteria = getSession().createCriteria(entity.getClass());
			
			String columnName = EntityAnnotationManager.getColumnName(field);
			
			criteria.add(Restrictions.eq(columnName, fieldValue));
			T existEntity = criteria.findEntity();

			if (existEntity != null) {
				Unique unique = field.getAnnotation(Unique.class);
				throw new ServiceException(unique.value() + "【" + fieldValue + "】已存在！");
			}
		}
		
		getSession().save(entity);
		
		if(needCache() && entity != null) {
			CacheManager.set(OrmEntityCache.ProviderID, getTableName(), entity.getId(), entity);
		}
		
		return entity;
	}
	
	/**
	 * 保存实体，可以复写copy逻辑
	 * @private
	 * @author Darkness
	 * @date 2012-11-27 上午10:56:03 
	 * @version V1.0
	 */
	public T saveWithCopy(T entity, String copyId) {
		
		save(entity);
		
		return entity;
	}

	/**
	 * 更新Entity
	 * @method update
	 * @param {Entity} entity
	 * 
	 * @author Darkness
	 * @date 2013-2-1 上午11:24:21 
	 * @version V1.0
	 */
	public void update(T entity) {
		
		getSession().update(entity);
		
		if(needCache() && entity != null) {
			CacheManager.set(OrmEntityCache.ProviderID, getTableName(), entity.getId(), entity);
		}
	}
	
	/**
	 * 根据ids删除Entity，ids格式为:  "'1','2','3'"
	 * @method delete
	 * @param {String} ids
	 * @return {int} 删除的行数
	 * @author Darkness
	 * @date 2012-10-30 下午08:35:36 
	 * @version V1.0
	 */
	public int delete(String ids) {
		
		if(!ids.startsWith("'")) {
			ids = "'" + ids + "'";
		}
		
		return getSession().delete(getGenericClass(), ids);
	}
	
	/**
	 * 根据ids逻辑删除Entity，ids格式为:  "'1','2','3'"
	 * @method logicDelete
	 * @param {String} ids
	 * @return {int} 删除的行数
	 * 
	 * @author Darkness
	 * @date 2013-2-1 上午11:28:48 
	 * @version V1.0
	 */
	public int logicDelete(String ids) {
		
		if(!ids.startsWith("'")) {
			ids = "'" + ids + "'";
		}
		
		String sql = "UPDATE " + getTableName() + " SET " + BaseEntity.DeleteStatus + " = 'N' WHERE id IN("+ids+")";
		return getSession().createQuery(sql).executeNoQuery();
	}
	
	/**
	 * 根据ids启用Entity，ids格式为:  "'1','2','3'"
	 * @method enable
	 * @param {String} ids
	 * @return {int} 启用的行数
	 * 
	 * @author Darkness
	 * @date 2013-2-1 下午01:31:37 
	 * @version V1.0
	 */
	public int enable(String ids) {
		if(!ids.startsWith("'")) {
			ids = "'" + ids + "'";
		}
		
		String sql = "UPDATE " + getTableName() + " SET " + BaseEntity.UseFlag + " = 'Y' WHERE id IN("+ids+")";
		return getSession().createQuery(sql).executeNoQuery();
	}
	
	/**
	 * 根据ids禁用Entity，ids格式为:  "'1','2','3'"
	 * @method disable
	 * @param {String} ids
	 * @return {int} 禁用的行数
	 * 
	 * @author Darkness
	 * @date 2013-2-1 下午01:31:37 
	 * @version V1.0
	 */
	public int disable(String ids) {
		if(!ids.startsWith("'")) {
			ids = "'" + ids + "'";
		}
		
		String sql = "UPDATE " + getTableName() + " SET " + BaseEntity.UseFlag + " = 'N' WHERE id IN("+ids+")";
		return getSession().createQuery(sql).executeNoQuery();
	}

	@Override
	public void persistNewItem(T item) {
	}

	@Override
	public void persistUpdatedItem(T item) {
	}

	@Override
	public void persistDeletedItem(T item) {
	}

	@Override
	protected void BuildChildCallbacks() {
	}

	@Override
	protected String getBaseQuery() {
		return "SELECT * FROM " + getTableName();
	}

	@Override
	protected String getBaseWhereClause() {
		return null;
	}
	
	/**
	 * 根据属性查找Entity
	 * @method findOneByProperty
	 * @param {String} field
	 * @param {Object} value
	 * @return {Entity}
	 * 
	 * @author Darkness
	 * @date 2012-12-13 上午09:56:29 
	 * @version V1.0
	 */
	public T findOneByProperty(String field, Object value) {
		return findOneByRestrictions(()->Restrictions.eq(field, value));
	}
	
	public T findOneByRestrictions(Supplier<Restrictions> supplier) {
		Criteria criteria = getSession().createCriteria(getGenericClass());
		criteria.add(supplier.get());
		return criteria.findEntity();
	}
	
	/**
	 * 根据属性查找Entity列表
	 * @method findListByProperty
	 * @param {String} field
	 * @param {Object} value
	 * @return {List<Entity>}
	 * 
	 * @author Darkness
	 * @date 2013-2-1 上午11:26:25 
	 * @version V1.0
	 */
	public List<T> findListByProperty(String field, Object value) {
		return findListByRestrictions(() -> Restrictions.eq(field, value));
	}
	
	public List<T> findListByRestrictions(Supplier<Restrictions> supplier) {
		Criteria criteria = getSession().createCriteria(getGenericClass());
		criteria.add(supplier.get());
		return criteria.findEntities();
	}
	
	/**
	 * 根据属性查找Entity列表
	 * @method findListByProperty
	 * @param {String} field
	 * @param {Object} value
	 * @return {List<Entity>}
	 * 
	 * @author Darkness
	 * @date 2013-2-1 上午11:26:25 
	 * @version V1.0
	 */
	public List<T> findListByProperties(Object... args) {
		
		Criteria criteria = getSession().createCriteria(getGenericClass());

		for (int i=0; i<args.length;) {
			criteria.add(Restrictions.eq(args[i]+"", args[i+1]));
			i+=2;
		}
		
		return criteria.findEntities();
	}

	/**
	 * 根据id查询对象列表
	 * 
	 * @author Darkness
	 * @date 2013-4-1 下午03:21:01 
	 * @version V1.0
	 */
	public List<T> findByIds(String ids) {
		
		if (ObjectUtil.empty(ids)) {
			throw new ServiceException(LangMapping.get("Common.InvalidID"));
		}
		
		Criteria criteria = getSession().createCriteria(getGenericClass());
		
		// ids: "1, 2, 3",after replace: ids = "1','2','3";
		ids = ids.replaceAll(",", "','");
		
		//here ids :  "'1','2','3'"
		ids = "'" + ids + "'";

		criteria.add(Restrictions.in(Entity.Id, ids));
		return criteria.findEntities();
	}
}
