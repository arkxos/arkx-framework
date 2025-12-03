package io.arkx.framework.queue;

import io.arkx.framework.queue2.Message;
import io.arkx.framework.queue2.Subscribe;

public class OtherService {

    // private IBaseDAO otherDao;

    @Subscribe(events = {UserUpdateEvent.class, UserDeleteEvent.class})
    public void onUserUpdateOrDelete(Message<?> event) {
        if (event instanceof UserUpdateEvent) {
            this.onUserUpdate((UserUpdateEvent) event);
        } else if (event instanceof UserDeleteEvent) {
            this.onUserDelete((UserDeleteEvent) event);
        }
    }

    /**
     * 重写父类的方法，处理用户删除事件
     */
    @Subscribe(events = {UserUpdateEvent.class})
    public void onUserUpdate(UserUpdateEvent baseEvent) {
        // 如果本类只处理一个事件，这里就不需要再类型判断了
        // UserDeleteEvent event = (UserDeleteEvent)baseEvent;

        System.out.println("处理事件：" + baseEvent.getClass().getName());

        User user = baseEvent.getSource();
        System.out.println("更新用户：" + user);
    }

    @Subscribe(events = {UserDeleteEvent.class})
    public void onUserDelete(UserDeleteEvent baseEvent) {

        System.out.println("处理事件：" + baseEvent.getClass().getName());

        User user = baseEvent.getSource();
        System.out.println("删除用户：" + user);
        // otherDao.deleteOtherData(((User)baseEvent.getSource()).getId());
    }

}
