package io.arkx.framework.performance.monitor2.domain;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.arkx.framework.boot.spring.IocBeanRegister;
import io.arkx.framework.performance.monitor2.LogMethodPerformanceRepository;
import io.arkx.framework.performance.monitor2.model.LogMethodPerformance;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Darkness
 * @date 2013-7-22 下午07:42:46
 * @version V1.0
 */
@Slf4j
public class StackEntityAnalyser {

	private static ConcurrentHashMap<String, MethodStats> methodStats = new ConcurrentHashMap<String, MethodStats>();
	private static long statLogFrequency = 10;
	private static long methodWarningThreshold = 1000;

	public static void analye(String methodName, long elapsedTime) {
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
			log.warn("method warning: " + methodName + "(), cnt = " + stats.count + ", lastTime = " + elapsedTime + ", maxTime = " + stats.maxTime);
		}

		if (stats.count % statLogFrequency == 0) {
			long avgTime = stats.totalTime / stats.count;
			long runningAvg = (stats.totalTime - stats.lastTotalTime) / statLogFrequency;
			log.info("====性能监控===>method: " + methodName + "(), cnt = " + stats.count + " ms, lastTime = " + elapsedTime + " ms, avgTime = " + avgTime
					+ " ms, runningAvg = " + runningAvg + " ms, maxTime = " + stats.maxTime + " ms");

			// reset the last total time
			stats.lastTotalTime = stats.totalTime;
			
			LogMethodPerformanceRepository logMethodPerformanceRepository = IocBeanRegister.getBean("logMethodPerformanceRepository");
			LogMethodPerformance logMethodPerformance = new LogMethodPerformance();
			logMethodPerformance.setAvgTime(runningAvg);
			
			if(methodName.startsWith("com")) {
				int index = methodName.lastIndexOf(".");
				String className = methodName.substring(0, index);
				String methodNames = methodName.substring(index+1);
				logMethodPerformance.setClassName(className);
				logMethodPerformance.setMethodName(methodNames);
			} else {
				logMethodPerformance.setClassName("resource");
				logMethodPerformance.setMethodName(methodName);
			}

			Optional<LogMethodPerformance> oldLogMethodPerformanceOptional =
            	logMethodPerformanceRepository
                    .findOneByClassNameAndMethodName(
						logMethodPerformance.getClassName(),
						logMethodPerformance.getMethodName());

			if (oldLogMethodPerformanceOptional.isPresent()) {
				LogMethodPerformance oldLogMethodPerformance = oldLogMethodPerformanceOptional.get();
				oldLogMethodPerformance.setAvgTime(logMethodPerformance.getAvgTime());
				logMethodPerformanceRepository.save(oldLogMethodPerformance);
			} else {
				logMethodPerformanceRepository.save(logMethodPerformance);
			}
		}
	}

	private static class MethodStats {
		@SuppressWarnings("unused")
		public String methodName;
		public long count;
		public long totalTime;
		public long lastTotalTime;
		public long maxTime;

		public MethodStats(String methodName) {
			this.methodName = methodName;
		}
	}
}
