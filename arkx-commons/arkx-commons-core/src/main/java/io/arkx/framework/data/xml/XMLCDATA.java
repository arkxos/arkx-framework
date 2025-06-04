package io.arkx.framework.data.xml;

import io.arkx.framework.commons.lang.FastStringBuilder;

/**
 * 表示XML中一个CDATA块
 * 
 */
public final class XMLCDATA extends XMLNode {
	String text;

	public XMLCDATA(String text) {
		this.text = text;
	}

	@Override
	public void toString(String prefix, FastStringBuilder sb) {
		sb.append(prefix).append("<![CDATA[").append(text == null ? "" : text).append("]]>");
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public int getType() {
		return XMLNode.CDATA;
	}

	@Override
	void repack() {
		text = new String(text.toCharArray());
	}

	public void setText(String text) {
		this.text = text;
	}

}
