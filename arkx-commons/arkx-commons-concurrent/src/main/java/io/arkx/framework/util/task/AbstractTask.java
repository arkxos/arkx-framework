package io.arkx.framework.util.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import io.arkx.framework.util.task.callback.Progress;
import io.arkx.framework.util.task.callback.TaskCompletedListener;
import io.arkx.framework.util.task.callback.TaskListener;
import io.arkx.framework.util.task.exception.TaskException;
import io.arkx.framework.util.task.util.Assert;
import io.arkx.framework.util.task.util.Utils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class AbstractTask implements Task {

	private String id;// 任务的 ID

	@Setter
	private String type;// 任务的类型

	private Progress progress;// 进度回调函数

	private double progressPercent;

	@Getter
	private List<TaskListener> taskListeners = new ArrayList<>();

	private boolean hasTriggerCompleted = false;

	private List<TaskCompletedListener> taskCompletedListeners = new ArrayList<>();

	private final AtomicReference<TaskStatus> statusReference = new AtomicReference<>(TaskStatus.INIT);// 任务的状态

	protected Future<?> future;

	private final long createTime;

	private int globalExecuteOrder;

	private boolean waittingForExecute = true;

	private boolean finished = false;

	private long startTime;

	private long endTime;

	protected AbstractTask(String type, String id) {
		if (Utils.isEmpty(type)) {
			this.type = Task.DEFAULT_TYPE_NAME;
		}
		else {
			this.type = type;
		}
		if (Utils.isEmpty(id)) {
			this.id = Utils.generateId();
		}
		else {
			this.id = id;
		}
		createTime = System.currentTimeMillis();
	}

	public final boolean setStatus(TaskStatus expect, TaskStatus update) {
		Assert.notNull(statusReference);

		boolean result = this.statusReference.compareAndSet(expect, update);

		if (update != TaskStatus.INIT) {
			this.waittingForExecute = false;
		}

		return result;
	}

	private void setStatusToCancel() {
		this.statusReference.set(TaskStatus.CANCEL);
	}

	@Override
	public TaskStatus getStatus() {
		return this.statusReference.get();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (future == null) {
			setStatusToCancel();
			return true;
		}
		if (future.cancel(mayInterruptIfRunning)) {
			setStatusToCancel();
			return true;
		}
		return false;
	}

	@Override
	public void await() {
		if (future != null) {
			try {
				future.get();
			}
			catch (InterruptedException | ExecutionException e) {
				throw new TaskException(e);
			}
		}
	}

	@Override
	public void await(long timeout, TimeUnit unit) throws TimeoutException {
		if (future != null) {
			try {
				future.get(timeout, unit);
			}
			catch (InterruptedException | ExecutionException e) {
				throw new TaskException(e);
			}
		}
	}

	public void addListener(TaskListener listener) {
		taskListeners.add(listener);
	}

	public void addCompletedListener(TaskCompletedListener taskCompletedListener) {
		this.taskCompletedListeners.add(taskCompletedListener);
	}

	@Override
	public boolean isFinished() {
		// TaskStatus status = getStatus();
		// return status != TaskStatus.INIT
		// && status != TaskStatus.QUEUED
		// && status != TaskStatus.RUNNING;
		return this.finished;
	}

	@Override
	public void triggerCompleted() {
		if (!isFinished()) {
			return;
		}
		if (!hasTriggerCompleted) {
			hasTriggerCompleted = true;
			for (TaskCompletedListener completedListener : taskCompletedListeners) {
				completedListener.onCompleteFinish();
			}
		}

	}

	public String getCost() {
		if (endTime == 0) {
			return "";
		}
		return formatTime(endTime - startTime);
	}

	/*
	 * 毫秒转化时分秒毫秒
	 */
	public static String formatTime(long ms) {
		// Integer namiao = 1000;
		long weimiaoUnit = 1000;
		long haomiaoUnit = weimiaoUnit * 1000;
		long secondUnit = haomiaoUnit * 1000;
		long minuteUnit = secondUnit * 60;
		long hourUnit = minuteUnit * 60;
		long dayUnit = hourUnit * 24;

		Long day = ms / dayUnit;
		Long hour = (ms - day * dayUnit) / hourUnit;
		Long minute = (ms - day * dayUnit - hour * hourUnit) / minuteUnit;
		Long second = (ms - day * dayUnit - hour * hourUnit - minute * minuteUnit) / secondUnit;
		Long haomiao = (ms - day * dayUnit - hour * hourUnit - minute * minuteUnit - second * secondUnit) / haomiaoUnit;
		Long weimiao = (ms - day * dayUnit - hour * hourUnit - minute * minuteUnit - second * secondUnit
				- haomiao * haomiaoUnit) / weimiaoUnit;
		Long namiao = ms - day * dayUnit - hour * hourUnit - minute * minuteUnit - second * secondUnit
				- haomiao * haomiaoUnit - weimiao * weimiaoUnit;

		StringBuffer sb = new StringBuffer();
		if (day > 0) {
			sb.append(day + "天");
		}
		if (hour > 0) {
			sb.append(hour + "小时");
		}
		if (minute > 0) {
			sb.append(minute + "分");
		}
		if (second > 0) {
			sb.append(second + "秒");
		}
		if (haomiao > 0) {
			sb.append(haomiao + "毫秒");
		}
		if (weimiao > 0) {
			sb.append(weimiao + "微秒");
		}
		if (namiao > 0) {
			sb.append(namiao + "纳秒");
		}

		String result = sb.toString();
		if (result.length() == 0) {
			result = "0纳秒";
		}
		return result;
	}

	public void finish() {
		this.setEndTime(System.nanoTime());
		this.finished = true;
	}

}
