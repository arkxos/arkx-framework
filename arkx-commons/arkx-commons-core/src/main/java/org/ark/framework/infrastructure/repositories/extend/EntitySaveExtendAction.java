package org.ark.framework.infrastructure.repositories.extend;

import io.arkx.framework.data.jdbc.Entity;
import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

/**
 * 
 * @author Darkness
 * @date 2013-3-14 下午03:43:48
 * @version V1.0
 */
public abstract class EntitySaveExtendAction implements IExtendAction {
	
	public static String ExtendPointID = "org.ark.framework.EntitySave";

	@Override
	public Object execute(Object[] params) throws ExtendException {
		save((Entity) params[0]);
		return null;
	}

	protected abstract void save(Entity entity);
	
	@Override
	public boolean isUsable() {
		return true;
	}
}
