package io.arkx.framework.message.tcp;

/**
 * @author Darkness
 * @date 2017年5月26日 下午4:43:44
 * @version 1.0
 * @since 1.0
 */

import java.util.concurrent.*;

public class BlockingQueueTest {

    // 声明一个容量为10的缓存队列
    BlockingQueue<String> queue = new LinkedBlockingQueue<String>(1024);

    public void start() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        // 启动线程
        service.execute(new Consumer(queue));
    }

    public void addCommand(String data) {
        try {
            queue.offer(data, 2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        // Producer producer1 = new Producer(queue);
        // Consumer consumer = new Consumer(queue);

        // 借助Executors

        // service.execute(consumer);
        //
        // // 执行10s
        // Thread.sleep(10 * 1000);
        // producer1.stop();
        //
        // Thread.sleep(2000);
        // // 退出Executor
        // service.shutdown();
    }

    /**
     * 生产者线程
     *
     * @author jackyuj
     */
    // public static class Producer implements Runnable {
    //
    // public Producer(BlockingQueue queue) {
    // this.queue = queue;
    // }
    //
    // @Override
    // public void run() {
    // String data = null;
    // Random r = new Random();
    //
    // System.out.println("启动生产者线程！");
    // try {
    // while (isRunning) {
    // System.out.println("正在生产数据...");
    // Thread.sleep(r.nextInt(DEFAULT_RANGE_FOR_SLEEP));
    //
    // data = "data:" + count.incrementAndGet();
    // System.out.println("将数据：" + data + "放入队列...");
    // if (!) {
    // System.out.println("放入数据失败：" + data);
    // }
    // }
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // Thread.currentThread().interrupt();
    // } finally {
    // System.out.println("退出生产者线程！");
    // }
    // }
    //
    // public void stop() {
    // isRunning = false;
    // }
    //
    // private volatile boolean isRunning = true;
    // private BlockingQueue queue;
    // private static AtomicInteger count = new AtomicInteger();
    // private static final int DEFAULT_RANGE_FOR_SLEEP = 1000;
    //
    // }

    public class Consumer implements Runnable {

        public Consumer(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        public void run() {
            System.out.println("启动消费者线程！");
            boolean isRunning = true;
            while (isRunning) {
                try {
                    System.out.println("正从队列获取数据...");
                    String data = queue.poll(2, TimeUnit.SECONDS);
                    if (null != data) {
                        System.out.println("拿到数据：" + data);
                        System.out.println("正在消费数据：" + data);
                        // Thread.sleep(r.nextInt(DEFAULT_RANGE_FOR_SLEEP));
                    } else {
                        // 超过2s还没数据，认为所有生产线程都已经退出，自动退出消费线程。
                        // isRunning = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                } finally {
                    System.out.println("退出消费者线程！");
                }
            }

        }

        private BlockingQueue<String> queue;
        private static final int DEFAULT_RANGE_FOR_SLEEP = 1000;
    }
}
