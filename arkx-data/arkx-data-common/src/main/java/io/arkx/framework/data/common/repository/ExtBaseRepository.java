package io.arkx.framework.data.common.repository;

import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.tree.Treex;
import org.springframework.beans.BeanUtils;
import org.springframework.data.repository.NoRepositoryBean;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>抽象DAO层基类 提供一些简便方法<br/>
 * <p/>
 * 想要使用该接口需要在spring配置文件的jpa:repositories中添加
 * factory-class="org.em.core.repository.support.GenericJpaRepositoryFactoryBean"
 * <p/>
 * <p>泛型 ： M 表示实体类型；ID表示主键类型
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:39
 */
@NoRepositoryBean
public interface ExtBaseRepository<T, ID> {

	<S extends T> S insert(S instance);

	<S extends T> S update(S instance);

	boolean support(String modelType);

	/**
	 * 获取对象属性描述
	 * @param target
	 * @param fieldClass
	 * @return
	 */
	default PropertyDescriptor findFieldPropertyDescriptor(Class<?> target, Class<?> fieldClass) {
		PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(target);
		for (PropertyDescriptor pd : propertyDescriptors) {
			if (pd.getPropertyType() == fieldClass) {
				return pd;
			}
		}
		return null;
	}

	Map<ID, T> mget(Collection<ID> ids);

    //for cache
    Map<ID, T> mgetOneByOne(Collection<ID> ids);

    //for cache
    List<T> findAllOneByOne(Collection<ID> ids);

    void toggleStatus(ID id);

    @SuppressWarnings("unchecked") 
    void fakeDelete(ID... ids);

	DataTable queryDataTable(String sql, Object... params);
	
	List<Map<String, Object>> queryMap(String sql, Object... params);

	List<T> queryList(String sql, Object... params);

	long queryForLong(String sql, Object... params);

	int executeSql(String sql, Object... params);

	Treex<String, T> findAllTree();

	Treex<String, T> queryTreeByParentId(String parentId);

	List<T> findChildrenByParentId(ID parentId);

}
