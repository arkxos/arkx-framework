package io.arkx.framework.performance.monitor.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Nobody
 * @date 2025-06-12 12:35
 * @since 1.0
 */
public class SummaryTraceNode extends TraceNode {

	String logName;

	long cost;

	int times = 1;

	int level;

	Map<String, SummaryTraceNode> mergedChild = new LinkedHashMap<>();

	public SummaryTraceNode(TraceNode entry) {
		this.logName = entry.getName();
		this.level = entry.getDepth();
		this.cost = entry.getDuration();
		init(entry);
	}

	public void init(TraceNode entry) {
		for (TraceNode childEntry : entry.getChildren()) {
			String logName = childEntry.getName();
			SummaryTraceNode existInfo = mergedChild.get(logName);
			SummaryTraceNode currentInfo = new SummaryTraceNode(childEntry);
			if (existInfo == null) {
				mergedChild.put(logName, currentInfo);
			}
			else {
				existInfo.merge(currentInfo);
			}
		}

	}

	private void merge(SummaryTraceNode otherEntry) {
		this.times += otherEntry.times;
		this.cost += otherEntry.cost;

		for (String key : otherEntry.mergedChild.keySet()) {
			SummaryTraceNode other = otherEntry.mergedChild.get(key);
			SummaryTraceNode exist = this.mergedChild.get(key);
			if (exist == null) {
				this.mergedChild.put(key, other);
			}
			else {
				exist.merge(other);
			}
		}
	}

}
