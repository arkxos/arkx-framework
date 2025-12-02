package io.arkx.framework.util.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.arkx.framework.util.task.util.RandomUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlidingWindowTest {

	@Test
	public void test() throws InterruptedException, ExecutionException {
		AtomicLong consumerIndex = new AtomicLong(); // 消费计数
		SlidingWindow.create(Long.class, 2, 10).sendWindow(index -> {
			try {
				Thread.sleep(RandomUtil.randomInt(100)); // 模拟发送耗时
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.error("中断异常！", e);
			}
			log.info("发送:{}", index);
			return index;
		}).receiveWindow(result -> {
			Assertions.assertEquals(consumerIndex.incrementAndGet(), result, "消费结果顺序错误！");
			try {
				Thread.sleep(RandomUtil.randomInt(100)); // 模拟接收耗时
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.error("中断异常！", e);
			}
			log.info("接收:{}", result);
		}).start();
	}

}
