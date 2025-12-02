package org.ark.framework.infrastructure.entityfactoryframework;

import io.arkx.framework.commons.collection.DataRow;

/**
 * @class org.ark.framework.infrastructure.entityfactoryframework.IEntityFactory
 * @author Darkness
 * @date 2012-9-25 下午7:23:17
 * @version V1.0
 */
public interface IEntityFactory<T> {

	T BuildEntity(DataRow dataRow);

}
