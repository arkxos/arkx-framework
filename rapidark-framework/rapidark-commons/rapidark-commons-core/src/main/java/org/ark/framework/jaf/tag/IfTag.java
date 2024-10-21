package org.ark.framework.jaf.tag;

import java.io.IOException;
import java.io.StringReader;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.ark.framework.jaf.PlaceHolder;
import org.ark.framework.jaf.PlaceHolderContext;
import org.ark.framework.jaf.expression.ExpressionParser;
import org.ark.framework.jaf.expression.Primitives;


/**
 * @class org.ark.framework.jaf.tag.IfTag 
 * if标签
 * &lt;ark:if condition="!${ID}">&lt;/ark:if>
 * @author Darkness
 * @date 2013-1-31 下午12:52:05
 * @version V1.0
 */
public class IfTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;
	
	public static final String IfTagInAttribute = "_IF_TAG_FALSE";
	public static final String IfParentTagInAttribute = "_IF_PARENT_TAG_FALSE";
	
	/**
	 * 条件
	 * @property condition
	 * @type {String}
	 */
	private String condition;
	private String out;
	private boolean pass;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.condition = null;
		this.out = null;
		this.pass = false;
	}

	public int doStartTag() throws JspException {
		ExpressionParser ep = new ExpressionParser(new StringReader(this.condition));
		PlaceHolderContext context = PlaceHolderContext.getInstance(this, this.pageContext);
		ep.setContext(context);
		Object obj = null;
		try {
			obj = ep.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		this.pass = Primitives.getBoolean(obj);
		if (this.pass) {
			if (this.out != null) {
				try {
					if (this.out.startsWith("${")) {
						PlaceHolder holder = new PlaceHolder(this.out);
						this.out = String.valueOf(context.eval(holder));
					}
					this.pageContext.getOut().print(this.out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return EVAL_BODY_AGAIN;
		}
		this.pageContext.setAttribute(IfTagInAttribute, this);
		this.pageContext.setAttribute(IfParentTagInAttribute, getParent());
		return Tag.SKIP_BODY;
	}

	public boolean isPass() {
		return this.pass;
	}

	public int doAfterBody() throws JspException {
		BodyContent body = getBodyContent();
		String content = body.getString().trim();
		try {
			getPreviousOut().write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return TagSupport.EVAL_PAGE;
	}

	public String getCondition() {
		return this.condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getOut() {
		return this.out;
	}

	public void setOut(String out) {
		this.out = out;
	}
}