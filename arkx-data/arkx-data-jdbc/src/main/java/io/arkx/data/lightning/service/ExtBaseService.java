package io.arkx.data.lightning.service;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import io.arkx.framework.commons.collection.DataTable;

/**
 * @author Nobody
 * @date 2025-08-27 0:53
 * @since 1.0
 */
public interface ExtBaseService<T, ID> {

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

	// for cache
	Map<ID, T> mgetOneByOne(Collection<ID> ids);

	// for cache
	List<T> findAllOneByOne(Collection<ID> ids);

	void toggleStatus(ID id);

	@SuppressWarnings("unchecked")
	void fakeDelete(ID... ids);

	DataTable queryDataTable(String sql, Object... params);

	List<Map<String, Object>> queryMap(String sql, Object... params);

	List<T> queryList(String sql, Object... params);

	long queryForLong(String sql, Object... params);

}
