package org.ark.framework.messages;


import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.commons.collection.Mapx;
import com.arkxos.framework.data.jdbc.Transaction;

/**
 * @class org.ark.framework.messages.MessageSender
 * 
 * @author Darkness
 * @date 2013-1-31 上午11:31:02 
 * @version V1.0
 */
public class MessageSender {
	private Mapx<String, Object> map = new Mapx<String, Object>();

	private ArrayList<Mapx<String, Object>> list = new ArrayList<Mapx<String, Object>>();
	private Transaction tran;
	private static long id = System.currentTimeMillis();
	private String messageName;
	private Message message;

	public void addContentVar(String varName, Object value) {
		this.map.put(varName, value);
	}

	public void setTransaction(Transaction tran) {
		this.tran = tran;
	}

	public void send() {
		Message msg = new Message();
		msg.setContent(this.map);
		msg.setTransaction(this.tran);
		msg.setName(getMessageName());
		msg.setID(getMessageName() + id++);
		this.message = msg;
		MessageBus.send(this);
	}

	public Message getMessage() {
		return this.message;
	}

	public void receiveFeedback(Mapx<String, Object> fmap) {
		this.list.add(fmap);
	}

	public List<Mapx<String, Object>> getFeedback() {
		return this.list;
	}

	public String getMessageName() {
		return this.messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}
}