package io.arkx.framework.data.db.dbtype;

import io.arkx.framework.extend.AbstractExtendService;

/**
 * 数据库类型扩展服务
 * @author Darkness
 * @date 2012-4-5 下午3:26:07
 * @version V1.0
 */
public class DBTypeService extends AbstractExtendService<IDBType> {
	private static DBTypeService instance = null;

	public static DBTypeService getInstance() {
		if (instance == null) {
			instance = findInstance(DBTypeService.class);
		}
		return instance;
	}
}
