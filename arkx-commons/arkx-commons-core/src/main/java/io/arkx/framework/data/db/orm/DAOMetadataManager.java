package io.arkx.framework.data.db.orm;

import io.arkx.framework.commons.collection.ConcurrentMapx;

/**
 * DAO元数据管理器
 *
 */
public class DAOMetadataManager {

    static ConcurrentMapx<String, DAOMetadata> map = new ConcurrentMapx<String, DAOMetadata>();

    @SuppressWarnings("rawtypes")
    public static DAOMetadata getMetadata(Class<? extends DAO> clazz) {
        String className = clazz.getName();
        if (!map.containsKey(className)) {
            DAOMetadata dm = new DAOMetadata(clazz);
            map.put(className, dm);
            return dm;
        }
        return map.get(className);
    }

}
