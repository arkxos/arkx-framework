package io.arkx.framework.queue.saga;

import io.arkx.framework.queue2.Message;

/**
 * 电话号码流程事件
 *
 * @author Darkness
 * @date 2014-12-17 下午9:37:35
 * @version V1.0
 * @since ark 1.0
 */
public abstract class PhoneNumberProcessEvent extends Message<String> {

	public PhoneNumberProcessEvent(String aProcessId) {
		super("PhoneNumberProcessEvent", aProcessId);
	}

	public String processId() {
		return this.getSource();
	}

}
