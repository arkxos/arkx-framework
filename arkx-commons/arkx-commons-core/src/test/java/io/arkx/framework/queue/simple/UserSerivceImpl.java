package io.arkx.framework.queue.simple;

import io.arkx.framework.queue2.Message;
import io.arkx.framework.queue2.MessageBus;

public class UserSerivceImpl {

    public void deleteUser(String id) {
        // 先发布删除事件
        MessageBus.globalInstance().publish(new Message<>("deleteUserEvent", new User("darkness", "堕落天使")));
    }

    public void updateUser(User user) {
        MessageBus.globalInstance().publish(new Message<>("updateUserEvent", user));
    }
}
