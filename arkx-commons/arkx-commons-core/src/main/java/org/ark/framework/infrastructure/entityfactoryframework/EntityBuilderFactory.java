package org.ark.framework.infrastructure.entityfactoryframework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ark.framework.infrastructure.ioc.IocManager;

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;

/**
 * @class org.ark.framework.infrastructure.entityfactoryframework.EntityBuilderFactory
 * @author Darkness
 * @date 2012-9-25 下午7:25:01
 * @version V1.0
 */
public class EntityBuilderFactory {

	// Dictionary used for caching purposes
	private static Map<String, Object> factories = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <T> IEntityFactory<T> buildFactory(Class<T> entityClass) {

		IEntityFactory<T> factory = null;

		// Get the key from the Generic parameter passed in
		String key = entityClass.getName();

		// See if the factory is in the cache
		if (factories.containsKey(key)) {
			// It was there, so retrieve it from the cache
			factory = (IEntityFactory<T>) factories.get(key);
		}
		else {
			// Create the factory

			String type = IocManager.getBeanClass(key);

			if (type == null) {
				return new BaseEntityBuilder<T>(entityClass);
			}

			// Get the type to be created using reflection
			Class<IEntityFactory<T>> entityFactoryType = null;
			try {
				entityFactoryType = (Class<IEntityFactory<T>>) Class.forName(type);
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			// Create the factory using reflection
			try {
				factory = entityFactoryType.newInstance();
			}
			catch (InstantiationException e) {
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			// Put the newly created factory in the cache
			factories.put(key, factory);
		}

		// Return the factory
		return factory;
	}

	public static <T> List<T> buildEntitiesFromSql(Class<T> clazz, String sql) {

		DataTable dataTable = getSession().createQuery(sql).executeDataTable();

		return buildEntitiesFromDataTable(clazz, dataTable);
	}

	public static <T> T buildEntityFromSql(Class<T> clazz, String sql) {

		DataTable dataTable = getSession().createQuery(sql).executeDataTable();
		if (dataTable != null && dataTable.getRowCount() > 0)
			return buildEntityFromDataRow(clazz, dataTable.get(0));
		return null;
	}

	public static <T> T buildEntityFromDataTable(Class<T> clazz, DataTable dataTable) {

		if (dataTable != null && dataTable.getRowCount() > 0)
			return buildEntityFromDataRow(clazz, dataTable.get(0));
		return null;
	}

	protected static <T> T buildEntityFromDataRow(Class<T> clazz, DataRow dataRow) {
		return EntityBuilderFactory.buildFactory(clazz).BuildEntity(dataRow);
	}

	public static <T> List<T> buildEntitiesFromDataTable(Class<T> clazz, DataTable dataTable) {
		List<T> result = new ArrayList<>();

		if (dataTable == null || dataTable.getRowCount() == 0) {
			return result;
		}

		// convert result to entity.
		try {

			for (DataRow dataRow : dataTable.getDataRows()) {

				T t = buildEntityFromDataRow(clazz, dataRow);

				result.add(t);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static Session getSession() {
		return SessionFactory.currentSession();
	}

}
