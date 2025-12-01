package io.arkx.framework.avatarmq.broker;

import io.arkx.framework.avatarmq.core.AckMessageCache;
import io.arkx.framework.avatarmq.core.MessageSystemConfig;

import java.util.concurrent.Callable;

/**
 * @filename:AckPushMessageController.java
 * @description:AckPushMessageController功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class AckPushMessageController implements Callable<Void> {

    private volatile boolean stoped = false;

    public Void call() {
        AckMessageCache ref = AckMessageCache.getAckMessageCache();
        int timeout = MessageSystemConfig.AckMessageControllerTimeOutValue;
        while (!stoped) {
            if (ref.hold(timeout)) {
                ref.commit();
            }
        }
        return null;
    }

    public void stop() {
        stoped = true;
    }

    public boolean isStoped() {
        return stoped;
    }
}
