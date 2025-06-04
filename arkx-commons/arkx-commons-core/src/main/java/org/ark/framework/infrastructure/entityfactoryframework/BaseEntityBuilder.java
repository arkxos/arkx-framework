package org.ark.framework.infrastructure.entityfactoryframework;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.arkx.framework.annotation.Column;
import io.arkx.framework.annotation.Ingore;
import com.arkxos.framework.commons.collection.DataRow;
import com.arkxos.framework.commons.util.lang.ReflectionUtil;


/**
 * @class org.ark.framework.infrastructure.entityfactoryframework.BaseEntityBuilder
 * @author Darkness
 * @date 2012-10-27 下午02:42:58
 * @version V1.0
 */
public class BaseEntityBuilder<T > implements IEntityFactory<T> {

	private Class<T> genericClass;

	public BaseEntityBuilder(Class<T> genericClass) {
		this.genericClass = genericClass;
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getGenericClass() {

		if (genericClass == null) {
			Type type = getClass().getGenericSuperclass();
			Type trueType = ((ParameterizedType) type).getActualTypeArguments()[0];
			genericClass = (Class<T>) trueType;
		}
		return genericClass;
	}

	@Override
	public T BuildEntity(DataRow dataRow) {

		// convert result to entity.
		T t = null;
		try {
			t = getGenericClass().newInstance();

			Field[] fields = ReflectionUtil.getDeclaredFields(getGenericClass());

			for (Field f : fields) {
				
				Ingore ingore = f.getAnnotation(Ingore.class);
				if(ingore != null) {
					continue;
				}
				
				String columnName = f.getName();
				
				Column column = f.getAnnotation(Column.class);
				if(column != null) {
					columnName = column.name();
				}
				
				ReflectionUtil.setFieldValue(t, f.getName(), dataRow.get(columnName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return t;
	}

}
