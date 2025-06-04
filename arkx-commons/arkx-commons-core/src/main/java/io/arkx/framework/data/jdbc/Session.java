package io.arkx.framework.data.jdbc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ark.framework.infrastructure.repositories.extend.EntityDeleteExtendAction;
import org.ark.framework.infrastructure.repositories.extend.EntitySaveExtendAction;

import io.arkx.framework.Account;
import io.arkx.framework.annotation.Column;
import io.arkx.framework.annotation.EntityAnnotationManager;
import io.arkx.framework.annotation.Ingore;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.commons.util.lang.ReflectionUtil;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionPoolManager;
import io.arkx.framework.extend.ExtendManager;

/**   
 * @class org.ark.framework.infrastructure.repositories.OrmManager
 * @author Darkness
 * @date 2012-11-25 下午01:36:45 
 * @version V1.0   
 */
public class Session {
	
	public static final String PRIMARY_KEY_NAME = "id";

	private Transaction transaction;
	String poolName;
	
	Session() {
		this(ConnectionPoolManager.DEFAULT_POOLNAME);
	}
	
	Session(String poolName) {
		this.poolName = poolName;
	}
	
	public void beginTransaction() {
		this.transaction = TransactionFactory.create(poolName);
		transaction.begin();
	}
	
	public Session readOnly() {
		this.transaction = TransactionFactory.createReadOnly(poolName);
		return this;
	}
	
	public void commit() {
		transaction.commit();
		transaction = null;
	}
	
	public void rollback() {
		if(transaction != null) {
			transaction.rollback();
		}
	}
	
	public void close() {
		if(transaction != null) {
			transaction.close();
		}
	}
	
	public Connection getConnection() {
		return transaction.getConnection();
	}
	
	public Query createQuery() {
		return new Query(transaction);
	}
	
	public Query createQuery(String sql, Object... params) {
		return new Query(transaction, sql, params);
	}
	
	public SimpleQuery createSimpleQuery() {
		return new SimpleQuery(transaction);
	}
	
	public SimpleQuery createSimpleQuery(String sql, Object... params) {
		return new SimpleQuery(transaction, sql, params);
	}
	
	public Criteria createCriteria(Class<? extends Entity> domainObjectClass) {
		return new Criteria(transaction, domainObjectClass);
	}
	
	public <T extends Entity> T findById(Class<T> clazz, String id) {
		Criteria criteria = new Criteria(transaction, clazz);
		criteria.add(Restrictions.eq(Entity.Id, id));
		
		return criteria.findEntity();
	}

	public <T extends Entity> T findByExample(Class<T> clazz, T example) {
		Criteria criteria = new Criteria(transaction, clazz);
		return criteria.findEntityByExample(example);
	}
	
	/**
	 * 保存实体
	 * 
	 * @author Darkness
	 * @date 2012-11-25 下午03:11:37 
	 * @version V1.0
	 */
	public <T extends Entity> int save(T entity) {
		
		List<Entity> list = new ArrayList<>();
		list.add(entity);
		
		ExtendManager.invoke(EntitySaveExtendAction.ExtendPointID, entity);
		
		return save(list);
	}
	
	/**
	 * 保存实体集合
	 * 
	 * @author Darkness
	 * @date 2012-11-25 下午03:11:46 
	 * @version V1.0
	 */
	public <T extends Entity> int save(List<T> entities) {
		
		if(entities == null || entities.isEmpty()) {
			return 0;
		}
		
		long sortOrder = 0;
		if(BaseEntity.class.isAssignableFrom(entities.get(0).getClass())) {
			sortOrder = new JdbcTemplate(getConnection()).executeLong("SELECT max(SORT_ORDER) FROM " + EntityAnnotationManager.getTableName(entities.get(0).getClass()));
		}
		
		for (T entity : entities) {
			if(StringUtil.isEmpty(entity.getId())) {
				entity.generateNewId();
			}
			
			if(BaseEntity.class.isAssignableFrom(entity.getClass())) {
				BaseEntity baseEntity = (BaseEntity)entity;
				baseEntity.setCreateTime(new Date());
				baseEntity.setUpdateTime(baseEntity.getCreateTime());
				baseEntity.setSortOrder(++sortOrder);
				
				if(!StringUtil.isEmpty(Account.getId())) {
					if(StringUtil.isEmpty(baseEntity.getCreatorId())) {
						baseEntity.setCreatorId(Account.getId());
					}
					if(StringUtil.isEmpty(baseEntity.getUpdatorId())) {
						baseEntity.setUpdatorId(Account.getId());
					}
				}
			}
		}

		Field[] fields = ReflectionUtil.getDeclaredFields(entities.get(0).getClass());

		String sql = "INSERT INTO " + EntityAnnotationManager.getTableName(entities.get(0).getClass()) + " (";
		String valuesSql = "";
		
		boolean isFirst = true;
		
		for (Field field : fields) {

			Ingore ingore = field.getAnnotation(Ingore.class);
			if(ingore != null) {
				continue;
			}
			
			if (!isFirst) {
				sql += ",";
				valuesSql += ",";
			}
			isFirst = false;

			String columnName = EntityAnnotationManager.getColumnName(field);
			
			sql += columnName + " ";
			valuesSql += "?";
		}
		sql += ") VALUES(" + valuesSql + ")";

		Query queryBuilder = new Query(transaction, sql);
		
		List<ArrayList<Object>> paramsList = new ArrayList<>();
		for (Entity entity : entities) {
			
			ArrayList<Object> params = new ArrayList<>();
			
			for (Field field : fields) {

				Ingore ingore = field.getAnnotation(Ingore.class);
				if(ingore != null) {
					continue;
				}
				
				params.add(ReflectionUtil.getFieldValue(entity, field.getName()));
			}
			
			paramsList.add(params);
		}
		
		queryBuilder.addBatch(paramsList);
		return queryBuilder.executeNoQuery();
	}
	
	/**
	 * 更新实体
	 * 
	 * @author Darkness
	 * @date 2012-11-25 下午03:12:00 
	 * @version V1.0
	 */
	public <T extends Entity> int update(T entity) {
		List<Entity> list = new ArrayList<>();
		list.add(entity);
		
		if(BaseEntity.class.isAssignableFrom(entity.getClass())) {
			BaseEntity baseEntity = (BaseEntity)entity;
			baseEntity.setUpdateTime(new Date());
			if(!StringUtil.isEmpty(Account.getId())) {
				if(StringUtil.isEmpty(baseEntity.getUpdatorId())) {
					baseEntity.setUpdatorId(Account.getId());
				}
			}
		}
		
		return update(list);
	}
	
	/**
	 * 更新实体集合
	 * 
	 * @author Darkness
	 * @date 2012-11-25 下午03:12:06 
	 * @version V1.0
	 */
	public <T extends Entity> int update(List<T> entities) {
		
		if(entities == null || entities.size() == 0) {
			return 0;
		}
		
		Field[] fields = ReflectionUtil.getDeclaredFields(entities.get(0).getClass());

		String sql = "UPDATE " + EntityAnnotationManager.getTableName(entities.get(0).getClass()) + " SET ";
		boolean isFirst = true;
		for (Field field : fields) {

			if (PRIMARY_KEY_NAME.equalsIgnoreCase(field.getName())) {
				continue;
			}
			
			Ingore ingore = field.getAnnotation(Ingore.class);
			if(ingore != null) {
				continue;
			}

			if (!isFirst) {
				sql += ",";
			}
			isFirst = false;

			String columnName = field.getName();
			
			Column column = field.getAnnotation(Column.class);
			if(column != null) {
				columnName = column.name();
			}
			
			sql += columnName + "=?";
		}
		sql += " WHERE " + PRIMARY_KEY_NAME + "=?";
		
		List<ArrayList<Object>> paramsList = new ArrayList<>();
		for (Entity entity : entities) {
			
			if(BaseEntity.class.isAssignableFrom(entity.getClass())) {
				BaseEntity baseEntity = (BaseEntity)entity;
				baseEntity.setUpdateTime(new Date());
				if(!StringUtil.isEmpty(Account.getId())) {
					if(StringUtil.isEmpty(baseEntity.getUpdatorId())) {
						baseEntity.setUpdatorId(Account.getId());
					}
				}
			}
			
			ArrayList<Object> params = new ArrayList<>();
			for (Field field : fields) {

				if (PRIMARY_KEY_NAME.equalsIgnoreCase(field.getName())) {
					continue;
				}
				
				Ingore ingore = field.getAnnotation(Ingore.class);
				if(ingore != null) {
					continue;
				}

				params.add(ReflectionUtil.getFieldValue(entity, field.getName()));
			}
			params.add(ReflectionUtil.getFieldValue(entity, PRIMARY_KEY_NAME));
			
			paramsList.add(params);
		}
		
		
		Query queryBuilder = new Query(transaction , sql);
		
		queryBuilder.addBatch(paramsList);
		
		return queryBuilder.executeNoQuery();
	}
	
	/**
	 * 根据实体主键列表删除实体
	 * 
	 * @author Darkness
	 * @date 2012-11-25 下午03:12:18 
	 * @version V1.0
	 */
	public <T extends Entity> int delete(Class<T> clazz, String ids) {
		
		String tableName = EntityAnnotationManager.getTableName(clazz);
		return delete(tableName, ids);
	}
	
	public <T extends Entity> int delete(String tableName, String ids) {
		
		String sql = "DELETE FROM " + tableName + " WHERE id in (" + ids + ")";
		int rows = new Query(transaction, sql).executeNoQuery();
		
		ExtendManager.invoke(EntityDeleteExtendAction.ExtendPointID, new Object[]{tableName, ids});
		
		return rows;
	}
	
	/**
	 * 删除实体
	 * 
	 * @author Darkness
	 * @date 2012-11-25 下午04:09:38 
	 * @version V1.0
	 */
	public <T extends Entity> int delete(T entity) {
		List<Entity> list = new ArrayList<>();
		list.add(entity);
		
		return delete(list);
	}
	
	/**
	 * 删除实体集合
	 * 
	 * @author Darkness
	 * @date 2012-11-25 下午03:12:36 
	 * @version V1.0
	 */
	public <T extends Entity> int delete(List<T> entities) {
		
		if(entities == null || entities.size() == 0) {
			return 0;
		}
		
		String ids = "";
		for (T entity : entities) {
			if(ids.length() > 0){
				ids += ",'" + entity.getId() + "'";
			}else{
				ids = "'"+entity.getId()+"'";
			}
			
		}
		
		return delete(entities.get(0).getClass(), ids);
	}
}
