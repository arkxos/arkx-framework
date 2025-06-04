package org.ark.framework.extend.actions;


import com.arkxos.framework.extend.ExtendManager;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

/**
 * @class org.ark.framework.extend.actions.ExtendTag
 * 扩展标签
 * 
 * @author Darkness
 * @date 2012-8-7 下午9:33:32 
 * @version V1.0
 */
public class ExtendTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	private String id;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int doStartTag() throws JspException {
		ExtendManager.invoke(this.id, new Object[] { this.pageContext });
		return 0;
	}
}