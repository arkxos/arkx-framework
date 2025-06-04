package org.ark.framework.infrastructure.repositories.extend;

import com.arkxos.framework.data.jdbc.Entity;
import com.arkxos.framework.extend.ExtendException;
import com.arkxos.framework.extend.IExtendAction;

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
