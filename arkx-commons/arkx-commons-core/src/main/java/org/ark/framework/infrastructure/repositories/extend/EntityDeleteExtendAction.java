package org.ark.framework.infrastructure.repositories.extend;

import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

/**
 * @class org.ark.framework.infrastructure.repositories.extend.EntityDeleteExtendAction
 * @author Darkness
 * @date 2013-1-22 下午10:17:59
 * @version V1.0
 */
public abstract class EntityDeleteExtendAction implements IExtendAction {

	public static String ExtendPointID = "org.ark.framework.EntityDelete";

	@Override
	public Object execute(Object[] params) throws ExtendException {
		return afterEntityDelete((String) params[0], (String) params[1]);
	}

	/**
	 * 实体删除后操作
	 *
	 * @author Darkness
	 * @date 2013-1-22 下午10:21:17
	 * @version V1.0
	 */
	public abstract boolean afterEntityDelete(String tableName, String ids);

	@Override
	public boolean isUsable() {
		// TODO Auto-generated method stub
		return false;
	}

}
