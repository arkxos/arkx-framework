package io.arkx.framework.commons.task;

import com.alibaba.fastjson.JSON;
import io.arkx.framework.commons.queueexecutor.QueueInfo;
import io.arkx.framework.commons.queueexecutor.TaskEnginer;
import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.SystemInfo;
import io.arkx.framework.commons.util.TimeWatch;

import java.io.File;

/**
 * @author Darkness
 * @date 2017年6月15日 下午5:08:31
 * @version 1.0
 * @since 1.0 
 */
public class TaskEnginerTest {

	public static void main(String[] args) {
		
		String taskEnginerDataPath = SystemInfo.userHome() + File.separator + "temp" + File.separator + "task-enginer-data";
		TaskEnginer taskEnginer = new TaskEnginer(taskEnginerDataPath);
		taskEnginer.start();
		
		if(!taskEnginer.containsTask(ReadFileTaskProcessor.ID+"_01")) {
//			taskEnginer.deleteTask(ReadFileTaskModel.ID);
			taskEnginer.addTask(new ReadFileTaskProcessor("_01"));
		}
		
		taskEnginer.onFinished(() -> {
			System.out.println("execute finished");
		});
		
		taskEnginer.run();
		
		TimeWatch timeWatch = TimeWatch.create();
		timeWatch.startWithTaskName("dd");
		String text = FileUtil.readText("/Users/darkness/ice-queen3-data/task-enginer-data/download-raw-eod-quote__xiuqiu-2016-4__snapshot_2016-11-18.json");
		timeWatch.stopAndPrint();
		timeWatch.startWithTaskName("json");
		QueueInfo task = JSON.parseObject(text, QueueInfo.class);
		timeWatch.stopAndPrint();
		System.out.println(task);
	}
}
