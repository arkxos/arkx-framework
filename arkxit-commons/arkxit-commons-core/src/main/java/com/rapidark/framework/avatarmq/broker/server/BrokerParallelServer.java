package com.rapidark.framework.avatarmq.broker.server;

import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.rapidark.framework.avatarmq.broker.AckPullMessageController;
import com.rapidark.framework.avatarmq.broker.AckPushMessageController;
import com.rapidark.framework.avatarmq.broker.SendMessageController;
import com.rapidark.framework.avatarmq.netty.NettyClustersConfig;

/**
 * @filename:BrokerParallelServer.java
 * @description:BrokerParallelServer功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class BrokerParallelServer implements RemotingServer {

    protected int parallel = NettyClustersConfig.getWorkerThreads();
    protected ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(parallel));
    protected ExecutorCompletionService<Void> executorService;

    public BrokerParallelServer() {

    }

    public void init() {
        executorService = new ExecutorCompletionService<Void>(executor);
    }

    public void start() {
        for (int i = 0; i < parallel; i++) {
            executorService.submit(new SendMessageController());
            executorService.submit(new AckPullMessageController());
            executorService.submit(new AckPushMessageController());
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
