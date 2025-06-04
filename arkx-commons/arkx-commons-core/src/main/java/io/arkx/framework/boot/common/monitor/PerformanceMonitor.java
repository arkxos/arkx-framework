package io.arkx.framework.boot.common.monitor;

import org.springframework.stereotype.Service;

import io.arkx.framework.commons.monitor.StackData;
import io.arkx.framework.commons.monitor.StackEntry;

/**
 * 性能监控
 * 
 * @author Darkness
 * @date 2013-7-22 下午04:36:38
 * @version V1.0
 */
@Service
public class PerformanceMonitor {

	private ThreadLocal<StackData> dataHolder = new ThreadLocal<>();

	/**
	 * 性能监控开关 可以在运行时动态设置开关
	 */
	private volatile boolean switchOn = true;

	/**
	 * 方法执行阈值
	 */
	private volatile int threshold = 100;
	
	public boolean isSwitchOn() {
		return switchOn;
	}

	public void start(String logName) {
		StackData data = dataHolder.get();
		StackEntry currentEntry = new StackEntry(logName, System.currentTimeMillis());
		if (data == null) {
			data = new StackData();
			data.root = currentEntry;
			data.level = 1;
			dataHolder.set(data);
		} else {
			StackEntry parent = data.currentEntry;
			currentEntry.parent = parent;
			parent.child.add(currentEntry);
		}
		data.currentEntry = currentEntry;
		currentEntry.level = data.level;
		data.level++;
	}

	/**
	 * @param threshold
	 *            打印日志的阈值
	 */
	public void stop() {
		StackData data = dataHolder.get();
		StackEntry self = data.currentEntry;
		self.end();
		data.currentEntry = self.parent;
		data.level--;
		if (data.root == self && (self.endTime - self.beginTime) > threshold) {
			data.printStack();
		}
		// 是自己,需要结束监控
		if(data.root == self) {
			dataHolder.set(null);
		}
	}

}
