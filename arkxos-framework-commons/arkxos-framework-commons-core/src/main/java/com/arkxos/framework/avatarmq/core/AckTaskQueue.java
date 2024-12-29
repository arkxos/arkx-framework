package com.arkxos.framework.avatarmq.core;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.arkxos.framework.avatarmq.msg.ProducerAckMessage;

/**
 * @filename:AckTaskQueue.java
 * @description:AckTaskQueue功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class AckTaskQueue {

    private static ConcurrentLinkedQueue<ProducerAckMessage> ackQueue = new ConcurrentLinkedQueue<ProducerAckMessage>();

    public static boolean pushAck(ProducerAckMessage ack) {
        return ackQueue.offer(ack);
    }

    public static boolean pushAck(List<ProducerAckMessage> acks) {
        boolean flag = false;
        for (ProducerAckMessage ack : acks) {
            flag = ackQueue.offer(ack);
        }
        return flag;
    }

    public static ProducerAckMessage getAck() {
        return ackQueue.poll();
    }
}
