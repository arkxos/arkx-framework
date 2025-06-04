package org.ark.framework.infrastructure.repositories;

import java.util.List;

import org.ark.framework.infrastructure.entityfactoryframework.EntityBuilderFactory;

import io.arkx.framework.annotation.EntityAnnotationManager;
import com.arkxos.framework.cache.CacheDataProvider;
import com.arkxos.framework.cache.CacheManager;
import com.arkxos.framework.commons.collection.CacheMapx;
import com.arkxos.framework.data.jdbc.Entity;


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
