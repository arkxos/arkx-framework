package org.ark.framework.messages;


import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.data.jdbc.Transaction;

/**
 * @class org.ark.framework.messages.Message
 * 
 * @author Darkness
 * @date 2013-1-31 上午11:30:35 
 * @version V1.0
 */
public class Message {
	private String ID;
	private String name;
	private Transaction transaction;
	private Mapx<String, Object> content;

	public Mapx<String, Object> getContent() {
		return this.content;
	}

	public void setContent(Mapx<String, Object> content) {
		this.content = content;
	}

	public Transaction getTransaction() {
		return this.transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public boolean isTransactional() {
		return this.transaction != null;
	}

	public String getID() {
		return this.ID;
	}

	public void setID(String id) {
		this.ID = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}