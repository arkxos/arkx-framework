package org.ark.framework.orm;

import io.arkx.framework.data.db.dbtype.IDBType;

/**
 * @class org.ark.framework.orm.TableUpdateInfo
 * @author Darkness
 * @date 2012-3-8 下午2:01:37
 * @version V1.0
 */
public abstract class TableUpdateInfo {
	public abstract String[] toSQLArray(IDBType paramString);
}