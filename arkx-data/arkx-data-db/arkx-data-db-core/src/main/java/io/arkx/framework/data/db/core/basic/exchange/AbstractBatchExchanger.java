// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.core.basic.exchange;

import org.springframework.core.task.AsyncTaskExecutor;

import io.arkx.framework.data.db.common.util.ExamineUtils;
import io.arkx.framework.data.db.core.basic.robot.RobotReader;
import io.arkx.framework.data.db.core.basic.robot.RobotWriter;

public abstract class AbstractBatchExchanger {

	private MemChannel memChannel;

	private AsyncTaskExecutor readThreadExecutor;

	private AsyncTaskExecutor writeThreadExecutor;

	public AbstractBatchExchanger(AsyncTaskExecutor readExecutor, AsyncTaskExecutor writeExecutor, int channelMaxSize) {
		ExamineUtils.checkNotNull(readExecutor, "readExecutor");
		ExamineUtils.checkNotNull(writeExecutor, "writeExecutor");
		this.memChannel = MemChannel.createNewChannel(channelMaxSize);
		this.readThreadExecutor = readExecutor;
		this.writeThreadExecutor = writeExecutor;
	}

	public MemChannel getMemChannel() {
		return memChannel;
	}

	public int getChannelWaitingNum() {
		return memChannel.size();
	}

	public void exchange(RobotReader reader, RobotWriter writer) {
		// 为reader和writer配置数据传输隧道
		reader.setChannel(this.memChannel);
		writer.setChannel(this.memChannel);

		// 初始化reader和writer
		reader.init(readThreadExecutor);
		writer.init(writeThreadExecutor);

		// 启动reader和writer的并行工作
		writer.startWork();
		reader.startWork();

		// writer会等待reader执行完
		writer.waitForFinish();

		// 收集统计信息
		Throwable throwable = collectPerfStats(reader, writer);
		if (null != throwable) {
			if (throwable instanceof RuntimeException exception) {
				throw exception;
			}
			throw new RuntimeException(throwable);
		}
	}

	protected abstract Throwable collectPerfStats(RobotReader reader, RobotWriter writer);

}
