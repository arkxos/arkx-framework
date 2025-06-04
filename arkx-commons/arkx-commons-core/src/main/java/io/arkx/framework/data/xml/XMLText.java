package io.arkx.framework.data.xml;

import io.arkx.framework.commons.lang.FastStringBuilder;

/**
 * 表示一个XML文本块
 * 
 */
public final class XMLText extends XMLNode {
	String text;

	public XMLText(String text) {
		this.text = text;
	}

	@Override
	public void toString(String prefix, FastStringBuilder sb) {
		if (text != null) {
			encode(text, sb);
		}
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public int getType() {
		return XMLNode.TEXT;
	}

	@Override
	void repack() {
		text = new String(text.toCharArray());
	}

	public void setText(String text) {
		this.text = text;
	}

}
