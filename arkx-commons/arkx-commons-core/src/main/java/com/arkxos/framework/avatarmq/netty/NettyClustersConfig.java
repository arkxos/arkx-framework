package com.arkxos.framework.avatarmq.netty;

import com.arkxos.framework.avatarmq.core.MessageSystemConfig;

/**
 * @filename:NettyClustersConfig.java
 * @description:NettyClustersConfig功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class NettyClustersConfig {

    private int clientSocketSndBufSize = MessageSystemConfig.SocketSndbufSize;
    private int clientSocketRcvBufSize = MessageSystemConfig.SocketRcvbufSize;
    private static int workerThreads = Runtime.getRuntime().availableProcessors() * 2;

    public static int getWorkerThreads() {
        return workerThreads;
    }

    public static void setWorkerThreads(int workers) {
        workerThreads = workers;
    }

    public int getClientSocketSndBufSize() {
        return clientSocketSndBufSize;
    }

    public void setClientSocketSndBufSize(int clientSocketSndBufSize) {
        this.clientSocketSndBufSize = clientSocketSndBufSize;
    }

    public int getClientSocketRcvBufSize() {
        return clientSocketRcvBufSize;
    }

    public void setClientSocketRcvBufSize(int clientSocketRcvBufSize) {
        this.clientSocketRcvBufSize = clientSocketRcvBufSize;
    }
}
