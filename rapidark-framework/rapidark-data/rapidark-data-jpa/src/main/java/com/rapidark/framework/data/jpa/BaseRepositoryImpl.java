package com.rapidark.framework.data.jpa;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

import com.rapidark.framework.common.utils.ArkSpringContextHolder;
import com.rapidark.framework.common.utils.SystemIdGenerator;
import com.rapidark.framework.data.jpa.entity.*;

import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.rapidark.framework.Account;
import com.rapidark.framework.NoUtil;
import com.rapidark.framework.boot.spring.IocBeanRegister;
import com.rapidark.framework.boot.spring.axon.Auditor;
import com.rapidark.framework.boot.spring.axon.CurrentAuditor;
import com.rapidark.framework.commons.collection.DataTable;
import com.rapidark.framework.commons.exception.ServiceException;
import com.rapidark.framework.commons.util.StringUtil;
import com.rapidark.framework.commons.util.UuidUtil;
import com.rapidark.framework.data.jdbc.ResultDataTable;

/**
 * <p>通用Jpa仓库实现</p>
 * @author Darkness
 * @date 2019-07-19 18:41:49
 * @version V1.0
 */
public class BaseRepositoryImpl<T extends Object, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements BaseRepository<T, ID> {

	private final Class<T> domainClass;
	private EntityManager entityManager;
	JpaEntityInformation<T, Serializable> entityInformation;
	private SystemIdGenerator systemIdGenerator;
	private boolean isStatusAble = false; // 实体类是否弃用状态字段
    private Method statusReadMethod;// 状态字段读方法
    private Method statusWriteMethod;// 状态字段写方法
    
	public BaseRepositoryImpl(JpaEntityInformation<T, Serializable> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
		this.entityInformation = entityInformation;
		this.domainClass = entityInformation.getJavaType();
		this.initStatusInfo();
	}

	/**
	 * 初始化状态信息
	 */
	private void initStatusInfo() {
		PropertyDescriptor descriptor = findFieldPropertyDescriptor(entityInformation.getJavaType(), Status.class);
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
                result.put((ID)entityInformation.getId(t), t);
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
                if (status == Status.ENABLED || status == Status.DISABLED) {
                    ReflectionUtils.invokeMethod(statusWriteMethod, target,
                            status == Status.DISABLED ? Status.ENABLED : Status.DISABLED);
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

    /**
     * 获取对象属性描述
     * @param target
     * @param fieldClass
     * @return
     */
    private PropertyDescriptor findFieldPropertyDescriptor(Class<?> target, Class<?> fieldClass) {
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(target);
        for (PropertyDescriptor pd : propertyDescriptors) {
            if (pd.getPropertyType() == fieldClass) {
                return pd;
            }
        }
        return null;
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
		if(entity instanceof BaseEntity) {
			BaseEntity baseEntity = (BaseEntity)entity;
			String operatorId = "";
			LocalDateTime occrOn = LocalDateTime.now();
			
			Auditor currentAuditor = CurrentAuditor.get(); 
			if(currentAuditor != null) {
				operatorId = currentAuditor.getOperatorId();
				occrOn = currentAuditor.getOccurredOn();
			} else {
				operatorId = Account.getUserName();
				occrOn = LocalDateTime.now();
			}
			
			if(StringUtil.isEmpty(baseEntity.getCreateBy())) {
				baseEntity.setCreateBy(operatorId);
				baseEntity.setCreateTime(occrOn);
			}
			
			if(StringUtil.isEmpty(baseEntity.getUpdateBy())) {
				baseEntity.setUpdateBy(operatorId);
				baseEntity.setUpdateTime(occrOn);
			}
		}

		if(entity instanceof AbstractIdLongEntity) {
			AbstractIdLongEntity baseEntity = (AbstractIdLongEntity)entity;
			if (baseEntity.getId() == null) {
				Long id = getSystemIdGenerator().generate();
				baseEntity.setId(id);
			}
		} else if(entity instanceof AbstractIdStringEntity) {
			AbstractIdStringEntity baseEntity = (AbstractIdStringEntity)entity;
			if (StringUtil.isEmpty(baseEntity.getId())) {
				baseEntity.setId(UuidUtil.base58Uuid());
			}
		}

		if (TreeEntity.class.isAssignableFrom(entity.getClass())) {
			TreeEntity treeEntity = (TreeEntity) entity;

			if (StringUtil.isEmpty(treeEntity.getInnerCode())) {
				if (StringUtil.isEmpty(treeEntity.getParentInnerCode()) || "0".equals(treeEntity.getParentInnerCode())) {
					treeEntity.setInnerCode(NoUtil.getMaxNo(treeEntity.getClass().getSimpleName() + "InnerCode", 4));
					treeEntity.setParentInnerCode(treeEntity.getInnerCode());

					Long sortOrder = jdbcTemplate().queryForObject(
							"SELECT max(SORT_ORDER) FROM " + getTableName() + " ORDER BY SORT_ORDER", Long.class);
					if (sortOrder == null) {
						sortOrder = 0L;
					}
					treeEntity.setSortOrder(sortOrder + 1L);
				} else {
					Query query = entityManager.createQuery("FROM " + entity.getClass().getSimpleName() + " WHERE innerCode='"+treeEntity.getParentInnerCode()+"'");
					TreeEntity parentEntity = (TreeEntity)query.getSingleResult();

					if (StringUtil.isEmpty(treeEntity.getInnerCode())) {
						treeEntity.setInnerCode(NoUtil.getMaxNo(treeEntity.getClass().getSimpleName() + "InnerCode", parentEntity.getInnerCode(), 4));
					}
//					treeEntity.setParentInnerCode(parentEntity.getInnerCode());
					treeEntity.setTreeLevel(parentEntity.getTreeLevel() + 1L);

					Long sortOrder = jdbcTemplate().queryForObject(
							"SELECT max(SORT_ORDER) FROM " + getTableName() + " WHERE INNER_CODE LIKE ?", new Object[] {parentEntity.getInnerCode() + "%"}, Long.class);
					if (sortOrder == null) {
						sortOrder = 0L;
					}
					treeEntity.setSortOrder(sortOrder + 1L);

					if ("Y".equals(parentEntity.getIsLeaf())) {
						parentEntity.setIsLeaf("N");
					}
					if (parentEntity.getIsTreeLeaf() == 1) {
						parentEntity.setIsTreeLeaf(0);
					}
//					getSession().createQuery("update " + EntityAnnotationManager.getTableName(entity.getClass()) + " set sort_order=sort_order+1 where sort_order>?", orderflag).executeNoQuery();
				}
			}
		}
		
		return super.save(entity);
	}
	
	@Override
	public DataTable queryDataTable(String sql, Object... params) {
		DataTable dataTable = jdbcTemplate().query(sql, params, new ResultSetExtractor<DataTable>() {

			@Override
			public DataTable extractData(ResultSet rs) throws SQLException, DataAccessException {
				return new ResultDataTable(rs);
			}});
		
		return dataTable;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map<String, Object>> queryMap(String sql, Object... params) {
		Query query = entityManager.createNativeQuery(sql);
		if(params != null) {
			int i = 0;
			for (Object param : params) {
				query.setParameter(i++, param);
			}
		}
		
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List rows = query.getResultList();  
		return rows;
	}
	
//	public long queryForLong(String sql, Object... params) {
//		Object result = jdbcTemplate().queryForObject(sql, params, Object.class);
//		if(result == null) {
//			return 0L;
//		}
//		return Long.valueOf(result+"");
//	}
	
	public long queryForLong(String sql, Object... params)  {
		List<Long> results = jdbcTemplate().query(sql, params, new RowMapperResultSetExtractor<Long>(new SingleColumnRowMapper<>(Long.class)));
		if(results.isEmpty()) {
			return 0L;
		}
		if(results.get(0) == null) {
			return 0L;
		}
		
		long value = results.get(0);
		return value; 
	}
	
	public String getTableName() {
		//通过EntityManager获取factory
		EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
		SessionFactoryImpl sessionFactory = (SessionFactoryImpl)entityManagerFactory.unwrap(SessionFactory.class);
		Map<String, EntityPersister> persisterMap = sessionFactory.getMetamodel().entityPersisters();
		for(Map.Entry<String,EntityPersister> entity : persisterMap.entrySet()){
		    Class<?> targetClass = entity.getValue().getMappedClass();
		    SingleTableEntityPersister persister = (SingleTableEntityPersister)entity.getValue();
		    
		    String entityName = targetClass.getSimpleName();//Entity的名称
		    String tableName = persister.getTableName();//Entity对应的表的英文名
		 
//		    System.out.println("类名：" + entityName + " => 表名：" + tableName);
		    
		    if (targetClass == domainClass) {
		    	return tableName;
		    }
		 
		    //属性
//		    Iterable<AttributeDefinition> attributes = persister.getAttributes();
//		    for(AttributeDefinition attr : attributes){
//		        String propertyName = attr.getName(); //在entity中的属性名称
//		        String[] columnName = persister.getPropertyColumnNames(propertyName); //对应数据库表中的字段名
//		        String type = "";
//		        PropertyDescriptor targetPd = BeanUtils.getPropertyDescriptor(targetClass, propertyName);
//		        if(targetPd != null){
//		            type = targetPd.getPropertyType().getSimpleName();
//		        }
//		        System.out.println("属性名：" + propertyName + " => 类型：" + type + " => 数据库字段名：" + columnName[0]);
//		    }
		}
		throw new ServiceException("非jpa管理的类" + domainClass);
	}
	
	public JdbcTemplate jdbcTemplate() {
		return IocBeanRegister.getBean(JdbcTemplate.class);
	}

}
