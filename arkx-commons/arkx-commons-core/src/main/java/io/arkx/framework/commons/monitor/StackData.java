package io.arkx.framework.commons.monitor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.arkx.framework.commons.util.TimeWatch;

/**
 * 监控信息变量
 * 
 * @author Darkness
 * @date 2013-7-22 下午07:51:03
 * @version V1.0
 */
public class StackData {
	
	private Logger logger = LoggerFactory.getLogger(StackData.class);

	// 记录根根节点
	public StackEntry root;
	// 当前正在调用方法节点
	public StackEntry currentEntry;
	// 堆栈树高度
	public int level;
	
	/**
	 * 此处还可以进行改进，可以将超时的数据放入一个有界队列 里，在另一个线程进行打印。
	 * 
	 * @param data
	 */
	public void printStack() {
		if (logger.isWarnEnabled()) {
			StringBuilder sb = new StringBuilder("\r\n");
			StackEntry root = this.root;
			mergeNode(root, sb);
			logger.warn(sb.toString());
		}
	}

	/**
	 * |-A
	 * +---B
	 * +---+---B1
	 * +---+---B1
	 * +---+---B2
	 * +---B
	 * +---+---B1
	 * +---+---B1
	 * +---+---B2
	 *  
	 * @author Darkness
	 * @date 2016年1月12日 下午10:21:27
	 * @version V1.0
	 * @since infinity 1.0
	 */
	private void appendNode(MergedStackEntry entry, StringBuilder sb) {
		long totalTime = entry.cost;
		if (entry.level == 1) {
			sb.append("|-");
		}
		sb.append(TimeWatch.formatTime(totalTime));
		sb.append("; times: "+entry.times+",[");
		sb.append(entry.logName);
		sb.append("]");

		for (MergedStackEntry cnode : entry.mergedChild.values()) {
			sb.append("\r\n|");
			for (int i = 0, l = entry.level; i < l; i++) {
				sb.append("+---");
			}
			appendNode(cnode, sb);
		}

	}
	
	private void mergeNode(StackEntry entry,StringBuilder sb) {
		MergedStackEntry mergedStackEntry = new MergedStackEntry(entry);
		
		appendNode(mergedStackEntry, sb);
	}

}

class MergedStackEntry {
	String logName;
	long cost;
	int times=1;
	int level;
	Map<String, MergedStackEntry> mergedChild = new LinkedHashMap<>();

	public MergedStackEntry(StackEntry entry) {
		this.logName = entry.logName;
		this.level = entry.level;
		this.cost = entry.cost();
		init(entry);
	}
	
	public void init(StackEntry entry) {
		for (StackEntry childEntry : entry.child) {
			String logName = childEntry.logName;
			MergedStackEntry existInfo = mergedChild.get(logName);
			MergedStackEntry currentInfo = new MergedStackEntry(childEntry);
			if (existInfo == null) {
				mergedChild.put(logName, currentInfo);
			} else {
				existInfo.merge(currentInfo);
			}
		}
		
	}

	private void merge(MergedStackEntry otherEntry) {
		this.times += otherEntry.times;
		this.cost += otherEntry.cost;
		
		for (String key : otherEntry.mergedChild.keySet()) {
			MergedStackEntry other = otherEntry.mergedChild.get(key);
			MergedStackEntry exist = this.mergedChild.get(key);
			if(exist == null) {
				this.mergedChild.put(key, other);
			} else {
				exist.merge(other);
			}
		}
	}
}
