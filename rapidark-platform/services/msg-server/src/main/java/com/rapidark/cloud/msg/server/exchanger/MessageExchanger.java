package com.rapidark.cloud.msg.server.exchanger;

import com.rapidark.cloud.msg.client.model.BaseMessage;

/**
 * @author woodev
 */

public interface MessageExchanger {
    boolean support(Object message);

    boolean exchange(BaseMessage message);
}
