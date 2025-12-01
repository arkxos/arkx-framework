package org.ark.framework.jaf.tag;

import io.arkx.framework.commons.util.ObjectUtil;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import org.ark.framework.jaf.TagUtil;

import java.io.IOException;


/**
 * @class org.ark.framework.jaf.controls.ToolBarTag
 * <h2>工具栏标签，用来修饰&lt;ark:button></h2>
 * <br/>
 * <img src="images/ToolBarTag_1.png"/>
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:44:27 
 * @version V1.0
 */
public class ToolBarTag extends BodyTagSupport {
	
	private static final long serialVersionUID = 1L;
	
	private String theme;
	
	/**
	 * id
	 * @property id
	 * @type {String}
	 */
	private String id;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.theme = null;
		this.id = null;
	}

	public int doStartTag() throws JspException {
		try {
			if (ObjectUtil.empty(this.id)) {
				this.id = TagUtil.getTagID(this.pageContext, "ToolBar");
			}
			this.pageContext.getOut().print("<div class=\"z-toolbar");
			if ("flat".equals(this.theme)) {
				this.pageContext.getOut().print(" z-toolbar-flat");
			}
			this.pageContext.getOut().print("\"");
			this.pageContext.getOut().print(" id=\"" + this.id + "\"");
			this.pageContext.getOut().print(">");
			this.pageContext.getOut().print("<div class=\"z-toolbar-ct\">");
			this.pageContext.getOut().print("<div class=\"z-toolbar-overflow\">");
			this.pageContext.getOut().print("<div class=\"z-toolbar-nowrap\">");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 2;
	}

	public int doAfterBody() throws JspException {
		BodyContent body = getBodyContent();
		String content = body.getString().trim();
		try {
			getPreviousOut().write(content);
			getPreviousOut().write("</div></div></div></div>");
			getPreviousOut().print("<script>");
			getPreviousOut().print("Ark.Page.onReady(function(){new Ark.Toolbar('" + this.id + "');});");
			getPreviousOut().print("</script>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 6;
	}

	public String getTheme() {
		return this.theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}
}