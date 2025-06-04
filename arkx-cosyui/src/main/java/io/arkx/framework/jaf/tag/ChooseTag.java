package org.ark.framework.jaf.tag;

import java.io.IOException;
import java.io.StringReader;

import org.ark.framework.jaf.PlaceHolderContext;
import org.ark.framework.jaf.expression.ExpressionParser;
import org.ark.framework.jaf.expression.ParseException;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;


/**
 * @class org.ark.framework.jaf.tag.ChooseTag
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:51:23 
 * @version V1.0
 */
public class ChooseTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	private String variable;
	private Object value;
	private boolean matched;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.variable = null;
		this.matched = false;
	}

	public int doStartTag() throws JspException {
		ExpressionParser ep = new ExpressionParser(new StringReader(this.variable));
		PlaceHolderContext context = PlaceHolderContext.getInstance(this, this.pageContext);
		ep.setContext(context);
		try {
			this.value = ep.execute();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 2;
	}

	public int doAfterBody() throws JspException {
		BodyContent body = getBodyContent();
		String content = body.getString().trim();
		try {
			getPreviousOut().write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 6;
	}

	public String getVariable() {
		return this.variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public Object getValue() {
		return this.value;
	}

	public boolean isMatched() {
		return this.matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}
}