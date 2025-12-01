package org.ark.framework.infrastructure.repositories;

import io.arkx.framework.annotation.EntityAnnotationManager;
import io.arkx.framework.cache.CacheDataProvider;
import io.arkx.framework.cache.CacheManager;
import io.arkx.framework.commons.collection.CacheMapx;
import io.arkx.framework.data.jdbc.Entity;
import org.ark.framework.infrastructure.entityfactoryframework.EntityBuilderFactory;

import java.util.List;


/**   
 * @class org.ark.framework.infrastructure.repositories.OrmEntityCache
 * @author Darkness
 * @date 2012-12-18 下午03:40:09 
 * @version V1.0   
 */
public class OrmEntityCache extends CacheDataProvider {

	public static final String ProviderID = "OrmEntity";
	
	@Override
	public String getExtendItemID() {
		return ProviderID;
	}

	@Override
	public String getExtendItemName() {
		return "Orm 缓存";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onTypeNotFound(String type) {
		CacheManager.setMapx(ProviderID, type, new CacheMapx<String, Object>(10000));
		
		Class<? extends Entity> entityClass = null;
		try {
			entityClass = (Class<? extends Entity>)Class.forName(type);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		String tableName = EntityAnnotationManager.getTableName(entityClass);
		String sql = "SELECT * FROM " + tableName;
		List<? extends Entity> entities = EntityBuilderFactory.buildEntitiesFromSql(entityClass, sql);
		for (Entity entity : entities) {
			CacheManager.set(ProviderID, type, entity.getId(), entity);
		}
		
	}

	@Override
	public void onKeyNotFound(String type, String key) {
	}

}
