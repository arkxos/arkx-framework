package io.arkx.data.lightning.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.repository.query.RelationalExampleMapper;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import io.arkx.data.lightning.plugin.treetable.closure.entity.BizTableMeta;
import io.arkx.data.lightning.plugin.treetable.closure.service.ClosureTableService;
import io.arkx.data.lightning.plugin.treetable.closure.service.ClosureTableServiceImpl;
import io.arkx.data.lightning.plugin.treetable.closure.service.TreeTableUtil;
import io.arkx.framework.boot.spring.IocBeanRegister;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.tree.Treex;
import io.arkx.framework.commons.exception.ServiceException;
import io.arkx.framework.commons.util.ArkSpringContextHolder;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.commons.util.SystemIdGenerator;
import io.arkx.framework.commons.util.UuidUtil;
import io.arkx.framework.data.common.entity.*;
import io.arkx.framework.data.common.repository.ExtBaseRepository;
import io.arkx.framework.data.jdbc.ResultDataTable;

/**
 * @author Nobody
 * @date 2025-07-26 16:34
 * @since 1.0
 */
@NoRepositoryBean
public class BaseJdbcRepositoryImpl<T extends Persistable<ID>, ID extends Serializable>
		extends SimpleJdbcRepository<T, ID> implements ExtBaseRepository<T, ID> {

	private final JdbcAggregateOperations entityOperations;

	private final PersistentEntity<T, ?> persistentEntity;

	private final RelationalExampleMapper exampleMapper;

	// private final NamedParameterJdbcOperations operations;

	private Class<T> domainClass;

	private SystemIdGenerator systemIdGenerator;

	private boolean isStatusAble = false; // 实体类是否弃用状态字段

	private Method statusReadMethod;// 状态字段读方法

	private Method statusWriteMethod;// 状态字段写方法

	public BaseJdbcRepositoryImpl(JdbcAggregateOperations entityOperations, PersistentEntity<T, ?> entity,
			JdbcConverter converter) {
		super(entityOperations, entity, converter);
		this.entityOperations = entityOperations;
		// this.operations = operations;
		this.domainClass = entity.getTypeInformation().getType();
		this.persistentEntity = entity;
		this.exampleMapper = new RelationalExampleMapper(converter.getMappingContext());
		this.initStatusInfo();
	}

	@Override
	public <S extends T> void batchInsert(List<S> entities) {
		for (S entity : entities) {
			insert(entity);
		}
	}

	@Override
	public <S extends T> S insert(S entity) {
		if (entity instanceof LongId baseEntity) {
			if (baseEntity.getId() == null) {
				Long id = getSystemIdGenerator().generate();
				baseEntity.setId(id);
			}
		}
		else if (entity instanceof StringId baseEntity) {
			if (StringUtil.isEmpty(baseEntity.getId())) {
				baseEntity.setId(UuidUtil.base58Uuid());
			}
		}

		if (TreeEntity.class.isAssignableFrom(entity.getClass())) {
			TreeEntity<?> treeEntity = (TreeEntity<?>) entity;
			String bizTable = "xxx";
			BizTableMeta meta = TreeTableUtil.findBizTableMeta(treeEntity.getClass());

			ClosureTableService closureTableService = ArkSpringContextHolder.getBean(ClosureTableService.class);

			closureTableService.insertClosureRelations(treeEntity.getId(), // nodeId
					treeEntity.getParentId(), // parentId
					meta, // businessTable
					meta.getIdType() // idType
			);

			// if (StringUtil.isEmpty(treeEntity.getInnerCode())) {
			// if (StringUtil.isEmpty(treeEntity.getParentInnerCode()) ||
			// "0".equals(treeEntity.getParentInnerCode())) {
			// treeEntity.setInnerCode(NoUtil.getMaxNo(treeEntity.getClass().getSimpleName()
			// + "InnerCode", 4));
			// treeEntity.setParentInnerCode(treeEntity.getInnerCode());
			//
			// Long sortOrder = jdbcTemplate().queryForObject(
			// "SELECT max(SORT_ORDER) FROM " + getTableName() + " ORDER BY SORT_ORDER",
			// Long.class);
			// if (sortOrder == null) {
			// sortOrder = 0L;
			// }
			// treeEntity.setSortOrder(sortOrder + 1L);
			// } else {
			//
			// Example<TreeEntity> treeEntityExample = Example.of(treeEntity,
			// exampleMapper);
			// Query query = entityManager.createQuery("FROM " +
			// entity.getClass().getSimpleName() + " WHERE
			// innerCode='"+treeEntity.getParentInnerCode()+"'");
			// TreeEntity parentEntity = (TreeEntity)query.getSingleResult();
			//
			// if (StringUtil.isEmpty(treeEntity.getInnerCode())) {
			// treeEntity.setInnerCode(NoUtil.getMaxNo(treeEntity.getClass().getSimpleName()
			// + "InnerCode", parentEntity.getInnerCode(), 4));
			// }
			//// treeEntity.setParentInnerCode(parentEntity.getInnerCode());
			// treeEntity.setTreeLevel(parentEntity.getTreeLevel() + 1L);
			//
			// Long sortOrder = jdbcTemplate().queryForObject(
			// "SELECT max(SORT_ORDER) FROM " + getTableName() + " WHERE INNER_CODE LIKE
			// ?",
			// new Object[] {parentEntity.getInnerCode() + "%"}, Long.class);
			// if (sortOrder == null) {
			// sortOrder = 0L;
			// }
			// treeEntity.setSortOrder(sortOrder + 1L);
			//
			// if ("Y".equals(parentEntity.getIsLeaf())) {
			// parentEntity.setIsLeaf("N");
			// }
			// if (parentEntity.getIsTreeLeaf() == 1) {
			// parentEntity.setIsTreeLeaf(0);
			// }
			//// getSession().createQuery("update " +
			// EntityAnnotationManager.getTableName(entity.getClass()) + " set
			// sort_order=sort_order+1 where sort_order>?", orderflag).executeNoQuery();
			// }
			// }
		}

		return this.entityOperations.insert(entity);
	}

	@Override
	public <S extends T> S update(S entity) {
		// 1. 执行默认更新逻辑
		S updatedEntity = this.entityOperations.update(entity);
		// 2. 树表增强逻辑
		if (isTreeEntity(entity)) {
			handleTreeEntityUpdate((TreeEntity) entity);
		}
		return updatedEntity;
	}

	private <S extends T> boolean isTreeEntity(S entity) {
		return entity instanceof TreeEntity;
	}

	private void handleTreeEntityUpdate(TreeEntity<ID> entity) {
		ClosureTableServiceImpl closureService = ArkSpringContextHolder.getBean(ClosureTableServiceImpl.class);

		T oldEntity = findById(entity.getId()).orElse(null);
		BizTableMeta meta = TreeTableUtil.findBizTableMeta(entity.getClass());
		if (oldEntity != null && isParentChanged((TreeEntity) oldEntity, entity)) {
			closureService.updateClosureRelations(entity.getId(), entity.getParentId(), meta);
		}
	}

	private ClosureTableServiceImpl getClosureService() {
		return ArkSpringContextHolder.getBean(ClosureTableServiceImpl.class);
	}

	private IdType getIdType(TreeEntity entity) {
		return entity instanceof LongId ? IdType.LONG : IdType.STRING;
	}

	private boolean isParentChanged(TreeEntity oldEntity, TreeEntity newEntity) {
		return !Objects.equals(oldEntity.getParentId(), newEntity.getParentId());
	}

	@Override
	public void delete(T entity) {
		// 1. 执行默认删除逻辑
		this.entityOperations.delete(entity);

		// 2. 树表增强逻辑
		if (isTreeEntity(entity)) {
			BizTableMeta meta = TreeTableUtil.findBizTableMeta((Class<? extends TreeEntity>) entity.getClass());
			getClosureService().deleteClosureRelations(((TreeEntity) entity).getId(), meta);
		}
	}

	/**
	 * 初始化状态信息
	 */
	private void initStatusInfo() {
		PropertyDescriptor descriptor = findFieldPropertyDescriptor(persistentEntity.getType(), Status.class);
		isStatusAble = descriptor != null;
		if (isStatusAble) {
			statusReadMethod = descriptor.getReadMethod();
			statusWriteMethod = descriptor.getWriteMethod();
		}
	}

	/**
	 * 根据id查询map结构数据
	 */
	@Override
	public Map<ID, T> mget(Collection<ID> ids) {
		return toMap(findAllById(ids));
	}

	@Override
	public Map<ID, T> mgetOneByOne(Collection<ID> ids) {
		return toMap(findAllOneByOne(ids));
	}

	@Override
	public List<T> findAllOneByOne(Collection<ID> ids) {
		List<T> results = new ArrayList<>();
		for (ID id : ids) {
			findById(id).ifPresent(results::add);
		}
		return results;
	}

	private Map<ID, T> toMap(List<T> list) {
		Map<ID, T> result = new LinkedHashMap<>();
		for (T t : list) {
			if (t != null) {
				result.put((ID) persistentEntity.getIdentifierAccessor(t).getIdentifier(), t);
			}
		}
		return result;
	}

	/**
	 * 启用、禁用对象
	 */
	@Override
	@Transactional
	public void toggleStatus(ID id) {
		if (isStatusAble && id != null) {
			Optional<T> targetOptional = findById(id);
			if (targetOptional.isPresent()) {
				T target = targetOptional.get();
				Status status = (Status) ReflectionUtils.invokeMethod(statusReadMethod, target);
				if (status == Status.ACTIVE || status == Status.INACTIVE) {
					ReflectionUtils.invokeMethod(statusWriteMethod, target,
							status == Status.INACTIVE ? Status.ACTIVE : Status.INACTIVE);
					save(target);
				}
			}
		}
	}

	/**
	 * 逻辑删除
	 */
	@Override
	@Transactional
	public void fakeDelete(ID... ids) {
		for (ID id : ids) {
			changeStatus(id, Status.DELETED);
		}
	}

	/**
	 * 改变实体状态
	 * @param id
	 * @param status
	 */
	private void changeStatus(ID id, Status status) {
		if (isStatusAble && id != null) {
			Optional<T> targetOptional = findById(id);
			if (targetOptional.isPresent()) {
				T target = targetOptional.get();
				Status oldStatus = (Status) ReflectionUtils.invokeMethod(statusReadMethod, target);
				if (oldStatus != status) {
					ReflectionUtils.invokeMethod(statusWriteMethod, target, status);
					save(target);
				}
			}
		}
	}

	@Override
	public boolean support(String modelType) {
		return domainClass.getName().equals(modelType);
	}

	public SystemIdGenerator getSystemIdGenerator() {
		if (systemIdGenerator == null) {
			systemIdGenerator = ArkSpringContextHolder.getBean(SystemIdGenerator.class);
		}
		return systemIdGenerator;
	}

	@Override
	@Transactional
	public <S extends T> S save(S entity) {
		if (entity.isNew()) {
			return this.insert(entity);
		}
		return this.update(entity);
	}

	@Override
	public DataTable queryDataTable(String sql, Object... params) {
		DataTable dataTable = jdbcTemplate().query(sql, params, new ResultSetExtractor<DataTable>() {

			@Override
			public DataTable extractData(ResultSet rs) throws SQLException, DataAccessException {
				return new ResultDataTable(rs);
			}
		});

		return dataTable;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map<String, Object>> queryMap(String sql, Object... params) {
		// Query query = entityManager.createNativeQuery(sql);
		// if(params != null) {
		// int i = 0;
		// for (Object param : params) {
		// query.setParameter(i++, param);
		// }
		// }
		//
		// query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		// List rows = query.getResultList();
		// return rows;
		return null;
	}

	@Override
	public List<T> queryList(String sql, Object... params) {
		// Query query = entityManager.createNativeQuery(sql);
		// if(params != null) {
		// int i = 1;
		// for (Object param : params) {
		// query.setParameter(i++, param);
		// }
		// }
		//
		// query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.TO_LIST);
		// List rows = query.getResultList();
		// return rows;
		return null;
	}

	// public long queryForLong(String sql, Object... params) {
	// Object result = jdbcTemplate().queryForObject(sql, params, Object.class);
	// if(result == null) {
	// return 0L;
	// }
	// return Long.valueOf(result+"");
	// }

	@Override
	public long queryForLong(String sql, Object... params) {
		List<Long> results = jdbcTemplate().query(sql, params,
				new RowMapperResultSetExtractor<>(new SingleColumnRowMapper<>(Long.class)));
		if (results == null) {
			return 0L;
		}
		if (results.isEmpty()) {
			return 0L;
		}
		if (results.get(0) == null) {
			return 0L;
		}

		return results.get(0);
	}

	@Override
	public int executeSql(String sql, Object... params) {
		return jdbcTemplate().update(sql, params);
	}

	@Override
	public Treex<String, T> queryTreeByParentId(String parentId) {
		if (TreeEntity.class.isAssignableFrom(domainClass)) {
			// TreeEntity<?> treeEntity = (TreeEntity<?>) entity;
			String bizTable = "xxx";
			BizTableMeta meta = TreeTableUtil.findBizTableMeta((Class<? extends TreeEntity>) domainClass);

			ClosureTableService closureTableService = ArkSpringContextHolder.getBean(ClosureTableService.class);

			return closureTableService.queryTreeData(parentId, meta, (Class<? extends TreeEntity>) domainClass);
		}
		return null;
	}

	@Override
	public Treex<String, T> findAllTree() {
		return this.queryTreeByParentId(null);
	}

	@Override
	public List<T> findChildrenByParentId(ID parentId) {
		// 构建查询条件
		Query query = query(where("parent_id").is(parentId)).sort(Sort.by("sort_order").ascending());
		return this.entityOperations.findAll(query, domainClass);
	}

	public String getTableName() {
		// 通过EntityManager获取factory
		// EntityManagerFactory entityManagerFactory =
		// entityManager.getEntityManagerFactory();
		// SessionFactoryImpl sessionFactory =
		// (SessionFactoryImpl)entityManagerFactory.unwrap(SessionFactory.class);
		// Map<String, EntityPersister> persisterMap =
		// sessionFactory.getMetamodel().entityPersisters();
		// for(Map.Entry<String,EntityPersister> entity : persisterMap.entrySet()){
		// Class<?> targetClass = entity.getValue().getMappedClass();
		// SingleTableEntityPersister persister =
		// (SingleTableEntityPersister)entity.getValue();
		//
		// String entityName = targetClass.getSimpleName();//Entity的名称
		// String tableName = persister.getTableName();//Entity对应的表的英文名
		//
		//// System.out.println("类名：" + entityName + " => 表名：" + tableName);
		//
		// if (targetClass == domainClass) {
		// return tableName;
		// }
		//
		// //属性
		//// Iterable<AttributeDefinition> attributes = persister.getAttributes();
		//// for(AttributeDefinition attr : attributes){
		//// String propertyName = attr.getName(); //在entity中的属性名称
		//// String[] columnName = persister.getPropertyColumnNames(propertyName);
		// //对应数据库表中的字段名
		//// String type = "";
		//// PropertyDescriptor targetPd = BeanUtils.getPropertyDescriptor(targetClass,
		// propertyName);
		//// if(targetPd != null){
		//// type = targetPd.getPropertyType().getSimpleName();
		//// }
		//// System.out.println("属性名：" + propertyName + " => 类型：" + type + " => 数据库字段名："
		// + columnName[0]);
		//// }
		// }
		throw new ServiceException("非jpa管理的类" + domainClass);
	}

	public JdbcTemplate jdbcTemplate() {
		return IocBeanRegister.getBean(JdbcTemplate.class);
	}

}
