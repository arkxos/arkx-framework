package com.arkxos.framework.cosyui.html;

import io.arkx.framework.commons.lang.FastStringBuilder;

/**
 * HTML中的指令
 * 
 */
public class HtmlInstruction extends HtmlNode {
	String instruction;

	public HtmlInstruction(String instruction) {
		this.instruction = instruction;
	}

	@Override
	public String getText() {
		return "";
	}

	@Override
	public int getType() {
		return HtmlNode.INSTRUCTION;
	}

	@Override
	void repack() {
		instruction = new String(instruction.toCharArray());
	}

	@Override
	public void format(FastStringBuilder sb, String prefix) {
		sb.append(instruction);
	}

	@Override
	public HtmlNode clone() {
		return new HtmlInstruction(instruction);
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

}
