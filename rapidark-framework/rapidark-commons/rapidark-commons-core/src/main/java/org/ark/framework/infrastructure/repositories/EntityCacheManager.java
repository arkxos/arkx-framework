package org.ark.framework.infrastructure.repositories;

/**
 * @class org.ark.framework.infrastructure.repositories.EntityCacheManager
 * @author Darkness
 * @date 2012-12-18 下午04:42:17
 * @version V1.0
 */
public class EntityCacheManager {
	
	/**
	 * 检测实体是否启用缓存
	 * 
	 * @author Darkness
	 * @date 2012-12-18 下午04:43:30 
	 * @version V1.0
	 */
	public static boolean isUseCache(Class<?> entityClass) {
		if (entityClass.isAnnotationPresent(Cache.class)) {
			return entityClass.getAnnotation(Cache.class).value();
		}

		return false;
	}
}
