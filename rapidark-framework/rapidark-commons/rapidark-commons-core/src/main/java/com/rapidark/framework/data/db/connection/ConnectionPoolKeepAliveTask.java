package com.rapidark.framework.data.db.connection;

import com.rapidark.framework.schedule.SystemTask;

/**
 * 数据库连接保持活动任务,每三分钟执行一次
 * 
 */
public class ConnectionPoolKeepAliveTask extends SystemTask {
	public static final String ID = "com.rapidark.framework.data.db.connection.ConnectionPoolKeepAliveTask";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "@{Platform.DBConnPoolKeepAliveTask}";
	}

	@Override
	public void execute() {
		if (ConnectionPoolManager.isEmpty()) {
			return;
		}
		for (ConnectionPool pool : ConnectionPoolManager.pools()) {
			pool.keepAlive();
		}
	}

	@Override
	public String getDefaultCronExpression() {
		return "*/3 * * * *";
	}
}