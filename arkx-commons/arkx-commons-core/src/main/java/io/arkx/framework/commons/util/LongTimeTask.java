package io.arkx.framework.commons.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Darkness
 * @date 2014-12-26 上午9:37:33
 * @version V1.0
 */
public abstract class LongTimeTask extends Thread {
	
	private static Map<Long, LongTimeTask> map = new HashMap<>();

	private static long IDBase = System.currentTimeMillis();
	private static final int MaxListSize = 1000;
	
	public static LongTimeTask createEmptyInstance() {
		return new LongTimeTask(false) {
			public void execute() {
			}
		};
	}

	public static Map<Long, LongTimeTask> getAllTasks() {
		clearStopedTask();
		return map;
	}
	
	public static LongTimeTask getInstanceById(long id) {
		return map.get(id);
	}

	public static void removeInstanceById(long id) {
		synchronized (LongTimeTask.class) {
			map.remove(id);
		}
	}

	public static String cancelByType(String type) {
		String message = "该任务不存在:" + type;
		LongTimeTask ltt = getInstanceByType(type);
		if (ltt != null) {
			ltt.stopTask();
			message = "任务终止中,请稍等片刻！";
		}
		return message;
	}

	public static LongTimeTask getInstanceByType(String type) {
		if (StringUtil.isNotEmpty(type)) {
			long current = System.currentTimeMillis();
			for (Long key : map.keySet()) {
				LongTimeTask ltt = map.get(key);
				if (type.equals(ltt.getType())) {
					if (current - ltt.stopTime > 60000L) {
						map.remove(key);
						return null;
					}
					return ltt;
				}
			}
		}
		return null;
	}
	
	private long id;
	private ArrayList<String> list = new ArrayList<>();
	protected int percent;
	protected String currentInfo;
	private String finishedInfo;
	protected ArrayList<String> errors = new ArrayList<>();
	private boolean stopFlag;
	private String type;
	private long stopTime = System.currentTimeMillis() + (24*60*1000L);

	public LongTimeTask() {
		this(true);
	}

	private LongTimeTask(boolean flag) {
		if (flag) {
			setName("LongTimeTask Thread");
			synchronized (LongTimeTask.class) {
				this.id = (IDBase++);
				map.put(this.id, this);
				clearStopedTask();
			}
		}
	}

	private static void clearStopedTask() {
		synchronized (LongTimeTask.class) {
			long current = System.currentTimeMillis();
			Map<Long, LongTimeTask> tempMap = new HashMap<>(map);
			for (Long k : tempMap.keySet()) {
				LongTimeTask ltt = tempMap.get(k);
				if (current - ltt.stopTime > 5000L) {
					map.remove(k);
				}
			}
		}
	}

	public long getTaskID() {
		return this.id;
	}

	public void info(String message) {

		this.list.add(message);
		if (this.list.size() > MaxListSize)
			this.list.remove(0);
	}

	public String[] getMessages() {
		String[] arr = new String[this.list.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = this.list.get(i);
		}
		this.list.clear();
		return arr;
	}

	@Override
	public void run() {
		if (StringUtil.isNotEmpty(this.type)) {
			LongTimeTask ltt = getInstanceByType(this.type);
			if ((ltt != null) && (ltt != this)) {
				return;
			}
		}
		try {
			execute();
		} catch (Exception ie) {
			interrupt();
			addError(ie.getMessage());
		} finally {
			this.stopTime = System.currentTimeMillis();
		}
	}

	protected abstract void execute();

	public boolean checkStop() {
		return this.stopFlag;
	}

	public void stopTask() {
		clearStopedTask();
		this.stopFlag = true;
	}

	public int getPercent() {
		return this.percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

	public void setCurrentInfo(String currentInfo) {
		this.currentInfo = currentInfo;
	}

	public String getCurrentInfo() {
		return this.currentInfo;
	}

	public void setFinishedInfo(String finishedInfo) {
		this.finishedInfo = finishedInfo;
	}

	public String getFinishedInfo() {
		return this.finishedInfo;
	}

	public void addError(String error) {
		this.errors.add(error);
	}

	public void addError(String[] errorArr) {
		for (int i = 0; i < errorArr.length; i++)
			this.errors.add(errorArr[i]);
	}

	public String getAllErrors() {
		if (this.errors.isEmpty()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		StringFormat sf = new StringFormat("总计 ? 个错误", this.errors.size());
		sb.append(sf.toString() + ":<br>");
		for (int i = 0; i < this.errors.size(); i++) {
			sb.append(i + 1);
			sb.append(": ");
			sb.append(this.errors.get(i));
			sb.append("<br>");
		}
		return sb.toString();
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

