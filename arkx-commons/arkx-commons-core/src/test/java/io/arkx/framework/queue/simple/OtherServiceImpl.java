package io.arkx.framework.queue.simple;

import io.arkx.framework.queue2.Message;
import io.arkx.framework.queue2.Subscribe;

public class OtherServiceImpl {
    // private IBaseDAO otherDao;

    @Subscribe(eventNames = {"deleteUserEvent", "updateUserEvent"})
    public void onUserUpdateOrDelete(Message<User> event) {
        if ("updateUserEvent".equals(event.name())) {
            this.onUserUpdate(event);
        } else if ("deleteUserEvent".equals(event.name())) {
            this.onUserDelete(event);
        }
    }

    /**
     * 重写父类的方法，处理用户删除事件
     */
    @Subscribe(eventNames = {"updateUserEvent"})
    public void onUserUpdate(Message<User> event) {
        // 如果本类只处理一个事件，这里就不需要再类型判断了
        // UserDeleteEvent event = (UserDeleteEvent)baseEvent;

        System.out.println("处理事件：" + event.getClass().getName());

        User user = event.getSource();
        System.out.println("更新用户：" + user);
    }

    @Subscribe(eventNames = {"deleteUserEvent"})
    public void onUserDelete(Message<User> baseEvent) {

        System.out.println("处理事件：" + baseEvent.getClass().getName());

        User user = baseEvent.getSource();
        System.out.println("删除用户：" + user);
        // otherDao.deleteOtherData(((User)baseEvent.getSource()).getId());
    }

}
