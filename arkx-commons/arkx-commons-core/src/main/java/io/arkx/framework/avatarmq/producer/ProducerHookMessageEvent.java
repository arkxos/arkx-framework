package io.arkx.framework.avatarmq.producer;

import io.arkx.framework.avatarmq.core.HookMessageEvent;

/**
 * @filename:ProducerHookMessageEvent.java
 * @description:ProducerHookMessageEvent功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class ProducerHookMessageEvent extends HookMessageEvent<String> {

	private boolean brokerConnect = false;

	private boolean running = false;

	public ProducerHookMessageEvent() {
		super();
	}

	public void disconnect(String addr) {
		synchronized (this) {
			if (isRunning()) {
				setBrokerConnect(false);
			}
		}
	}

	public boolean isBrokerConnect() {
		return brokerConnect;
	}

	public void setBrokerConnect(boolean brokerConnect) {
		this.brokerConnect = brokerConnect;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
