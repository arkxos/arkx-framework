package com.rapidark.framework.data.xml;

/**
 * 表示一个XML命名空间
 * 
 */
public class XMLNamespace {
	String prefix;

	public XMLNamespace(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}
}
