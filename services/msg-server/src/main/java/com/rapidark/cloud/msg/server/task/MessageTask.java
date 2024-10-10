package com.rapidark.cloud.msg.server.task;

import com.rapidark.cloud.msg.client.model.BaseMessage;
import com.rapidark.cloud.msg.server.exchanger.MessageExchanger;

import java.util.concurrent.Callable;

/**
 * @author woodev
 */
public class MessageTask implements Callable<Boolean> {
    private MessageExchanger exchanger;

    private BaseMessage notify;

    public MessageTask(MessageExchanger exchanger, BaseMessage notify) {
        this.exchanger = exchanger;
        this.notify = notify;
    }

    @Override
    public Boolean call() {
        return exchanger.exchange(notify);
    }
}
