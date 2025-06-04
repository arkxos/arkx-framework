package io.arkx.framework.queue.simple;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EventTest {

	@Test
	public void iocAutoRegiste() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("ioc-event-simple.xml");
		UserSerivceImpl userSerivce = (UserSerivceImpl) applicationContext.getBean("userSerivceImpl");

		userSerivce.updateUser(new User("sky", "堕落天使"));

		userSerivce.deleteUser("darkness");
	}
}
