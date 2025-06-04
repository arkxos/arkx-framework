package org.ark.framework.schedule;

import io.arkx.framework.commons.collection.Mapx;
import com.arkxos.framework.extend.IExtendItem;

/**
 * @class org.ark.framework.schedule.AbstractTaskManager
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:20:58 
 * @version V1.0
 */
public abstract class AbstractTaskManager implements IExtendItem {
	
	public abstract Mapx<String, String> getUsableTasks();

	public abstract Mapx<String, String> getConfigEnableTasks();

	public abstract String getTaskCronExpression(String paramString);

	public abstract void execute(String paramString);

	public abstract boolean isRunning(String paramString);
}