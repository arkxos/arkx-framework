package com.arkxos.framework.queue2;

public class Message<T> {
	
	private String name;
	private String description;
	private T source;

	public Message(String name) {
		this.name = name;
	}
	
	public Message(String name, T source) {
		this.name = name;
		this.source = source;
	}
	
	public Message(String name, String description, T source) {
		this.name = name;
		this.description = description;
		this.source = source;
	}

	public T getSource() {
		return source;
	}
	
	public String name() {
		return this.name;
	}
	
	public String description() {
		return this.description;
	}
}
