package io.arkx.framework.queue;

import io.arkx.framework.queue2.Message;

public class UserDeleteEvent extends Message<User> {

	public UserDeleteEvent(User user) {
		super("UserDeleteEvent", user);
	}

}
