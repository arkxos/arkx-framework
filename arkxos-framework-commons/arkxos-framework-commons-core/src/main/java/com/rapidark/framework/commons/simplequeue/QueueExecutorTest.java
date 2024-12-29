package com.rapidark.framework.commons.simplequeue;

/**
 * @author Darkness
 * @date 2017年7月15日 下午4:27:13
 * @version 1.0
 * @since 1.0 
 */
public class QueueExecutorTest {

	public static void main(String[] args) {
		QueueExecutor queueExecutor = QueueExecutor.create("test-queue", new ElementProcessor() {
			
			@Override
			public Config getConfig() {
				return Config.me().setSleepTime(100);
			}
			
			@Override
			public ExecuteResult execute(ElementWarpper elementWarpper, Task task) {
//				System.out.println(elementWarpper.get());
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return new ExecuteResult();
			}
		});
		
		for (int i = 0; i < 100_0000; i++) {
			queueExecutor.addElement("element" + i);
		}
		
		queueExecutor.thread(20).printPercent().run();
		
		System.out.println("========== finished ==========");
	}
}
