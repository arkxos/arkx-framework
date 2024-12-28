package com.rapidark.framework.avatarmq.core;

import java.util.concurrent.Callable;
import java.util.concurrent.Phaser;

import com.rapidark.framework.avatarmq.broker.SendMessageLauncher;
import com.rapidark.framework.avatarmq.consumer.ClustersState;
import com.rapidark.framework.avatarmq.consumer.ConsumerContext;
import com.rapidark.framework.avatarmq.model.MessageDispatchTask;
import com.rapidark.framework.avatarmq.model.MessageSource;
import com.rapidark.framework.avatarmq.model.MessageType;
import com.rapidark.framework.avatarmq.model.RemoteChannelData;
import com.rapidark.framework.avatarmq.model.RequestMessage;
import com.rapidark.framework.avatarmq.model.ResponseMessage;
import com.rapidark.framework.avatarmq.msg.ConsumerAckMessage;
import com.rapidark.framework.avatarmq.msg.Message;
import com.rapidark.framework.avatarmq.netty.NettyUtil;

/**
 * @filename:SendMessageTask.java
 * @description:SendMessageTask功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class SendMessageTask implements Callable<Void> {

    private MessageDispatchTask[] tasks;
    private Phaser phaser = null;
    private SendMessageLauncher launcher = SendMessageLauncher.getInstance();

    public SendMessageTask(Phaser phaser, MessageDispatchTask[] tasks) {
        this.phaser = phaser;
        this.tasks = tasks;
    }

    public Void call() throws Exception {
        for (MessageDispatchTask task : tasks) {
            Message msg = task.getMessage();

            if (ConsumerContext.selectByClusters(task.getClusters()) != null) {
                RemoteChannelData channel = ConsumerContext.selectByClusters(task.getClusters()).nextRemoteChannelData();

                ResponseMessage response = new ResponseMessage();
                response.setMsgSource(MessageSource.AvatarMQBroker);
                response.setMsgType(MessageType.AvatarMQMessage);
                response.setMsgParams(msg);
                response.setMsgId(new MessageIdGenerator().generate());

                try {
                    if (!NettyUtil.validateChannel(channel.getChannel())) {
                        ConsumerContext.setClustersStat(task.getClusters(), ClustersState.NETWORKERR);
                        continue;
                    }

                    RequestMessage request = (RequestMessage) launcher.launcher(channel.getChannel(), response);

                    ConsumerAckMessage result = (ConsumerAckMessage) request.getMsgParams();

                    if (result.getStatus() == ConsumerAckMessage.SUCCESS) {
                        ConsumerContext.setClustersStat(task.getClusters(), ClustersState.SUCCESS);
                    }
                } catch (Exception e) {
                    ConsumerContext.setClustersStat(task.getClusters(), ClustersState.ERROR);
                }
            }
        }
        phaser.arriveAndAwaitAdvance();
        return null;
    }
}
