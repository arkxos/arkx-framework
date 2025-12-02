package io.arkx.framework.commons.queueexecutor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.arkx.framework.commons.queueexecutor.processor.ElementProcessor;
import io.arkx.framework.commons.queueexecutor.scheduler.QueueScheduler;
import io.arkx.framework.commons.util.CountableThreadPool;
import io.arkx.framework.commons.util.Handler;

/**
 * @author Darkness
 * @date 2015-1-9 下午3:58:21
 * @version V1.0
 */
public class MultiThreadedQueueExecutor<T> {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private ReentrantLock newElementLock = new ReentrantLock();

	private ReentrantLock startLock = new ReentrantLock();

	private Condition newElementCondition = newElementLock.newCondition();

	private int emptySleepTime = 30000;

	private ElementProcessor<T> elementProcessor;

	private Handler queueExecuteFinishHandler;

	protected CountableThreadPool threadPool;

	private QueueScheduler<T> scheduler;

	private String name;

	private int threadNum;

	private int WaitForStart = 1;

	private int Running = 2;

	private int Stoped = 3;

	private int status = WaitForStart;

	public MultiThreadedQueueExecutor(String name, String type, ElementProcessor<T> elementProcessor) {
		// this.setType(type);
		this.name = name;// "抓取数据 "
		this.elementProcessor = elementProcessor;
		this.scheduler = new QueueScheduler<T>();
	}

	public MultiThreadedQueueExecutor<T> thread(int threadNum) {
		this.threadNum = threadNum;
		threadPool = new CountableThreadPool(threadNum);
		return this;
	}

	public MultiThreadedQueueExecutor<T> onFinish(Handler queueExecuteFinishHandler) {
		this.queueExecuteFinishHandler = queueExecuteFinishHandler;
		return this;
	}

	private void signalNewElement() {
		try {
			newElementLock.lock();
			newElementCondition.signalAll();
		}
		finally {
			newElementLock.unlock();
		}
	}

	protected void processElement(Element<T> element) {
		this.elementProcessor.process(element, this);
	}

	private void waitNewElement() {
		newElementLock.lock();
		try {
			// double check
			if (threadPool.getThreadAlive() == 0) {
				return;
			}
			newElementCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e) {
			logger.warn("waitNewElement - interrupted, error {}", e);
		}
		finally {
			newElementLock.unlock();
		}
	}

	/**
	 * 异步执行，不阻塞
	 */
	public void startAsync() {
		if (status == Running) {
			return;
		}

		startLock.lock();

		if (status == WaitForStart) {
			status = Running;
			runAsync();
		}
		else if (status == Stoped) {
			status = Running;
			this.thread(this.threadNum);
			runAsync();
		}

		startLock.unlock();
	}

	/**
	 * 异步执行，不阻塞
	 */
	public void start() {
		if (status == Running) {
			return;
		}

		startLock.lock();

		if (status == WaitForStart) {
			status = Running;
			execute();
		}
		else if (status == Stoped) {
			status = Running;
			this.thread(this.threadNum);
			execute();
		}

		startLock.unlock();
	}

	private void runAsync() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				execute();
			}
		});
		thread.setDaemon(false);
		thread.start();
	}

	public MultiThreadedQueueExecutor<T> addElement(Element<T> element) {
		if (element != null) {
			this.scheduler.push(element);
		}
		return this;
	}

	public MultiThreadedQueueExecutor<T> addElements(List<Element<T>> elements) {
		if (elements != null) {
			for (Element<T> element : elements) {
				this.scheduler.push(element);
			}
		}
		return this;
	}

	protected int percent;

	protected String currentInfo;

	protected void execute() {
		NumberFormat perFormat = NumberFormat.getPercentInstance(Locale.CHINA);
		DecimalFormat percentFormat = (DecimalFormat) perFormat;
		// 设置Pattern 会使百分比格式，自带格式失效
		// percentFormat.applyPattern("##.00");
		// 设置小数部分 最小位数为2
		percentFormat.setMinimumFractionDigits(2);

		while (!Thread.currentThread().isInterrupted()) {
			Element<T> element = scheduler.poll();
			if (element == null) {
				if (threadPool.getThreadAlive() == 0) {
					break;
				}
				waitNewElement();
			}
			else {
				int total = scheduler.getTotalElementsCount();
				int left = scheduler.getLeftElementsCount();
				int already = total - left;
				this.percent = Double.valueOf((already) * 100.0D / total).intValue();
				this.currentInfo = this.name + " " + already + "/" + total;
				System.out.println(
						"总数：" + scheduler.getTotalElementsCount() + "，剩余：" + scheduler.getLeftElementsCount()
								+ "，剩余百分比："
								+ percentFormat.format(
										(scheduler.getLeftElementsCount() - 0.0) / scheduler.getTotalElementsCount())
								+ "");
				final Element<T> elementFinal = element;
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							processElement(elementFinal);
						}
						catch (Exception e) {
							logger.error("process element " + elementFinal + " error", e);
							e.printStackTrace();
						}
						finally {
							signalNewElement();
						}
					}
				});
			}
		}

		this.percent = 100;

		close();

		logger.debug("multi threaded queen closed");

		if (this.queueExecuteFinishHandler != null) {
			this.queueExecuteFinishHandler.execute();
		}
	}

	public void close() {
		threadPool.shutdown();
		status = Stoped;
	}

	public boolean isShutdown() {
		return threadPool.isShutdown();
	}

}
