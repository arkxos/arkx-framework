package io.arkx.framework.queue;

import io.arkx.framework.queue2.MessageBus;

public class UserSerivce {
	
	public void deleteUser(String id) {
		// 先发布删除事件
		MessageBus.globalInstance().publish(new UserDeleteEvent(new User("darkness", "堕落天使")));
		// 再把用户删除
		// userDao.deleteUser(id);
	}
	
	public void updateUser(User user) {
		MessageBus.globalInstance().publish(new UserUpdateEvent(user));
	}
}
