package io.arkx.framework.commons.lang;

import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Darkness
 * @date 2016年1月3日 下午10:33:16
 * @version V1.0
 * @since infinity 1.0
 */
public class PerformanceMonitorInterceptor implements MethodInterceptor {

	Logger logger = LoggerFactory.getLogger(PerformanceMonitorInterceptor.class.getName());

	private static ConcurrentHashMap<String, MethodStats> methodStats = new ConcurrentHashMap<>();

	private static long statLogFrequency = 10;

	private static long methodWarningThreshold = 1000;

	private void updateStats(String methodName, long elapsedTime) {
		MethodStats stats = methodStats.get(methodName);
		if (stats == null) {
			stats = new MethodStats(methodName);
			methodStats.put(methodName, stats);
		}
		stats.count++;
		stats.totalTime += elapsedTime;
		if (elapsedTime > stats.maxTime) {
			stats.maxTime = elapsedTime;
		}

		if (elapsedTime > methodWarningThreshold) {
			logger.warn("method warning: " + methodName + "(), cnt = " + stats.count + ", lastTime = " + elapsedTime
					+ ", maxTime = " + stats.maxTime);
		}

		if (stats.count % statLogFrequency == 0) {
			long avgTime = stats.totalTime / stats.count;
			long runningAvg = (stats.totalTime - stats.lastTotalTime) / statLogFrequency;
			logger.debug("method: " + methodName + "(), cnt = " + stats.count + ", lastTime = " + elapsedTime
					+ ", avgTime = " + avgTime + ", runningAvg = " + runningAvg + ", maxTime = " + stats.maxTime);

			// reset the last total time
			stats.lastTotalTime = stats.totalTime;
		}
	}

	class MethodStats {

		public String methodName;

		public long count;

		public long totalTime;

		public long lastTotalTime;

		public long maxTime;

		public MethodStats(String methodName) {
			this.methodName = methodName;
		}

	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			return invocation.proceed();
		}
		finally {
			updateStats(invocation.getClass().getName() + "." + invocation.getMethod().getName(),
					(System.currentTimeMillis() - start));
		}
	}

}
