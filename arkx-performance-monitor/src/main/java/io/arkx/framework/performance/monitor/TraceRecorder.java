package io.arkx.framework.performance.monitor;

import io.arkx.framework.performance.monitor.config.MonitorConfig;
import io.arkx.framework.performance.monitor.model.TraceNode;
import io.arkx.framework.performance.monitor.repository.TraceRepository;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Nobody
 * @date 2025-06-06 0:42
 * @since 1.0
 */
/* ====================== 高性能跟踪记录器 ====================== */
@Slf4j
@Component
public  class TraceRecorder {

	// 性能参数
	private static final int QUEUE_CAPACITY = 100_000;
	private static final int MIN_BATCH_SIZE = 10;

	// 运行时状态
	private final BlockingQueue<TraceNode> queue;
	private volatile boolean running = true;
	private final ExecutorService writerThread = Executors.newSingleThreadExecutor();

	// 依赖组件
	private final MonitorConfig config;
	private final TraceRepository repository;

	public TraceRecorder(MonitorConfig config, TraceRepository repository) {
		this.config = config;
		this.queue =  new LinkedBlockingQueue<>(Math.max(config.getQueueCapacity(), 10_000));
		this.repository = repository;
		this.writerThread.execute(this::runBatchWriter);
	}

	// 获取队列当前大小
	public int queueSize() {
		return queue.size();
	}

	// 获取队列容量
	public int getQueueCapacity() {
		return queue.remainingCapacity() + queue.size();
	}

	// 记录跟踪节点（非阻塞）
	public void record(TraceNode node) {
		if (!queue.offer(node)) {
			log.debug("Trace recorder queue overflow, node dropped");
		}
	}

	// 核心写逻辑
	private void runBatchWriter() {
		List<TraceNode> batch = new ArrayList<>(config.getWriteBatchSize());

		while (running || !queue.isEmpty()) {
			try {
				// 获取第一个节点
				TraceNode node = queue.poll(100, TimeUnit.MILLISECONDS);
				if (node == null) continue;

				batch.add(node);

				// 批量获取更多节点
				queue.drainTo(batch, config.getWriteBatchSize() - 1);

				// 时间或数量触发写入
				processBatch(batch);
				batch.clear();

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				log.error("Error in trace writer thread", e);
			}
		}

		// 最终写入剩余节点
		if (!batch.isEmpty()) {
			processBatch(batch);
		}
	}

	// 处理批次写入
	private void processBatch(List<TraceNode> batch) {
		long startTime = System.currentTimeMillis();

		// 过滤掉无效节点
		List<TraceNode> validNodes = batch.stream()
				.filter(n -> n != null && n.getTraceId() != null)
				.collect(Collectors.toList());

		if (validNodes.isEmpty()) return;

		try {
			// 批量存储
			if (config.isStoreToDatabase()) {
				repository.saveBatch(validNodes);
			}
		} catch (Exception e) {
			log.error("Failed to save batch of {} trace nodes", validNodes.size(), e);
		}

		long processingTime = System.currentTimeMillis() - startTime;
//		systemMonitor.onQueueItemProcessed(queue.size(), processingTime);
	}

	// 关闭清理
	@PreDestroy
	public void shutdown() {
		running = false;
		writerThread.shutdown();
		try {
			if (!writerThread.awaitTermination(5, TimeUnit.SECONDS)) {
				writerThread.shutdownNow();
			}
		} catch (InterruptedException e) {
			writerThread.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}
