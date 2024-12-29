package com.arkxos.framework.cache;

import com.arkxos.framework.schedule.SystemTask;

/**
 * 缓存事件同步任务,每秒执行一次
 */
public class CacheSyncTask extends SystemTask {
	
	public static final String ID = "com.rapidark.framework.cache.ClusteringSyncTask";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "@{Platform.ClusteringSyncTask}";
	}

	@Override
	public void execute() {
		//CacheSyncUtil.sync();
	}

	@Override
	public String getDefaultCronExpression() {
		return "* * * * * *";
	}

	@Override
	public boolean isDisabled() {
		return !CacheSyncUtil.enabled();
	}

	@Override
	public boolean enable4Front() {
		return true;
	}

}