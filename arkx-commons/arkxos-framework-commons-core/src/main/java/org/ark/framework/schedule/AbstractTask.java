package org.ark.framework.schedule;

import com.arkxos.framework.extend.IExtendItem;

/**
 * @class org.ark.framework.schedule.AbstractTask
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:20:42 
 * @version V1.0
 */
public abstract class AbstractTask implements IExtendItem {
	public abstract String getType();

	public abstract String getCronExpression();
}