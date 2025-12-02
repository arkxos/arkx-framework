package org.ark.framework.jaf.controls;

import org.ark.framework.jaf.tag.CheckboxTag;

import jakarta.servlet.jsp.PageContext;

/**
 * @class org.ark.framework.jaf.controls.RadioTag
 * @author Darkness
 * @date 2013-1-31 下午12:43:52
 * @version V1.0
 */
public class RadioTag extends CheckboxTag {

	private static final long serialVersionUID = 1L;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.type = "radio";
	}

}
