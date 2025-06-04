package io.arkx.framework.commons.monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 方法性能统计实体
 * 
 * @author Darkness
 * @date 2013-7-22 下午07:49:34
 * @version V1.0
 */
public class StackEntry {

	public String logName;
	public long beginTime;
	public long endTime;

	public int level;// 节点所处高度
	public List<StackEntry> child;// 调用的子方法
	public StackEntry parent;// 上级节点

	public StackEntry(String logName, long currentTimeMillis) {
		this.logName = logName;
		this.beginTime = currentTimeMillis;
		this.child = new ArrayList<>(3);
	}

	/**
	 * 方法调用结束
	 * 
	 * @author Darkness
	 * @date 2013-7-22 下午07:57:17 
	 * @version V1.0
	 */
	public void end() {
		endTime = System.currentTimeMillis();
		StackEntityAnalyser.analye(logName, endTime-beginTime);
	}
	
	public long cost() {
		return endTime - beginTime;
	}

}
