package io.arkx.framework.util.task;

import io.arkx.framework.util.task.util.TypeReference;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.stream.LongStream;

/**
 * 并发工具类，类似滑动窗口(单步步进)
 * @author www@yiynx.cn
 * @param <T>
 */
public class SlidingWindow<T> {

    private int windowSize; // 窗口大小
    private long dataPacketTotal; // 数据包总数
    private LongFunction<T> producerDataPacketFunction; // 生产数据包函数
    private Consumer<T> consumerDataPacketFunction = t -> {}; // 消费数据包函数，默认空

    public static <T> SlidingWindow<T> create(Class<T> dataClass, int windowSize, long dataPacketTotal) {
        SlidingWindow<T> slidingWindow = new SlidingWindow<>();
        slidingWindow.windowSize = windowSize;
        slidingWindow.dataPacketTotal = dataPacketTotal;
        return slidingWindow;
    }

    public static <T> SlidingWindow<T> create(TypeReference<T> typeReference, int windowSize, long dataPacketTotal) {
        SlidingWindow<T> slidingWindow = new SlidingWindow<>();
        slidingWindow.windowSize = windowSize;
        slidingWindow.dataPacketTotal = dataPacketTotal;
        return slidingWindow;
    }

    public SlidingWindow<T> sendWindow(LongFunction<T> producerDataPacketFunction) {
        this.producerDataPacketFunction = producerDataPacketFunction;
        return this;
    }

    public SlidingWindow<T> receiveWindow(Consumer<T> consumerDataPacketFunction) {
        this.consumerDataPacketFunction = consumerDataPacketFunction;
        return this;
    }

    public void start() {
        if (dataPacketTotal <= 0) {
            return;
        }
        if (dataPacketTotal == 1) {
            consumerDataPacketFunction.accept(producerDataPacketFunction.apply(1));
            return;
        }
        long finalWindowSize = Math.min(windowSize, dataPacketTotal);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            1, (int) finalWindowSize, 10, TimeUnit.SECONDS,
            new SynchronousQueue<>(true), new ThreadPoolExecutor.CallerRunsPolicy());
        try {
            List<CompletableFuture<T>> windowList = new LinkedList<>();
            LongStream.rangeClosed(1, finalWindowSize).forEach(index ->
                windowList.add(CompletableFuture.supplyAsync(() -> producerDataPacketFunction.apply(index), threadPoolExecutor))
            );
            long current = 1;
            do {
                CompletableFuture<T> firstWindow = windowList.remove(0);
                consumerDataPacketFunction.accept(firstWindow.get());
                if (dataPacketTotal - finalWindowSize >= current) {
                    final long index = finalWindowSize + current;
                    windowList.add(CompletableFuture.supplyAsync(() -> producerDataPacketFunction.apply(index), threadPoolExecutor));
                }
            } while (++current <= dataPacketTotal);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPoolExecutor.shutdown();
        }
    }

}
