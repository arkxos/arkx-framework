package com.arkxos.framework.queue;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.arkxos.framework.queue2.MessageBus;

public class MessageBusTest {

	@Test
	public void publish() {

		MessageBus eventController = new MessageBus();
		
		System.out.println("==============publish================");
		UserSerivce userSerivceImpl = new UserSerivce();
		OtherService otherServiceImpl = new OtherService();

		MessageBus.globalInstance().register(userSerivceImpl);
		MessageBus.globalInstance().register(otherServiceImpl);

		userSerivceImpl.updateUser(new User("sky", "堕落天使"));

		userSerivceImpl.deleteUser("darkness");
		System.out.println("==============publish================");
	}
	
	@Test
	public void registerAndRemove() {
		MessageBus eventController = new MessageBus();
		System.out.println("==============registerAndRemove================");
		UserSerivce userSerivceImpl = new UserSerivce();
		OtherService otherServiceImpl = new OtherService();

		MessageBus.globalInstance().register(userSerivceImpl);
		MessageBus.globalInstance().unregister(userSerivceImpl);
		
		MessageBus.globalInstance().register(otherServiceImpl);
		MessageBus.globalInstance().unregister(otherServiceImpl);
		
		userSerivceImpl.updateUser(new User("sky", "堕落天使"));

		userSerivceImpl.deleteUser("darkness");
		System.out.println("==============registerAndRemove================");
	}

	@Test
	public void iocAutoRegiste() throws Exception {
		System.out.println("==============iocAutoRegiste================");
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("ioc-event.xml");
		UserSerivce userSerivce = (UserSerivce) applicationContext.getBean("userSerivce");

		userSerivce.updateUser(new User("sky", "堕落天使"));

		userSerivce.deleteUser("darkness");
		System.out.println("==============iocAutoRegiste================");
	}
}
