package org.ark.framework.infrastructure.repositories;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ark.framework.infrastructure.IUnitOfWork;
import org.ark.framework.infrastructure.entityfactoryframework.EntityBuilderFactory;
import org.ark.framework.infrastructure.entityfactoryframework.IEntityFactory;
import org.ark.framework.infrastructure.repositoryframework.RepositoryBase;

import io.arkx.framework.commons.util.StringFormat;
import io.arkx.framework.data.db.connection.ConnectionPoolManager;
import io.arkx.framework.data.jdbc.BaseEntity;
import io.arkx.framework.data.jdbc.Entity;


/**
 * @class org.ark.framework.infrastructure.repositories.SqlRepositoryBase
 * @author Darkness
 * @date 2012-9-25 下午7:19:32
 * @version V1.0
 */
public abstract class SqlRepositoryBase<T extends Entity> extends RepositoryBase<T> {

	// #region Private Members

	private IEntityFactory<T> entityFactory;

	private Class<T> genericClass;
	@SuppressWarnings("rawtypes")
	private HashMap<String, AppendChildData> childCallbacks;
	private String baseQuery;
	private String baseWhereClause;

	// #region AppendChildData Delegate
	// The delegate signature required for callback methods
	public interface AppendChildData<T> {
		void append(T entityAggregate, Object childEntityKeyValue);
	}

	@SuppressWarnings("rawtypes")
	protected HashMap<String, AppendChildData> getChildCallbacks() {
		return this.childCallbacks;
	}

	// #endregion

	@SuppressWarnings("unchecked")
	protected Class<T> getGenericClass() {

		if (genericClass == null) {
			Type type = getClass().getGenericSuperclass();
			Type trueType = ((ParameterizedType) type).getActualTypeArguments()[0];
			genericClass = (Class<T>) trueType;
		}
		return genericClass;
	}

	// #endregion

	// #region Constructors

	protected SqlRepositoryBase() {
		this(null);
	}

	@SuppressWarnings("rawtypes")
	protected SqlRepositoryBase(IUnitOfWork unitOfWork) {
		super(unitOfWork);
		this.entityFactory = EntityBuilderFactory.buildFactory(getGenericClass());
		this.childCallbacks = new HashMap<String, AppendChildData>();
		this.BuildChildCallbacks();
		this.baseQuery = this.getBaseQuery();
		this.baseWhereClause = this.getBaseWhereClause();
	}

	// #endregion

	// #region Abstract Methods

	@Override
	public T findBy(Object key) {
		StringBuilder builder = this.getBaseQueryBuilder();
		builder.append(this.buildBaseWhereClause(key));
		return this.buildEntityFromSql(builder.toString());
	}

	// #endregion

	// #region Protected Methods

	/**
	 * 根据sql构建实体
	 * 
	 * @author Darkness
	 * @date 2012-9-27 上午9:26:03
	 * @version V1.0
	 */
	protected T buildEntityFromSql(String sql) {

		return EntityBuilderFactory.buildEntityFromSql(getGenericClass(), sql);
	}

	/**
	 * 根据sql查询结果集
	 * 
	 * @author Darkness
	 * @date 2012-9-27 上午9:23:27
	 * @version V1.0
	 */
	protected ResultSet executeQuery(String sql) {
		try {
			stmt = getConnection().prepareStatement(sql);
			return stmt.executeQuery();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	Connection mysqlConn = null;
	PreparedStatement stmt = null;

	private Connection getConnection() {
		if (mysqlConn == null) {
			mysqlConn = ConnectionPoolManager.getConnection();//ConnectionManager.getConnection();
		}
		return mysqlConn;
	}

	/**
	 * 根据sql构建实体集
	 * 
	 * @author Darkness
	 * @date 2012-9-27 上午9:26:49
	 * @version V1.0
	 */
	protected List<T> buildEntitiesFromSql(String sql) {

		return EntityBuilderFactory.buildEntitiesFromSql(getGenericClass(), sql);
	}

	// #endregion

	/**
	 * 查询所有有效Entity列表
	 * @method findAll
	 * @return {List<Entity>}
	 */
	@Override
	public List<T> findAll() {
		
		List<T> entities = findAllSortBy("DESC");
	
		findAllAfter(entities);
		
		return entities;
	}
	
	protected void findAllAfter(List<T> entities) {}
	
	public List<T> findAllAsc() {
		List<T> entities = findAllSortBy("asc");
		
		findAllAfter(entities);
		
		return entities;
	}
	
	private List<T> findAllSortBy(String sortOrderType) {
		StringBuilder builder = this.getBaseQueryBuilder();
		List<T> result = buildEntitiesFromSql(builder.toString() + " WHERE " + BaseEntity.UseFlag + "='Y' AND " + BaseEntity.DeleteStatus + "='N' ORDER BY SORT_ORDER " + sortOrderType);
		
		if(result == null) {
			return new ArrayList<T>();
		}
		
		return result;
	}
	
	
	
	/**
	 * 查询所有没有逻辑删除的Entity列表
	 * @method findAllLogic
	 * @return {List<Entity>}
	 * 
	 * @author Darkness
	 * @date 2013-2-1 下午01:36:14 
	 * @version V1.0
	 */
	public List<T> findAllLogic() {
		StringBuilder builder = this.getBaseQueryBuilder();
		List<T> result = buildEntitiesFromSql(builder.toString() + " WHERE " + BaseEntity.DeleteStatus + "='N' ORDER BY SORT_ORDER");
		
		if(result == null) {
			return new ArrayList<>();
		}
		
		return result;
	}

	protected StringBuilder getBaseQueryBuilder() {
		StringBuilder builder = new StringBuilder(50);
		builder.append(this.baseQuery);
		return builder;
	}

	protected String buildBaseWhereClause(Object key) {
		return StringFormat.format(this.baseWhereClause, key);
	}

	protected abstract void BuildChildCallbacks();

	protected abstract String getBaseQuery();

	protected abstract String getBaseWhereClause();
}
